<script lang="ts">
    import { enhance } from '$app/forms';

    const { form } = $props();
    let username = $state('');
    let password = $state('');

    let error = $state({name: '', pass: ''});

    function discardEnter(e: KeyboardEvent) {
        if (e.key == 'Enter')
            e.preventDefault();
    }

    function validateInput(): boolean {
        error.name = username ? '' : 'Username is required';
        error.pass = password ? '' : 'Password is required';

        return !error.name && !error.pass;
    }
</script>

<h1>login</h1>

{#if form?.error}
    <p class="error">{form.error}</p>
{/if}

<form method="POST" use:enhance={({ cancel, formData }) => {
    if (!validateInput()) cancel();
    const username = formData.get('username') as string;
    const password = formData.get('password') as string;
    formData.set('username', username.trim());
    formData.set('password', password.trim());

}}>
    <div>
        <p>enter username: </p>
        <input type="text" 
               name="username" 
               bind:value={username}
               onkeydown={discardEnter}/>
    </div>
    <div>
        <p>enter password: </p>
        <input type="text" 
               name="password" 
               bind:value={password}
               autocomplete="off"/>
    </div>
    <button type="submit">Login</button>
</form>

{#if error.name}
    <p class="error">{error.name}</p>
{/if}
{#if error.pass}
    <p class="error">{error.pass}</p>
{/if}
