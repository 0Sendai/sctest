import type { Handle, HandleFetch } from '@sveltejs/kit/hooks';
import { redirect } from '@sveltejs/kit';
import { getUserFromToken } from '#lib/server/jwt.ts';
import type { UserPayload } from '#lib/server/jwt.ts';
import { redirectUser } from '#lib/server/redirectUser.ts';

export const handle: Handle = async ({ event, resolve }) => {
    const token = event.cookies.get('AUTH-TOKEN');
    const pathname = event.url.pathname;
    const isProtected = pathname.startsWith('/teacher') || pathname.startsWith('/student')
        || pathname.startsWith('/admin');
    const isLoginPage = pathname === '/login';
    
    if (!token && isProtected && !isLoginPage) 
        throw redirect(303, '/login');

    if (token) {
        const user: UserPayload | null = getUserFromToken(token);
        if (user) {
            event.locals.user = user;
            console.log(user);
        }
    }
    return await resolve(event);
};

export const handleFetch: HandleFetch = async ({ event, request, fetch }) => {
    if (event.url.pathname.startsWith('/api')) {
        let csrfToken: string | undefined;
        if (event.cookies) {
            csrfToken = event.cookies.get('XSRF-TOKEN');
        }
        else if (typeof document !== 'undefined') {
            const matches = document.cookie.match(/(?:^|; )XSRF-TOKEN=([^;]*)/);
            if (matches) csrfToken = decodeURIComponent(matches[1]);
        }
        const headers = new Headers(request.headers);
        if (csrfToken) {
            headers.set('XSRF-TOKEN', csrfToken);
        }
        const newReq = new Request(request, {
            headers,
            credentials: 'include'
        });
        return fetch(newReq);
    }
    
    return fetch(request);
};
