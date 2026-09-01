import { fail } from '@sveltejs/kit';
import { eq } from 'drizzle-orm';
import { users } from '$lib/server/db/schema';

export async function load({ locals }) {
    const student_list = await locals.db.select({
        id: users.id,
        name: users.name,
    }).from(users)
      .where(eq(users.role, 'student'));

    return { students: student_list };
}
