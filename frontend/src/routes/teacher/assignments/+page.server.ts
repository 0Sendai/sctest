import { fail, error } from '@sveltejs/kit';

export async function load({ locals }) {
    if (!locals.user?.id) {
        console.log('teacher/assignments bad user id');
        return fail(500, 'bad user id');
    }
  
    const response = await fetch(
        "http://localhost:8080/api/teacher/assignments?id=" +
        `${locals.user.id}`
    );
    if (!response.ok)
        throw error(500, "bad response from server");
    const data = await response.json();
    return { ...data };
}
