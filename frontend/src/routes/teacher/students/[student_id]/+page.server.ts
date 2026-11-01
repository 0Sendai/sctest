import { fail } from '@sveltejs/kit';

export async function load({ params, locals }) {
    if (!locals.user?.id) {
        return fail(401, { error: 'Unauthorized' });
    }

    if (locals.user.role !== 'teacher' && locals.user.role !== 'admin') {
        return fail(403, { error: 'Forbidden' });
    }

    const response = await fetch(
        'http://127.0.0.1:8080/api/teacher/students/student?id=' +
        params.student_id
    );
    if (!response.ok) {
        return fail(500, { error: "Student not found" });
    }
    const data = await response.json();
    return { ...data };
}
