import { json, error } from '@sveltejs/kit';
import type { RequestHandler } from './$types';
import { eq } from 'drizzle-orm';
import { db } from '$lib/server/db/index';
import { slideResults, assignmentResults, slides } from '$lib/server/db/schema';

export const POST: RequestHandler = async ({ request, locals }) => {
    // Проверяем авторизацию
    if (!locals.user?.id) {
        return error(401, 'Unauthorized');
    }

    // Проверяем роль студента
    if (locals.user.role !== 'student') {
        return error(403, 'Forbidden');
    }

    // Парсим тело запроса
    let body: { resultId: number; slideId: number; answer: any };
    try {
        body = await request.json();
    } catch (parseError) {
        console.error('Failed to parse request body:', parseError);
        return error(400, 'Invalid JSON');
    }

    const { resultId, slideId, answer } = body;

    // Валидируем входные данные
    if (!resultId || !slideId) {
        return error(400, 'Missing required fields: resultId, slideId');
    }

    try {
        // Проверяем, что результат задания существует и принадлежит студенту
        const assignmentResult = await db
            .select()
            .from(assignmentResults)
            .where(eq(assignmentResults.id, resultId))
            .limit(1);

        if (assignmentResult.length === 0) {
            return error(404, 'Assignment result not found');
        }

        if (assignmentResult[0].studentId !== locals.user.id) {
            return error(403, 'Access denied');
        }

        // Проверяем, что слайд существует
        const slide = await db
            .select()
            .from(slides)
            .where(eq(slides.id, slideId))
            .limit(1);

        if (slide.length === 0) {
            return error(404, 'Slide not found');
        }

        // Проверяем, что слайд принадлежит заданию
        if (slide[0].assignment_id !== assignmentResult[0].assignmentId) {
            return error(400, 'Slide does not belong to this assignment');
        }

        // Сохраняем или обновляем результат слайда
        await db
            .insert(slideResults)
            .values({
                result_id: resultId,
                slide_id: slideId,
                answer: answer,
            })
            .onConflictDoUpdate({
                target: [slideResults.result_id, slideResults.slide_id],
                set: {
                    answer: answer,
                    answered_at: new Date(),
                },
            });

        // Обновляем currentSlideId в результате задания
        await db
            .update(assignmentResults)
            .set({
                currentSlideId: slideId,
                updatedAt: new Date(),
            })
            .where(eq(assignmentResults.id, resultId));

        return json({ success: true });
    } catch (dbError) {
        console.error('Database error (slideResult):', dbError);
        return error(500, 'Database error');
    }
};
