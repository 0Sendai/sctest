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

        // Определяем правильность ответа
        const slideContent = slide[0].content as Record<string, unknown>;
        let isCorrect: boolean | null = null;

        if (slide[0].type === 'quiz' && 'correctVariantIndex' in slideContent && 'variants' in slideContent) {
            const correctIndex = slideContent.correctVariantIndex as number;
            const variants = slideContent.variants as string[];
            const studentAnswer = answer as string;
            // Находим индекс ответа студента в вариантах
            const studentIndex = variants.indexOf(studentAnswer);
            isCorrect = studentIndex === correctIndex;
        } else if (slide[0].type === 'text' && 'correctAnswer' in slideContent) {
            const correctAnswer = slideContent.correctAnswer as string;
            const studentAnswer = (answer as string)?.trim().toLowerCase();
            const correctTrimmed = correctAnswer.trim().toLowerCase();
            isCorrect = studentAnswer === correctTrimmed;
        }

        // Сохраняем или обновляем результат слайда
        await db
            .insert(slideResults)
            .values({
                result_id: resultId,
                slide_id: slideId,
                answer: answer,
                is_correct: isCorrect,
            })
            .onConflictDoUpdate({
                target: [slideResults.result_id, slideResults.slide_id],
                set: {
                    answer: answer,
                    is_correct: isCorrect,
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
