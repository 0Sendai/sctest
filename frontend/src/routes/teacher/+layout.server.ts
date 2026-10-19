import { redirect } from '@sveltejs/kit';

export function load({ locals }) {
    if (!locals.user) redirect(302, '/login');

    if (locals.user.role !== 'teacher') {
        console.log(`user ${locals.user} was tried to access teacher panel`);
        redirect(302, '/');
    }
}
