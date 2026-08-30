<script lang="ts">
    let { data } = $props();
</script>

<h1>list of assignments</h1>

<ul>
    {#each data.assignments as assignment}
        <li>{assignment.title}</li>  
    {/each}
</ul>
