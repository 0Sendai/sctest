import { fail, redirect } from '@sveltejs/kit';
import { eq, asc, and } from 'drizzle-orm';
import { db } from '$lib/server/db/index';
import { assignments, slides, assignmentResults, slideResults, users, slideComments } from '$lib/server/db/schema';
import { type Actions } from './$types';

export async function load({ params, locals }) {
    if (!locals.user?.id) {
        return fail(401, { error: 'Unauthorized' });
    }

    // Проверяем роль преподавателя
    if (locals.user.role !== 'teacher' && locals.user.role !== 'admin') {
        return fail(403, { error: 'Forbidden' });
    }

    const studentId = params.student_id;
    const assignmentId = params.assignment_id;

    // Проверяем, что студент существует
    const student = await locals.db
        .select({
            id: users.id,
            name: users.name,
            role: users.role,
        })
        .from(users)
        .where(eq(users.id, parseInt(studentId)))
        .limit(1);

    if (student.length === 0) {
        return fail(404, { error: 'Student not found' });
    }

    // Получаем задание
    const assignmentData = await locals.db
        .select({
            id: assignments.id,
            title: assignments.title,
            authorId: assignments.author_id,
            createdAt: assignments.created_at,
            updatedAt: assignments.updated_at,
        })
        .from(assignments)
        .where(eq(assignments.id, assignmentId))
        .limit(1);

    if (assignmentData.length === 0) {
        return fail(404, { error: 'Assignment not found' });
    }

    const assignment = assignmentData[0];

    // Получаем результат студента по этому заданию
    const resultData = await locals.db
        .select()
        .from(assignmentResults)
        .where(
            and(
                eq(assignmentResults.studentId, parseInt(studentId)),
                eq(assignmentResults.assignmentId, assignmentId)
            )
        )
        .limit(1);

    if (resultData.length === 0) {
        // Студент не проходил это задание
        return {
            student: student[0],
            assignment: {
                ...assignment,
                authorName: assignment.authorId,
            },
            result: null,
            slides: [],
            slideResults: [],
        };
    }

    const result = resultData[0];

    // Получаем все слайды задания
    const slidesData = await locals.db
        .select()
        .from(slides)
        .where(eq(slides.assignment_id, assignmentId))
        .orderBy(asc(slides.position));

    // Получаем результаты студента по слайдам
    const slideResultsData = await locals.db
        .select()
        .from(slideResults)
        .where(eq(slideResults.result_id, result.id));

    // Создаем маппинг slideId -> slideResult
    const slideResultsMap = new Map<number, typeof slideResultsData[0]>();
    for (const sr of slideResultsData) {
        slideResultsMap.set(sr.slide_id, sr);
    }

    // Формируем данные для отображения с ответами студента
    const slidesWithResults = slidesData.map((slide: typeof slidesData[0]) => {
        const slideResult = slideResultsMap.get(slide.id);
        return {
            id: slide.id,
            type: slide.type,
            position: slide.position,
            content: slide.content,
            studentAnswer: slideResult?.answer ?? null,
            isCorrect: slideResult?.is_correct ?? null,
            answeredAt: slideResult?.answered_at ?? null,
        };
    });

    // Считаем статистику
    const totalSlides = slidesData.length;
    const answeredSlides = slideResultsData.length;
    const correctAnswers = slideResultsData.filter((sr: typeof slideResultsData[0]) => sr.is_correct === true).length;
    const incorrectAnswers = slideResultsData.filter((sr: typeof slideResultsData[0]) => sr.is_correct === false).length;

    // Получаем комментарии преподавателя к слайдам
    const commentsData = await locals.db
        .select()
        .from(slideComments)
        .where(
            and(
                eq(slideComments.assignment_id, assignmentId),
                eq(slideComments.student_id, parseInt(studentId))
            )
        );

    // Создаем маппинг slideId -> comment
    const commentsMap = new Map<number, string>();
    for (const c of commentsData) {
        commentsMap.set(c.slide_id, c.comment || '');
    }

    return {
        student: student[0],
        assignment: {
            ...assignment,
            authorName: assignment.authorId,
        },
        result: {
            id: result.id,
            status: result.status,
            startedAt: result.startedAt,
            completedAt: result.completedAt,
            currentSlideId: result.currentSlideId,
        },
        slides: slidesWithResults,
        slideResults: slideResultsData,
        slideComments: commentsMap,
        stats: {
            total: totalSlides,
            answered: answeredSlides,
            correct: correctAnswers,
            incorrect: incorrectAnswers,
            unanswered: totalSlides - answeredSlides,
        },
    };
}

export const actions: Actions = {
    default: async ({ request, locals }) => {
        if (!locals.user?.id) {
            return fail(401, { error: 'Unauthorized' });
        }

        if (locals.user.role !== 'teacher' && locals.user.role !== 'admin') {
            return fail(403, { error: 'Forbidden' });
        }

        const formData = await request.formData();
        const assignmentId = formData.get('assignmentId') as string;
        const studentId = parseInt(formData.get('studentId') as string);
        const commentsJson = formData.get('comments') as string;
        const correctnessesJson = formData.get('correctnesses') as string;

        if (!assignmentId || !studentId) {
            return fail(400, { error: 'Missing required fields' });
        }

        try {
            // Проверяем, пришли ли комментарии как JSON (массовое сохранение)
            if (commentsJson) {
                const comments: Array<{ slide_id: number; comment: string }> = JSON.parse(commentsJson);

                // Сохраняем все комментарии
                for (const c of comments) {
                    await db
                        .insert(slideComments)
                        .values({
                            assignment_id: assignmentId,
                            slide_id: c.slide_id,
                            student_id: studentId,
                            comment: c.comment || null,
                        })
                        .onConflictDoUpdate({
                            target: [slideComments.assignment_id, slideComments.student_id, slideComments.slide_id],
                            set: {
                                comment: c.comment || null,
                            },
                        });
                }
            }

            // Проверяем, пришли ли отметки правильности
            const updatedSlides: Array<{ slide_id: number; is_correct: boolean }> = [];
            if (correctnessesJson) {
                const correctnesses: Array<{ slide_id: number; is_correct: boolean }> = JSON.parse(correctnessesJson);

                // Получаем resultId для этого студента и задания
                const resultData = await db
                    .select({ id: assignmentResults.id })
                    .from(assignmentResults)
                    .where(
                        and(
                            eq(assignmentResults.studentId, studentId),
                            eq(assignmentResults.assignmentId, assignmentId)
                        )
                    )
                    .limit(1);

                if (resultData.length > 0) {
                    const resultId = resultData[0].id;

                    // Обновляем is_correct для каждого слайда
                    for (const correctness of correctnesses) {
                        await db
                            .update(slideResults)
                            .set({
                                is_correct: correctness.is_correct,
                            })
                            .where(
                                and(
                                    eq(slideResults.result_id, resultId),
                                    eq(slideResults.slide_id, correctness.slide_id)
                                )
                            );

                        updatedSlides.push({
                            slide_id: correctness.slide_id,
                            is_correct: correctness.is_correct,
                        });
                    }
                }
            }

            return {
                success: true,
                updatedSlides,
            };
        } catch (dbError) {
            console.error('Database error:', dbError);
            return fail(500, { error: 'Database error' });
        }
    },
};
