import { redirect } from '@sveltejs/kit';

export function load({ locals }) {
    if (!locals.user || locals.user.role !== 'student') {
        redirect(303, '/login');
    }
}
