import type { Handle } from '@sveltejs/kit';
import type { UserPayload } from '$lib/server/jwt';
import { redirect } from '@sveltejs/kit';
import { db } from '$lib/server/db/index';
import { redirectUser } from '$lib/server/redirectUser';
import jwt from 'jsonwebtoken';
import { JWT_SECRET } from '$env/static/private';

export const handle: Handle = async ({ event, resolve }) => {
    
    return await resolve(event);
};
