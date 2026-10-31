import { fail } from '@sveltejs/kit';

export const actions = {
    default: async ({ request, locals }) => {
        const data = await request.formData();
        const name = data.get('name');
        const password = data.get('password');

        console.log('enter');

        if (!name || !password) {
            return fail(400, { error: 'bad username or password'});
        }

        const response = await fetch(
            'http://127.0.0.1:8080/api/teacher/students/create', { 
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
