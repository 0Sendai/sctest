import type { Cookies } from '@sveltejs/kit';
import { redirect } from '@sveltejs/kit';

export async function logout(locals: App.Locals, cookies: Cookies) {
    if(!locals.user) return;
    cookies.set('AUTH-TOKEN', '', {
        path: '/',
        httpOnly: true,
        secure: true,
        expires: new Date(0),
        maxAge: 0
    });
    cookies.delete('AUTH-TOKEN', {path: '/'});
    redirect(303, '/login');
}
