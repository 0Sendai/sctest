import { fail, error } from '@sveltejs/kit';
import { logout } from '$lib/server/logout';

export async function load({ locals }) {
    if (!locals.user?.id) {
        console.log('student bad user id');
        return fail(500, 'bad user id');
    }

    const studentId = locals.user.id;
    const response = await fetch(`http://127.0.0.1:8080/api/student?id=${studentId}`);
    if (!response.ok) {
        console.log(response)
        throw  error(500, 'bad response from server');
    }
    const results = await response.json();
    //console.log(results);
    return { results: results["results"] };
}

export const actions = {
    logout: async ({ locals, cookies }) => {
        await logout(locals, cookies);
    }
}
