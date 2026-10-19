import type { RequestHandler } from './$types';
import type { UserPayload } from '$lib/server/jwt';
import type { User } from '$lib/server/db/types';
import jwt from 'jsonwebtoken';
import { json } from '@sveltejs/kit';
import { compare } from 'bcrypt';
import { JWT_SECRET } from '$env/static/private';

export const POST: RequestHandler = async ({ request, locals, cookies }) => {
    console.log('entered to api/login');
    const data = await request.json();
    const username = data.username;
    const password = data.password;

    if (!(typeof username === 'string') || !(typeof password === 'string'))
        //return fail(400, { error: 'Bad username or password'});
        return json({ error: 'Bad username or password'}, { status: 400 });

    if (!username || !password) 
        //return fail(400, { error: 'Bad username or password'});
        return json({ error: 'Bad username or password'}, { status: 400 });
    
    const db = locals.db;
    let user_db: User | undefined;
    try {
        user_db = await db.query.users.findFirst({
            where: {
                name: username,
            },
        });
    } catch (e: any) {
        console.log("login db user select error", e);
        //error(500, 'user select error');
        return json({ error: 'user select error' }, { status: 500 });
    }
    
    if (!user_db || !user_db.password) {
        console.log("login db user select empty response");
        //return fail(400, {error: 'Wrong username or password'});
        return json({ error: 'Wrong username or password'}, { status: 400 }); 
    }


    const match = await compare(password, user_db.password);
    if (!match) {
        console.log('login Wrong username or pass', user_db);
        //return fail(400, { error: 'Wrong username or password' });
        return json({ error: 'Wrong username or password'}, { status: 400 });
    }
    
    console.log('login generating userPayload');
    const payload: UserPayload = {
        id: user_db.id,
        username: user_db.name,
        role: user_db.role,
    };
    const token = jwt.sign(payload, JWT_SECRET, { expiresIn: '4h'});
    if (!token) 
        //error(500, 'Token sign error');
        return json({ error: 'Token sign error' }, { status: 500 });
    cookies.set('session_token', token, { 
        path: '/',
        httpOnly: true,
        secure: true,
        maxAge: 60 * 60 * 4,
    });
    console.log("login redirecting user after token gen");
    //redirectUser(payload);
    return json({ status: 'success' });
    
};
