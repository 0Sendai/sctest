import { json, error } from '@sveltejs/kit';
import type { RequestHandler } from './$types';
import { eq } from 'drizzle-orm';
import { db } from '$lib/server/db/index';
import { assignmentResults } from '$lib/server/db/schema';

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
    let body: { resultId: number; status?: string; currentSlideId?: number };
    try {
        body = await request.json();
    } catch (parseError) {
        console.error('Failed to parse request body:', parseError);
        return error(400, 'Invalid JSON');
    }

    const { resultId, status, currentSlideId } = body;

    // Валидируем входные данные
    if (!resultId) {
        return error(400, 'Missing required field: resultId');
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

        // Формируем данные для обновления
        const updateData: {
            status?: 'in_progress' | 'completed';
            currentSlideId?: number | null;
            completedAt?: Date | null;
            updatedAt: Date;
        } = {
            updatedAt: new Date(),
        };

        if (status) {
            if (status !== 'in_progress' && status !== 'completed') {
                return error(400, 'Invalid status value');
            }
            updateData.status = status;
        }

        if (currentSlideId !== undefined) {
            updateData.currentSlideId = currentSlideId;
        }

        // Если задание завершено, устанавливаем completedAt
        if (status === 'completed') {
            updateData.completedAt = new Date();
        }

        // Обновляем результат задания
        await db
            .update(assignmentResults)
            .set(updateData)
            .where(eq(assignmentResults.id, resultId));

        return json({ success: true });
    } catch (dbError) {
        console.error('Database error (assignmentResult):', dbError);
        return error(500, 'Database error');
    }
};
