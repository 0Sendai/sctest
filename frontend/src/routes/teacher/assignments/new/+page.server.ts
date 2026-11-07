import { error } from '@sveltejs/kit';

export async function load({ locals, fetch }) {
    if (!locals.user?.id) {
        console.log('teacher/assignments/new bad user id');
        throw error(500, 'bad user id');
    }

    const response = await fetch(
        '/api/teacher/assignments/create'
    );
    if (!response.ok) {
        throw error(500, "can't load students");
    }
    const data = await response.json();
    return { teacherId: locals.user.id, students: data };
}
