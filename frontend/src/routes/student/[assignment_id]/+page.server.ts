import { fail, redirect } from '@sveltejs/kit';

export async function load({ params, locals, fetch }) {
    if (!locals.user?.id) {
        return fail(401, { error: 'Unauthorized' });
    }

    const studentId = locals.user.id;
    const assignmentId = params.assignment_id;

    const response = await fetch(
        '/api/student/assignment' +
        `?studentId=${studentId}` +
        `&assignmentId=${assignmentId}`
    );
    const data = await response.json();
    if (!response.ok) {
        return fail(500, { error: data.error });
    }
    return { ...data };

}
