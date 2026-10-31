import { error } from '@sveltejs/kit';

export async function load({ locals }) {
    const response = await fetch('http://127.0.0.1:8080/api/teacher/students');
    if (!response.ok)
        throw error(500, "can't load students");
    const students = await response.json();
    return { students: students };
}
