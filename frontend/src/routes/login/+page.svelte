<script lang="ts">
    import { enhance } from '$app/forms';

    //const { form } = $props();
    let username = $state('');
    let password = $state('');

    let validationError = $state({name: '', pass: ''});
    let serverError = $state('');

    function discardEnter(e: KeyboardEvent) {
        if (e.key == 'Enter')
            e.preventDefault();
    }

    function validateInput(): boolean {
        validationError.name = username ? '' : 'Имя пользователя обязательно';
        validationError.pass = password ? '' : 'Пароль обязателен';

        return !validationError.name && !validationError.pass;
    }

    async function handleSubmit(event: SubmitEvent) {
        event.preventDefault();
        const data = { name: username, password: password };
        const response = await fetch('/api/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data),
        });

        if (!response.ok) {
            const error_json = await response.json();
            serverError = error_json.error;
            return;
        }
        window.location.replace('/');
    }
</script>

<div class="login-container">
    <div class="login-card">
        <div class="login-header">
            <div class="logo-icon">
                <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                    <circle cx="12" cy="7" r="4"/>
                </svg>
            </div>
            <h1>Добро пожаловать</h1>
            <p class="subtitle">Войдите в свой аккаунт</p>
        </div>

        {#if serverError}
            <div class="error-banner">
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <circle cx="12" cy="12" r="10"/>
                    <line x1="15" y1="9" x2="9" y2="15"/>
                    <line x1="9" y1="9" x2="15" y2="15"/>
                </svg>
                <span>{serverError}</span>
            </div>
        {/if}

        <form method="POST" onsubmit={handleSubmit}>
            <div class="form-group">
                <label for="username">Имя пользователя</label>
                <input 
                    type="text" 
                    id="username"
                    name="username" 
                    bind:value={username}
                    placeholder="Введите имя пользователя"
                    onkeydown={discardEnter}
                    class:error={validationError.name}/>
                {#if validationError.name}
                    <span class="field-error">{validationError.name}</span>
                {/if}
            </div>

            <div class="form-group">
                <label for="password">Пароль</label>
                <input 
                    type="password" 
                    id="password"
                    name="password" 
                    bind:value={password}
                    placeholder="Введите пароль"
                    autocomplete="off"/>
                {#if validationError.pass}
                    <span class="field-error">{validationError.pass}</span>
                {/if}
            </div>

            <button type="submit" class="btn-primary">
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M15 3h4a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2h-4"/>
                    <polyline points="10 17 15 12 10 7"/>
                    <line x1="15" y1="12" x2="3" y2="12"/>
                </svg>
                Войти
            </button>
        </form>
    </div>
</div>

<style>
    .login-container {
        min-height: 100vh;
        display: flex;
        align-items: center;
        justify-content: center;
        background: linear-gradient(135deg, #f5f8fc 0%, #eef2f7 100%);
        padding: 1rem;
    }

    .login-card {
        background: white;
        border-radius: 16px;
        box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1),
                    0 2px 4px -1px rgba(0, 0, 0, 0.06),
                    0 0 0 1px rgba(142, 177, 209, 0.1);
        padding: 2.5rem;
        width: 100%;
        max-width: 420px;
        animation: slideUp 0.4s ease-out;
    }

    @keyframes slideUp {
        from {
            opacity: 0;
            transform: translateY(20px);
        }
        to {
            opacity: 1;
            transform: translateY(0);
        }
    }

    .login-header {
        text-align: center;
        margin-bottom: 2rem;
    }

    .logo-icon {
        width: 64px;
        height: 64px;
        background: linear-gradient(135deg, #8eb1d1 0%, #7aa3c4 100%);
        border-radius: 16px;
        display: flex;
        align-items: center;
        justify-content: center;
        margin: 0 auto 1rem;
        color: white;
    }

    .login-header h1 {
        font-size: 1.75rem;
        font-weight: 700;
        color: #1a1a2e;
        margin: 0 0 0.5rem;
    }

    .subtitle {
        color: #6b7280;
        margin: 0;
        font-size: 0.95rem;
    }

    .error-banner {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        background: #fef2f2;
        border: 1px solid #fecaca;
        color: #dc2626;
        padding: 0.75rem 1rem;
        border-radius: 8px;
        margin-bottom: 1.5rem;
        font-size: 0.875rem;
    }

    .form-group {
        margin-bottom: 1.25rem;
    }

    .form-group label {
        display: block;
        margin-bottom: 0.5rem;
        font-size: 0.875rem;
        font-weight: 500;
        color: #374151;
    }

    .form-group input {
        width: 100%;
        padding: 0.75rem 1rem;
        border: 2px solid #e5e7eb;
        border-radius: 8px;
        font-size: 1rem;
        transition: all 0.2s;
        outline: none;
        box-sizing: border-box;
    }

    .form-group input:focus {
        border-color: #8eb1d1;
        box-shadow: 0 0 0 3px rgba(142, 177, 209, 0.1);
    }

    .form-group input.error {
        border-color: #dc2626;
    }

    .form-group input.error:focus {
        box-shadow: 0 0 0 3px rgba(220, 38, 38, 0.1);
    }

    .field-error {
        display: block;
        color: #dc2626;
        font-size: 0.75rem;
        margin-top: 0.375rem;
    }

    .btn-primary {
        width: 100%;
        padding: 0.875rem 1.5rem;
        background: linear-gradient(135deg, #8eb1d1 0%, #7aa3c4 100%);
        color: white;
        border: none;
        border-radius: 8px;
        font-size: 1rem;
        font-weight: 600;
        cursor: pointer;
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 0.5rem;
        transition: all 0.2s;
        margin-top: 0.5rem;
    }

    .btn-primary:hover {
        transform: translateY(-1px);
        box-shadow: 0 4px 12px rgba(142, 177, 209, 0.4);
    }

    .btn-primary:active {
        transform: translateY(0);
    }
</style>
