import { redirect } from '@sveltejs/kit';

export function load({ locals }) {
    if (!locals.user) redirect(302, '/login');

    if (locals.user.role !== 'ROLE_ADMIN') {
        console.log(`user ${locals.user} was tried to access admin panel`);
        redirect(302, '/');
    }
}
