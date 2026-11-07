import { redirect, error } from '@sveltejs/kit';
import type { UserPayload } from '$lib/server/jwt';

export function redirectUser(user: UserPayload) {
    switch (user.role) {
        case 'ROLE_ADMIN': redirect(302, '/admin'); break;
        case 'ROLE_TEACHER': redirect(302, `/teacher/`); break;
        case 'ROLE_STUDENT': redirect(302, `/student/`); break;
        default: error(403, { message: 'bad user role' }); break;
    }
}

