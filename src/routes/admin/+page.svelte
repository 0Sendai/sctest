<script lang="ts">
    import { page } from '$app/state';
    const path = page.url.pathname;
</script>

<a href={path + '/teachers'}>teachers</a>
<a href={path + '/students'}>students</a>
<a href={path + '/assignments'}>assignments</a>

<form method="POST" action="?/logout">
    <button type="submit">logout</button>
</form>

