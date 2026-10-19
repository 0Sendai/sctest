import { fail } from '@sveltejs/kit';
import { assignments } from '$lib/server/db/schema';

export async function load({ locals }) {
    const assignments_list = await locals.db.select({
        title: assignments.title,
    }).from(assignments);

    // if (!teachers) {
    //     console.log('admin/teachers cannot select teachers from db');
    //     return fail(500, 'cannot select teachers from db');
    // }
    return { assignments: assignments_list };
}
