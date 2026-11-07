import { redirect } from '@sveltejs/kit';

export function load({ locals }) {
    if (!locals.user || locals.user.role !== 'ROLE_STUDENT') {
        redirect(303, '/login');
    }
}
