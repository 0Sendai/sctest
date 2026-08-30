import type { UserPayload } from '$lib/server/jwt';
import type { User } from '$lib/server/db/types';
import { redirect, fail, error } from '@sveltejs/kit';
import jwt from 'jsonwebtoken';
import { compare } from 'bcrypt';
import { redirectUser } from '$lib/server/redirectUser';
import { JWT_SECRET } from '$env/static/private';

export function load({ cookies, locals }) {
    const user = locals.user;
    console.log(locals.user);
    if (user) {
        console.log("redirected in beginning");
        redirectUser(user);
    }
}

export const actions = {
    default: async ({ request, locals, cookies }) => {
        const data = await request.formData();
        const username = data.get('username');
        const password = data.get('password');

        if (!(typeof username === 'string') || !(typeof password === 'string'))
            return fail(400, { error: 'Bad username or password'});

        if (!username || !password) 
            return fail(400, { error: 'Bad username or password'});
        
        const db = locals.db;
        let user_db: User | undefined;
        try {
            user_db = await db.query.users.findFirst({
                where: {
                    name: username,
                },
            });
        } catch (e: any) {
            console.log("login db user select error", error);
            error(500, 'user select error');
        }
        
        if (!user_db || !user_db.password) {
            console.log("login db user select empty response", error);
            error(500, 'user select empty response');
        }


        const match = await compare(password, user_db.password);
        if (!match) {
            console.log('login Wrong username or pass', user_db);
            return fail(400, { error: 'Wrong username or password' });
        }
        
        console.log('login generating userPayload');
        const payload: UserPayload = {
            id: user_db.id,
            username: user_db.name,
            role: user_db.role,
        };
        const token = jwt.sign(payload, JWT_SECRET, { expiresIn: '4h'});
        if (!token) error(500, 'Token sign error');
        cookies.set('session_token', token, { 
            path: '/',
            httpOnly: true,
            secure: true,
            maxAge: 60 * 60 * 4,
        });
        console.log("login redirecting user after token gen");
        redirectUser(payload);
    },
};


