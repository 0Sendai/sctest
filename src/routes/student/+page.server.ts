import { fail } from '@sveltejs/kit';
import { eq, asc, inArray, count, ne, and } from 'drizzle-orm';
import { assignments, assignmentResults, slideComments } from '$lib/server/db/schema';
import { logout } from '$lib/server/logout';

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

    // Получаем комментарии для завершенных заданий
    const completedResults = results.filter(r => r.status === 'completed');
    const commentsPromises = completedResults.map(r =>
        locals.db
            .select()
            .from(slideComments)
            .where(
                and(
                    eq(slideComments.assignment_id, r.assignmentId),
                    eq(slideComments.student_id, studentId)
                )
            )
            .then(comments => comments.some(c => c.comment && c.comment.trim() !== ''))
    );

    const hasCommentsArray = commentsPromises.length > 0
        ? await Promise.all(commentsPromises)
        : [];

    let commentIndex = 0;
    const resultsWithAssignments = results.map(result => {
        const assignment = assignmentsMap.get(result.assignmentId) || { id: result.assignmentId, title: 'Unknown' };
        let isGraded = false;
        if (result.status === 'completed' && hasCommentsArray[commentIndex]) {
            isGraded = true;
        }
        // Если статус in_progress, но currentSlideId null — студент ещё не начинал задание
        let displayStatus: string = result.status;
        if (result.status === 'in_progress' && !result.currentSlideId) {
            displayStatus = 'not_started';
        }
        commentIndex++;
        return {
            ...result,
            assignment,
            isGraded,
            displayStatus,
        };
    });

    return { results: resultsWithAssignments };
}

export const actions = {
    logout: async ({ locals, cookies }) => {
        await logout(locals, cookies);
    }
}
