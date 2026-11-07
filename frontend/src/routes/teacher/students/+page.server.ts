import { error } from '@sveltejs/kit';

export async function load({ locals, fetch }) {
    const response = await fetch('/api/teacher/students');
    const students = await response.json();
    console.log(students);
    if (!response.ok) 
        throw error(500, "can't load students");
    
    return { students: students };
}
