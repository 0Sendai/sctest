import type { Handle } from '@sveltejs/kit';
import type { UserPayload } from '$lib/server/jwt';
import { redirect } from '@sveltejs/kit';
import { db } from '$lib/server/db/index';
import { redirectUser } from '$lib/server/redirectUser';
import jwt from 'jsonwebtoken';
import { JWT_SECRET } from '$env/static/private';

export const handle: Handle = async ({ event, resolve }) => {
    event.locals.db = db;
    
    const token = event.cookies.get('session_token');
    //console.log(token);
    if (token) {
        try {
            const decoded = jwt.verify(token, JWT_SECRET) as UserPayload;

            event.locals.user = {
                id: decoded.id,
                username: decoded.username,
                role: decoded.role,
            };
        } catch (err) {
            event.locals.user = null;
            redirect(302, '/login');
        }
    } else {
        if (event.url.pathname.startsWith('/login'))
            return await resolve(event);

        event.locals.user = null;
        redirect(302, '/login');
    }

    const path: string = event.url.pathname.split('/')[1];
    //console.log(event.url.pathname.split('/'));
    //console.log(path);
    switch(path) {
        case 'admin': {
            if (event.locals.user.role !== 'admin')
                redirectUser(event.locals.user);
            break;
        }
        case 'teacher': {
            if (event.locals.user.role !== 'teacher')
                redirectUser(event.locals.user);
            break;
        }
        case 'student': {
            if (event.locals.user.role !== 'student')
                redirectUser(event.locals.user);
            break;
        }
        case 'login':
        case '': break;

        default: {
            console.log('bad pathname in hooks');
            redirectUser(event.locals.user);
        }
    }

    return await resolve(event);
};
