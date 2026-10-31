import { fail, redirect, error } from '@sveltejs/kit';
import type { InferInsertModel, InferSelectModel } from 'drizzle-orm';

import { assignments, slides, assignmentResults, users } from '$lib/server/db/schema';
import { eq, and, asc, inArray, ne } from 'drizzle-orm';

type Slide = {
    type: string;
    question: string;
    variants?: string[];
    correctVariantIndex?: number;
};

type SlidesJson = Slide[];

type SlideDb = InferInsertModel<typeof slides>;
type AssignmentDb = InferSelectModel<typeof assignments>;

export async function load({ params, locals }) {
    const assignmentId = params.assignment_id;
    const response = await fetch(
        "http://localhost:8080/api/teacher/" + 
        `assignments/edit?id=${assignmentId}`
    );
    if (!response.ok)
        throw error(500, "bad response from server");
    const data = await response.json();
    console.log(data);
    return { ...data.data };
}

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

export const actions = {
    default: async ({ request, locals, params }) => {
        const assignmentId = params.assignment_id;
        if (!assignmentId || typeof assignmentId !== 'string') {
            return fail(400, { error: 'Invalid assignment ID' });
        }

        const form = await request.formData();
        if (!form) {
            return fail(406, { error: 'empty question or variants' });
        }

        const parsedForm = await parseForm(form);
        if (!parsedForm) {
            console.log("parse form error");
            return fail(500, { error: 'parse form error' });
        }
        const [title, slideDb] = parsedForm;

        // Parse selected student IDs from JSON
        const studentIdsJson = form.get('studentIdsJson');
        let newStudentIds: number[] = [];
        if (studentIdsJson && typeof studentIdsJson === 'string') {
            try {
                newStudentIds = JSON.parse(studentIdsJson);
                if (!Array.isArray(newStudentIds)) {
                    newStudentIds = [];
                }
            } catch (e) {
                console.log('Failed to parse studentIdsJson:', e);
                newStudentIds = [];
            }
        }
        console.log(`New selected student IDs:`, newStudentIds);

        if (!locals.user) {
            console.log("no locals.user");
            return fail(500, { error: "no locals.user" });
        }

        try {
            const userId = locals.user.id;
            await locals.db.transaction(async (tx) => {
                // Verify assignment belongs to current user
                const [existingAssignment] = await tx.select()
                    .from(assignments)
                    .where(and(
                        eq(assignments.id, assignmentId),
                        eq(assignments.author_id, userId)
                    ))
                    .limit(1);

                if (!existingAssignment) {
                    console.log("assignment not found or not owned by user");
                    return;
                }

                // Update assignment title
                await tx.update(assignments)
                    .set({ title, updated_at: new Date() })
                    .where(eq(assignments.id, assignmentId));

                // Delete existing slides and insert new ones
                await tx.delete(slides)
                    .where(eq(slides.assignment_id, assignmentId));

                if (slideDb.length > 0) {
                    slideDb.forEach((slide: SlideDb) => { slide.assignment_id = assignmentId; });
                    await tx.insert(slides).values(slideDb);
                }

                // Sync student assignments
                // Remove assignments for students no longer selected
                if (newStudentIds.length > 0) {
                    await tx.delete(assignmentResults)
                        .where(and(
                            eq(assignmentResults.assignmentId, assignmentId),
                            ne(assignmentResults.studentId, -1) // always true, but needed for type
                        ));
                    // Actually, let's be more precise
                }
                
                // Delete all existing assignment results for this assignment
                await tx.delete(assignmentResults)
                    .where(eq(assignmentResults.assignmentId, assignmentId));

                // Insert new assignment results
                if (newStudentIds.length > 0) {
                    const assignmentResultsToInsert = newStudentIds.map(studentId => ({
                        studentId,
                        assignmentId,
                        status: 'in_progress' as const,
                    }));
                    await tx.insert(assignmentResults).values(assignmentResultsToInsert);
                    console.log(`Created ${newStudentIds.length} assignment results`);
                }
            });
        } catch (error) {
            console.log("db update error: ", error);
            return fail(500, { error: "db update error" });
        }

        console.log("assignment successfully updated!");
        redirect(303, '/teacher/assignments');
    },
};
