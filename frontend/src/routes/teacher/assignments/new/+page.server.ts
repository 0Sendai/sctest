import { fail, redirect, error } from '@sveltejs/kit';
import type { InferInsertModel } from 'drizzle-orm';

import { assignments, slides, assignmentResults, users } from '$lib/server/db/schema';
import { eq } from 'drizzle-orm';

type Slide = {
    type: string;
    question: string;
    variants: string[];
    correctVariantIndex: number;
};

type SlidesJson = Slide[];

type AssignmentDb = {
    title: string;
    author_id: number;
};

type SlideDb = InferInsertModel<typeof slides>;

function checkForString(variable: any): boolean {
    if (typeof variable !== 'string')
        return false;
    return true;
}

async function parseForm(form: FormData): Promise<[string, SlideDb[]] | null> {
    const title = form.get('title');
    if (!checkForString(title)) {
        console.log("title is not a string");
        return null;
    }

    const slides_json = form.get('slides');
    if (!checkForString(slides_json)) {
        console.log("slides is not a string");
        return null;
    }

    let slidesJson: SlidesJson;
    try {
        slidesJson = JSON.parse(slides_json as string) as SlidesJson;
    } catch (error) {
        console.log("can't parse json");
        return null;
    }
    console.log("slidesJson: ", slidesJson);
    if (!slidesJson || slidesJson.length === 0) {
        console.log("bad slidesJson after parsing json");
        return null;
    }

    let slideDb: SlideDb[] = [];
    for (let index = 0; index < slidesJson.length; index++) {
        const slide = slidesJson[index];
        if (!slide || !slide.type || !(typeof slide.type === 'string')) {
            console.log("bad slide type");
            return null;
        }
        if (!checkForString(slide.question)) {
            console.log("bad slide question");
            return null;
        }
        
        if (slide.type === 'text') {
            
            slideDb.push({
                assignment_id: '',
                type: slide.type,
                position: index,
                content: {
                    question: slide.question,
                },
            });
        } else if (slide.type === 'quiz') {
            if (!slide.variants || slide.variants.length === 0) {
                console.log("bad variants");
                return null;
            }
            if (typeof slide.correctVariantIndex !== 'number' ||
                slide.correctVariantIndex < 0 ||
                slide.correctVariantIndex >= slide.variants.length) {
                console.log("bad correct variant index");
                return null;
            }
            slideDb.push({
                assignment_id: '',
                type: slide.type,
                position: index,
                content: {
                    question: slide.question,
                    variants: slide.variants,
                    correctVariantIndex: slide.correctVariantIndex,
                },
            });
        } else {
            console.log("unknown type");
            return null;
        }
    }

    return [title as string, slideDb]; 
}

export async function load({ locals }) {
    if (!locals.user?.id) {
        console.log('teacher/assignments/new bad user id');
        throw error(500, 'bad user id');
    }

    const response = await fetch(
        'http://127.0.0.1:8080/api/teacher/assignments/create'
    );
    if (!response.ok) {
        throw error(500, "can't load students");
    }
    const data = await response.json();
    return { ...data };
    // const students = await locals.db.select({
    //     id: users.id,
    //     name: users.name,
    // }).from(users)
    //   .where(eq(users.role, 'student'))
    //   .orderBy(users.name);

    // return { students: students };
}

export const actions = {
    default: async ({ request, locals }) => {
        const form = await request.formData();
        //const data: TextAnswer | Quiz | null = await parseForm(form);
        // if (!data) {
        //     console.log("error while parsing ");
        //if (!validateData(data))
        if (!form) {
            return fail(406, {error: 'empty question or variants'});
        }

        console.log(form)
        const parsedForm = await parseForm(form);
        if (!parsedForm) {
            console.log("parse form error");
            return fail(500, {error: 'parse form error'});
        }
        const [title, slideDb] = parsedForm;

        console.log(`title: ${title}`);
        console.log('slideDb: ', slideDb);


        console.log("inserting into db");
        if (!locals.user) {
            console.log("no locals.user");
            return fail(500, {error: "no locals.user"});
        }
        const user = locals.user;

        // Parse selected student IDs from JSON
        const studentIdsJson = form.get('studentIdsJson');
        let studentIds: number[] = [];
        if (studentIdsJson && typeof studentIdsJson === 'string') {
            try {
                studentIds = JSON.parse(studentIdsJson);
                if (!Array.isArray(studentIds)) {
                    studentIds = [];
                }
            } catch (e) {
                console.log('Failed to parse studentIdsJson:', e);
                studentIds = [];
            }
        }
        console.log(`Selected student IDs:`, studentIds);

        try {
            await locals.db.transaction(async (tx) => {
                let [assignment] = await tx.insert(assignments)
                    .values({
                        title: title,
                        author_id: user.id,
                    }).returning({id: assignments.id});

                if (typeof assignment.id !==  'string' ) {
                    console.log("bad assignment_id");
                    tx.rollback();
                    return;
                }
                slideDb.forEach((slide: SlideDb) => {slide.assignment_id = assignment.id});
                await tx.insert(slides).values(slideDb);

                // Create assignment results for selected students
                if (studentIds.length > 0) {
                    const assignmentResultsToInsert = studentIds.map(studentId => ({
                        studentId,
                        assignmentId: assignment.id,
                        status: 'in_progress' as const,
                    }));
                    await tx.insert(assignmentResults).values(assignmentResultsToInsert);
                    console.log(`Created ${studentIds.length} assignment results`);
                }
            })
        } catch (error) {
            console.log("db insertion error: ", error);
            return fail(500, {error: "db insertion error"});
        }

        console.log("assignment successfully inserted!");
        redirect(303, '/teacher/assignments');
        
    },
};
