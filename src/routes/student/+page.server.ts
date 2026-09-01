import { fail } from '@sveltejs/kit';
import { eq, asc, inArray } from 'drizzle-orm';
import { assignments, assignmentResults } from '$lib/server/db/schema';

export async function load({ locals }) {
    if (!locals.user?.id) {
        console.log('student bad user id');
        return fail(500, 'bad user id');
    }

    const studentId = locals.user.id;

    // Получаем все результаты студента
    const results = await locals.db
        .select()
        .from(assignmentResults)
        .where(eq(assignmentResults.studentId, studentId))
        .orderBy(asc(assignmentResults.startedAt));

    // Получаем названия всех заданий
    const assignmentIds = results.map(r => r.assignmentId);
    const assignmentsData = assignmentIds.length > 0
        ? await locals.db
            .select({
                id: assignments.id,
                title: assignments.title,
            })
            .from(assignments)
            .where(inArray(assignments.id, assignmentIds as string[]))
        : [];

    // Объединяем данные
    const assignmentsMap = new Map(
        assignmentsData.map(a => [a.id, a])
    );

    const resultsWithAssignments = results.map(result => ({
        ...result,
        assignment: assignmentsMap.get(result.assignmentId) || { id: result.assignmentId, title: 'Unknown' },
    }));

    return { results: resultsWithAssignments };
}
