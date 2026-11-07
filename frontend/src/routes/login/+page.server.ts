import { redirectUser } from '#lib/server/redirectUser.ts';

export function load({ cookies, locals }) {
    const user = locals.user;
    console.log(locals.user);
    if (user) {
        console.log("redirected in beginning");
        redirectUser(user);
    }
}

