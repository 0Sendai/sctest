<script lang="ts">
    import { enhance } from '$app/forms';

    let { form } = $props();

    let name = $state('');
    let password = $state('');
    let err = $state({ name: '', password: '' });

    function validateInput(): boolean {
        err.name = name ? '' : 'Name is required';
        err.password = password ? '' : 'Password is required';
        return !err.name && !err.password;
    }

</script>

<h1>Add student</h1>

{#if form?.error}
    <p class="error">{form.error}</p>
{/if}

{#if form?.success}
    <p class="success">{form.success}</p>
{/if}

{#if err.name}
    <p class="error">{err.name}</p>
{/if}
{#if err.password}
    <p class="error">{err.password}</p>
{/if}

<form method="POST" use:enhance={({ cancel, formData }) => {
    console.log('enter form');
    if (!validateInput()) cancel();
    const username = formData.get('name') as string;
    const password = formData.get('password') as string;
    formData.set('name', username.trim());
    formData.set('password', password.trim());
    console.log('exit form');
}}>
    <div>
        <p>Enter student name: </p>
        <input type="text"
               name="name"
               bind:value={name}
               autocomplete="off"
               onkeydown={(event: KeyboardEvent) => {
                   if (event.key === 'Enter') event.preventDefault();
               }}/>
    </div>
    <div>
        <p>Input password: </p>
        <input type="text"
               name="password"
               bind:value={password}
               autocomplete="off"/>
    </div>
    <button type="submit">Add student</button>
</form>

<a href="./">Go back</a>

