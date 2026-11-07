import { fail, error } from '@sveltejs/kit';

export async function load({ locals, fetch }) {
    if (!locals.user?.id) {
        console.log('teacher/assignments bad user id');
        return fail(500, 'bad user id');
    }
  
    const response = await fetch(
        "/api/teacher/assignments?id=" +
        `${locals.user.id}`
    );
    if (!response.ok) {
        const data = await response.text();
        console.log(data);
        throw error(500, "bad response from server");
    }
    const data = await response.json();
    return { ...data };
}
