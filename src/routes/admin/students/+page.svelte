<script lang="ts">
    let { data } = $props();
</script>

<h1>list of students</h1>

<ul>
    {#each data.students as student}
        <li>{student.name}</li>  
    {/each}
</ul>
