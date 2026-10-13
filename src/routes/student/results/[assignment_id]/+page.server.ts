import { fail, redirect } from '@sveltejs/kit';
import { eq, and, asc, inArray } from 'drizzle-orm';
import { assignments, slides, assignmentResults, slideResults, slideComments } from '$lib/server/db/schema';

export async function load({ params, locals }) {
    if (!locals.user?.id) {
        return fail(401, { error: 'Unauthorized' });
    }

    const studentId = locals.user.id;
    const assignmentId = params.assignment_id;

    // Получаем задание
    const assignmentData = await locals.db
        .select({
            id: assignments.id,
            title: assignments.title,
        })
        .from(assignments)
        .where(eq(assignments.id, assignmentId))
        .limit(1);

    if (assignmentData.length === 0) {
        return fail(404, { error: 'Assignment not found' });
    }

    const assignment = assignmentData[0];

    // Получаем результат студента
    const resultData = await locals.db
        .select()
        .from(assignmentResults)
        .where(
            and(
                eq(assignmentResults.studentId, studentId),
                eq(assignmentResults.assignmentId, assignmentId)
            )
        )
        .limit(1);

    if (resultData.length === 0) {
        return fail(404, { error: 'Assignment result not found' });
    }

    const result = resultData[0];

    // Проверяем, что задание завершено и оценено
    if (result.status !== 'completed') {
        return fail(403, { error: 'Assignment not completed' });
    }

    // Проверяем, есть ли комментарии от преподавателя
    const commentsData = await locals.db
        .select()
        .from(slideComments)
        .where(
            and(
                eq(slideComments.assignment_id, assignmentId),
                eq(slideComments.student_id, studentId)
            )
        );

    const hasComments = commentsData.some(c => c.comment && c.comment.trim() !== '');

    if (!hasComments) {
        return fail(403, { error: 'Assignment not graded yet' });
    }

    // Получаем все слайды
    const slidesData = await locals.db
        .select()
        .from(slides)
        .where(eq(slides.assignment_id, assignmentId))
        .orderBy(asc(slides.position));

    // Получаем ответы студента
    const slideResultData = await locals.db
        .select()
        .from(slideResults)
        .where(
            and(
                eq(slideResults.result_id, result.id),
                inArray(
                    slideResults.slide_id,
                    slidesData.map(s => s.id)
                )
            )
        );

    // Создаем маппинг ответов
    const answersMap = new Map<number, any>();
    slideResultData.forEach(sr => {
        answersMap.set(sr.slide_id, sr.answer);
    });

    // Создаем маппинг комментариев
    const commentsMap = new Map<number, string>();
    commentsData.forEach(c => {
        if (c.comment && c.comment.trim() !== '') {
            commentsMap.set(c.slide_id, c.comment);
        }
    });

    // Очищаем слайды от чувствительных данных
    const cleanSlides = slidesData.map(slide => {
        let content: Record<string, unknown> = slide.content as Record<string, unknown>;
        if (content && typeof content === 'object') {
            if ('correctVariantIndex' in content) {
                content = { ...content, correctVariantIndex: undefined };
            }
            if ('correct' in content) {
                content = { ...content, correct: undefined };
            }
        }
        return {
            id: slide.id,
            type: slide.type,
            position: slide.position,
            content,
        };
    });

    return {
        assignment: {
            id: assignment.id,
            title: assignment.title,
        },
        result: {
            id: result.id,
            status: result.status,
            startedAt: result.startedAt,
            completedAt: result.completedAt,
        },
        slides: cleanSlides,
        answers: answersMap,
        comments: commentsMap,
    };
}
