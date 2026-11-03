import { fail } from '@sveltejs/kit';

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
