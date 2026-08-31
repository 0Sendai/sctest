<script lang="ts">
    import { enhance } from '$app/forms';

    let { form } = $props();

    let name = $state('');
    let password = $state('');
    let err = $state({ name: '', password: '' });

    function validateInput(): boolean {
        err.name = name ? '' : 'Имя обязательно';
        err.password = password ? '' : 'Пароль обязателен';
        return !err.name && !err.password;
    }

</script>

<div class="new-student-container">
    <div class="new-student-card">
        <div class="card-header">
            <div class="logo-icon">
                <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/>
                    <circle cx="9" cy="7" r="4"/>
                    <line x1="19" y1="8" x2="19" y2="14"/>
                    <line x1="22" y1="11" x2="16" y2="11"/>
                </svg>
            </div>
            <h1>Добавить студента</h1>
            <p class="subtitle">Заполните форму для создания нового аккаунта</p>
        </div>

        {#if form?.error}
            <div class="error-banner">
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <circle cx="12" cy="12" r="10"/>
                    <line x1="15" y1="9" x2="9" y2="15"/>
                    <line x1="9" y1="9" x2="15" y2="15"/>
                </svg>
                <span>{form.error}</span>
            </div>
        {/if}

        {#if form?.success}
            <div class="success-banner">
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/>
                    <polyline points="22 4 12 14.01 9 11.01"/>
                </svg>
                <span>{form.success}</span>
            </div>
        {/if}

        <form method="POST" use:enhance={({ cancel, formData }) => {
            if (!validateInput()) cancel();
            const username = formData.get('name') as string;
            const password = formData.get('password') as string;
            formData.set('name', username.trim());
            formData.set('password', password.trim());
        }}>
            <div class="form-group">
                <label for="name">Имя студента</label>
                <input 
                    type="text" 
                    id="name"
                    name="name" 
                    bind:value={name}
                    placeholder="Введите имя"
                    class:error={err.name || form?.error}
                    autocomplete="off"/>
                {#if err.name}
                    <span class="field-error">{err.name}</span>
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
                    class:error={err.password || form?.error}
                    autocomplete="off"/>
                {#if err.password}
                    <span class="field-error">{err.password}</span>
                {/if}
            </div>

            <button type="submit" class="btn-primary">
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/>
                    <circle cx="9" cy="7" r="4"/>
                    <line x1="19" y1="8" x2="19" y2="14"/>
                    <line x1="22" y1="11" x2="16" y2="11"/>
                </svg>
                Добавить студента
            </button>
        </form>

        <a href="../" class="btn-back">
            <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <line x1="19" y1="12" x2="5" y2="12"/>
                <polyline points="12 19 5 12 12 5"/>
            </svg>
            Назад к списку
        </a>
    </div>
</div>

<style>
    .new-student-container {
        min-height: 100vh;
        display: flex;
        align-items: center;
        justify-content: center;
        background: linear-gradient(135deg, #f5f8fc 0%, #eef2f7 100%);
        padding: 1rem;
    }

    .new-student-card {
        background: white;
        border-radius: 16px;
        box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1),
                    0 2px 4px -1px rgba(0, 0, 0, 0.06),
                    0 0 0 1px rgba(142, 177, 209, 0.1);
        padding: 2.5rem;
        width: 100%;
        max-width: 480px;
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

    .card-header {
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

    .card-header h1 {
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

    .success-banner {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        background: #f0fdf4;
        border: 1px solid #bbf7d0;
        color: #16a34a;
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
        box-sizing: border-box;
    }

    .btn-primary:hover {
        transform: translateY(-1px);
        box-shadow: 0 4px 12px rgba(142, 177, 209, 0.4);
    }

    .btn-primary:active {
        transform: translateY(0);
    }

    .btn-back {
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 0.5rem;
        width: 100%;
        padding: 0.875rem 1.5rem;
        background: white;
        color: #6b7280;
        border: 2px solid #e5e7eb;
        border-radius: 12px;
        font-size: 0.95rem;
        font-weight: 500;
        text-decoration: none;
        transition: all 0.2s;
        margin-top: 1rem;
        box-sizing: border-box;
    }

    .btn-back:hover {
        background: #f9fafb;
        border-color: #d1d5db;
        color: #374151;
    }
</style>
