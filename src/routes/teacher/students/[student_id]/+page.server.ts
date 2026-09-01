import { fail } from '@sveltejs/kit';
import { eq, asc } from 'drizzle-orm';
import { users, assignments, assignmentResults, slideResults } from '$lib/server/db/schema';

export async function load({ params, locals }) {
    if (!locals.user?.id) {
        return fail(401, { error: 'Unauthorized' });
    }

    if (locals.user.role !== 'teacher' && locals.user.role !== 'admin') {
        return fail(403, { error: 'Forbidden' });
    }

    const studentId = parseInt(params.student_id);

    // Получаем информацию о студенте
    const studentData = await locals.db
        .select({
            id: users.id,
            name: users.name,
            role: users.role,
        })
        .from(users)
        .where(eq(users.id, studentId))
        .limit(1);

    if (studentData.length === 0) {
        return fail(404, { error: 'Student not found' });
    }

    const student = studentData[0];

    // Получаем все результаты студента с информацией о заданиях
    const results = await locals.db
        .select({
            id: assignmentResults.id,
            assignmentId: assignmentResults.assignmentId,
            status: assignmentResults.status,
            startedAt: assignmentResults.startedAt,
            completedAt: assignmentResults.completedAt,
            assignmentTitle: assignments.title,
        })
        .from(assignmentResults)
        .leftJoin(assignments, eq(assignments.id, assignmentResults.assignmentId))
        .where(eq(assignmentResults.studentId, studentId))
        .orderBy(asc(assignmentResults.startedAt));

    // Для каждого результата получаем статистику по слайдам
    const assignmentsWithDetails = await Promise.all(
        results.map(async (result) => {
            const slideResultsList = await locals.db
                .select()
                .from(slideResults)
                .where(eq(slideResults.result_id, result.id))
                .orderBy(asc(slideResults.id));

            return {
                id: result.id,
                assignmentId: result.assignmentId,
                status: result.status,
                startedAt: result.startedAt,
                completedAt: result.completedAt,
                assignmentTitle: result.assignmentTitle,
                slideCount: slideResultsList.length,
                correctCount: slideResultsList.filter((sr: typeof slideResultsList[number]) => sr.is_correct === true).length,
                incorrectCount: slideResultsList.filter((sr: typeof slideResultsList[number]) => sr.is_correct === false).length,
            };
        })
    );

    return {
        student,
        assignments: assignmentsWithDetails,
    };
}
