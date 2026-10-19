import { redirect } from '@sveltejs/kit';
import { logout } from '$lib/server/logout';

export const actions = {
    logout: async ({ locals, cookies }) => {
        await logout(locals, cookies);
    }
}
