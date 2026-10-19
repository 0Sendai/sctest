<script lang="ts">
    let { data } = $props();
</script>

<h1>list of teachers</h1>

<ul>
    {#each data.teachers as teacher}
        <li>{teacher.name}</li>  
    {/each}
</ul>
