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
        }
    }
    return await resolve(event);
};

export const handleFetch: HandleFetch = async ({ event, request, fetch }) => {
    const reqUrl = new URL(request.url);
    if (reqUrl.pathname.startsWith('/api')) {
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
            headers.set('X-XSRF-TOKEN', csrfToken);
            headers.delete('host');
        }
        const cookieHeader = event.request.headers.get('cookie');
        if (cookieHeader) {
            headers.set('cookie', cookieHeader);
        }
        let targetUrl = request.url;
        if (reqUrl.hostname === '127.0.0.1' || reqUrl.hostname === 'localhost') {
            reqUrl.hostname = 'backend';
            reqUrl.port = '8080';
            targetUrl = reqUrl.toString();
        }
        const newReq = new Request(targetUrl, {
            headers: headers,
            method: request.method,
            body: request.body,
            // @ts-ignore
            duplex: 'half',
            credentials: 'include'
        });
        return fetch(newReq);
    }
    
    return fetch(request);
};
