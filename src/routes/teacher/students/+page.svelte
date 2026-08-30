<script lang="ts">
    import { page } from '$app/state';

    let { data } = $props();
    const path = page.url.pathname;
</script>

<a href={path + '/new'}>Add new student</a>

<h2>list of students</h2>

<ul>
    {#each data.students as student}
        <li>{student.name}</li>  
    {/each}
</ul>

<a href="./">Go back</a>
