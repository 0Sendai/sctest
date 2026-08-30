import { fail } from '@sveltejs/kit';
import { eq } from 'drizzle-orm';
import { users } from '$lib/server/db/schema';

export async function load({ locals }) {
    const student_list = await locals.db.select({
        name: users.name,
    }).from(users)
      .where(eq(users.role, 'student'));

    // if (!teachers) {
    //     console.log('admin/teachers cannot select teachers from db');
    //     return fail(500, 'cannot select teachers from db');
    // }
    return { students: student_list };
}
