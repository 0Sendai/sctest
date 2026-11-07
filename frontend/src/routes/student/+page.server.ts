import { fail, error } from '@sveltejs/kit';
import { logout } from '#lib/server/logout.ts';

export async function load({ locals, fetch }) {
    if (!locals.user?.id) {
        console.log('student bad user id');
        return fail(500, 'bad user id');
    }

    const studentId = locals.user.id;
    const response = await fetch(`/api/student?id=${studentId}`);
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
