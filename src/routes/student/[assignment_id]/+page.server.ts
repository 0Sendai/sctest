import { fail, redirect } from '@sveltejs/kit';
import { eq, asc, and, inArray } from 'drizzle-orm';
import { assignments, slides, assignmentResults, users, slideResults } from '$lib/server/db/schema';

export async function load({ params, locals }) {
    if (!locals.user?.id) {
        return fail(401, { error: 'Unauthorized' });
    }

    const studentId = locals.user.id;
    const assignmentId = params.assignment_id;

    // Проверяем, что задание существует и получаем его
    let assignmentData;
    try {
        assignmentData = await locals.db
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
    } catch (dbError) {
        console.error('Database error (assignments):', dbError);
        return fail(500, { error: 'Database error' });
    }

    if (assignmentData.length === 0) {
        return fail(404, { error: 'Assignment not found' });
    }

    const assignment = assignmentData[0];

    // Получаем все slides для этого задания, отсортированные по позиции
    let slidesData;
    try {
        slidesData = await locals.db
            .select()
            .from(slides)
            .where(eq(slides.assignment_id, assignmentId))
            .orderBy(asc(slides.position));
    } catch (dbError) {
        console.error('Database error (slides):', dbError);
        return fail(500, { error: 'Database error' });
    }

    // Очищаем slides от чувствительных данных (правильные ответы)
    const cleanSlides = slidesData.map(slide => {
        let content: Record<string, unknown> = slide.content as Record<string, unknown>;
        if (content && typeof content === 'object') {
            // Для quiz: убираем correctVariantIndex (или старый формат correct)
            if ('correctVariantIndex' in content) {
                content = { ...content, correctVariantIndex: undefined };
            }
            if ('correct' in content) {
                content = { ...content, correct: undefined };
            }
            // Для text: убираем correctAnswer
            if ('correctAnswer' in content) {
                content = { ...content, correctAnswer: undefined };
            }
        }
        return {
            id: slide.id,
            type: slide.type,
            position: slide.position,
            content,
        };
    });

    // Проверяем или создаем результат студента для этого задания
    let result;
    try {
        result = await locals.db
            .select()
            .from(assignmentResults)
            .where(
                and(
                    eq(assignmentResults.studentId, studentId),
                    eq(assignmentResults.assignmentId, assignmentId)
                )
            )
            .limit(1);
    } catch (dbError) {
        console.error('Database error (assignmentResults select):', dbError);
        return fail(500, { error: 'Database error' });
    }

    if (result.length === 0) {
        // Создаем новый результат
        try {
            const newResult = await locals.db
                .insert(assignmentResults)
                .values({
                    studentId,
                    assignmentId,
                    status: 'in_progress',
                })
                .returning();
            result = newResult;
        } catch (dbError) {
            console.error('Database error (assignmentResults insert):', dbError);
            return fail(500, { error: 'Database error' });
        }
    } else {
        // Обновляем updatedAt
        try {
            await locals.db
                .update(assignmentResults)
                .set({ updatedAt: new Date() })
                .where(eq(assignmentResults.id, result[0].id));
        } catch (dbError) {
            console.error('Database error (assignmentResults update):', dbError);
            return fail(500, { error: 'Database error' });
        }
    }

    const assignmentResult = result[0];

    // Получаем ответы студента на слайды, если задание уже начато
    let slideAnswers: Record<number, any> = {};
    if (assignmentResult.status === 'in_progress' || assignmentResult.currentSlideId) {
        try {
            const savedAnswers = await locals.db
                .select()
                .from(slideResults)
                .where(
                    and(
                        eq(slideResults.result_id, assignmentResult.id),
                        inArray(
                            slideResults.slide_id,
                            cleanSlides.map(s => s.id)
                        )
                    )
                );
            // Преобразуем в словарь: slideId -> answer
            savedAnswers.forEach(sa => {
                slideAnswers[sa.slide_id] = sa.answer;
            });
        } catch (dbError) {
            console.error('Database error (slideResults select):', dbError);
            // Не блокируем загрузку, если не удалось получить ответы
        }
    }

    return {
        assignment: {
            ...assignment,
            authorName: assignment.authorId, // TODO: получить имя автора
        },
        slides: cleanSlides,
        assignmentResult: {
            id: assignmentResult.id,
            currentSlideId: assignmentResult.currentSlideId,
            status: assignmentResult.status,
        },
        slideAnswers,
    };
    
}
