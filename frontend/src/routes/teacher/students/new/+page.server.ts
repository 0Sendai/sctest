import { fail } from '@sveltejs/kit';

export const actions = {
    default: async ({ request, locals, fetch }) => {
        const data = await request.formData();
        const name = data.get('name');
        const password = data.get('password');

        console.log('enter');

        if (!name || !password) {
            return fail(400, { error: 'bad username or password'});
        }

        const response = await fetch(
            '/api/teacher/students/create', { 
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    name: name,
                    password: password
                })
            }
        );
        const msg = await response.text();
        if (!response.ok) {
            return fail(500, { error: msg });
        }
        
        return { success: `User ${name} was added` };
    },
};
