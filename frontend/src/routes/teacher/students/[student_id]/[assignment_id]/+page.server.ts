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

    const response = await fetch(
        'http://127.0.0.1:8080/api/teacher/students/student/assignment' +
        `?studentId=${studentId}` +
        `&assignmentId=${assignmentId}`
    );
    if (!response.ok) {
        const errorMsg = await response.text();
        return fail(500, { error: errorMsg });
    }
    const data = await response.json();
    return { ...data };
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
