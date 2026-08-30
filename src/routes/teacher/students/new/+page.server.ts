import { fail, error } from '@sveltejs/kit';
import { hash } from 'bcrypt';
import { users } from '$lib/server/db/schema';

export const actions = {
    default: async ({ request, locals }) => {
        const data = await request.formData();
        const name = data.get('name');
        const password = data.get('password');

        console.log('enter');

        if (!name || !password) {
            return fail(400, { error: 'bad username or password'});
        }

        if (!(typeof name === 'string') || !(typeof password === 'string')) {
            console.log('teacher/students/new name or pass is not string');
            error(500, 'teacher/students/new name or pass is not string');
        }

        const user_db = await locals.db.query.users.findFirst({
            where: {
                name: name,
            }
        });
        if (user_db) {
            return fail(400, { error: 'Student already exists'});
        }
        const passHash = await hash(password, 10);
        try {
            await locals.db.insert(users).values({
                name: name,
                password: passHash,
                role: 'student',
            });
        } catch (error: any) {
            console.log('teacher/students/new ', error);
            return fail(400, { error: 'cannot insert student' });
        }
        console.log(`user ${name} was added`);
        return {
            success: `User ${name} was added`
        };
    },
};
