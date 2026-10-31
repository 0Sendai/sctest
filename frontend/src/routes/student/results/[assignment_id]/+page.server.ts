import { error, fail, redirect } from '@sveltejs/kit';

export async function load({ params, locals }) {
    if (!locals.user?.id) {
        return fail(401, { error: 'Unauthorized' });
    }

    const studentId = locals.user.id;
    const assignmentId = params.assignment_id;
    const response = await fetch(`http://localhost:8080/api/student/feedback?studId=${studentId}&assignmentId=${assignmentId}`);
    if (!response.ok)
        throw error(500, "bad response from server");
    const result = await response.json();
    console.log(result.data);
    return { result: result["data"] };
}
