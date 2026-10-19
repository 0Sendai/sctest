import type { UserPayload } from '$lib/server/jwt';
import type { User } from '$lib/server/db/types';
import { redirect, fail, error } from '@sveltejs/kit';
import jwt from 'jsonwebtoken';
import { compare } from 'bcrypt';
import { redirectUser } from '$lib/server/redirectUser';
import { JWT_SECRET } from '$env/static/private';

export function load({ cookies, locals }) {
    const user = locals.user;
    console.log(locals.user);
    if (user) {
        console.log("redirected in beginning");
        redirectUser(user);
    }
}

