import { fail } from '@sveltejs/kit';

export async function load({ params, locals, fetch }) {
    if (!locals.user?.id) {
        return fail(401, { error: 'Unauthorized' });
    }

    // Проверяем роль преподавателя
    if (locals.user.role !== 'ROLE_TEACHER' && locals.user.role !== 'ROLE_ADMIN') {
        return fail(403, { error: 'Forbidden' });
    }

    const studentId = params.student_id;
    const assignmentId = params.assignment_id;

    const response = await fetch(
        '/api/teacher/students/student/assignment' +
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
