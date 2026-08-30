import { redirect, error } from '@sveltejs/kit';
import type { UserPayload } from '$lib/server/jwt';

export function redirectUser(user: UserPayload) {
    switch (user.role) {
        case 'admin': redirect(302, '/admin'); break;
        case 'teacher': redirect(302, `/teacher/`); break;
        case 'student': redirect(302, `/student/`); break;
        default: error(403, { message: 'bad user role' }); break;
    }
}

