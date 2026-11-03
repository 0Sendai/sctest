import { error } from '@sveltejs/kit';

export async function load({ params, locals }) {
    const assignmentId = params.assignment_id;
    const response = await fetch(
        "http://localhost:8080/api/teacher/" + 
        `assignments/edit?id=${assignmentId}`
    );
    if (!response.ok)
        throw error(500, "bad response from server");
    const data = await response.json();
    console.log(data);
    return { ...data.data };
}
