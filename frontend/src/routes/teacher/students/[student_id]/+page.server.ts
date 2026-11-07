import { fail } from '@sveltejs/kit';

export async function load({ params, locals, fetch }) {
    if (!locals.user?.id) {
        return fail(401, { error: 'Unauthorized' });
    }

    if (locals.user.role !== 'ROLE_TEACHER' && locals.user.role !== 'ROLE_ADMIN') {
        return fail(403, { error: 'Forbidden' });
    }

    const response = await fetch(
        '/api/teacher/students/student?id=' +
        params.student_id
    );
    if (!response.ok) {
        return fail(500, { error: "Student not found" });
    }
    const data = await response.json();
    return { ...data };
}
