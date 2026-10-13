import { fail } from '@sveltejs/kit';
import { eq } from 'drizzle-orm';
import { assignments } from '$lib/server/db/schema';

export async function load({ locals }) {
    if (!locals.user?.id) {
        console.log('teacher/assignments bad user id');
        return fail(500, 'bad user id');
    }
    
    const assignments_list = await locals.db.select({
        id: assignments.id,
        title: assignments.title,
    }).from(assignments)
      .where(eq(assignments.author_id, locals.user.id));

    // if (!teachers) {
    //     console.log('admin/teachers cannot select teachers from db');
    //     return fail(500, 'cannot select teachers from db');
    // }
    return { assignments: assignments_list };
}
