<script lang="ts">
    import { enhance } from '$app/forms';

    let { form } = $props();
    let slide_type: 'quiz' | 'text' = $state('quiz');
    let question = $state('');
    let variants = $state(['']);
    let errorMsg = $state({ question: '', variants: ''});

    function addVariant() {
        variants = [...variants, ''];
    }

    function removeVariant(index: number) {
        variants.splice(index, 1);
    }

    function handleVariantEnter(e: KeyboardEvent) {
        if (e.key === 'Enter') {
            e.preventDefault();
            addVariant();
        }
    }

    function enhanceFunc({ cancel }: { cancel: () => void }) {
        if (!validateInput()) cancel();
    }

    function validateInput(): boolean {
        errorMsg.question = !question ? 'Вопрос обязателен' : '';
        if (slide_type === 'quiz') {
            errorMsg.variants = !variants.every((variant) => variant)
                                ? 'Заполните все варианты' : '';
            return !errorMsg.question && !errorMsg.variants;
        }
        return !errorMsg.question;
    }

    function disableEnter(e: KeyboardEvent) { 
        if (e.key === 'Enter') e.preventDefault(); 
    }
</script>

<div class="new-assignment-container">
    <div class="new-assignment-card">
        <div class="card-header">
            <div class="logo-icon">
                <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M12 20h9"/>
                    <path d="M16.5 3.5a2.121 2.121 0 0 1 3 3L7 19l-4 1 1-4L16.5 3.5z"/>
                </svg>
            </div>
            <h1>Создание задания</h1>
            <p class="subtitle">Настройте параметры нового слайда</p>
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

        <form method="POST" use:enhance={enhanceFunc}>
            <div class="form-group">
                <label for="type">Тип слайда</label>
                <select id="type" name="type" bind:value={slide_type}>
                    <option value="quiz">Опрос (Quiz)</option>
                    <option value="text">Вопрос с текстовым ответом</option>
                </select>
            </div>

            <div class="form-group">
                <label for="question">Вопрос</label>
                <input 
                    type="text" 
                    id="question"
                    name="question" 
                    bind:value={question}
                    placeholder="Введите вопрос"
                    class:error={errorMsg.question}
                    onkeydown={disableEnter}
                    autocomplete="off"/>
                {#if errorMsg.question}
                    <span class="field-error">{errorMsg.question}</span>
                {/if}
            </div>

            {#if slide_type === 'quiz'}
                <div class="variants-section">
                    <div class="section-label">Варианты ответа</div>
                    {#each variants as variant, index}
                        <div class="variant-row">
                            <input 
                                type="text" 
                                name="variants"
                                bind:value={variants[index]}
                                placeholder={`Вариант ${index + 1}`}
                                class:error={errorMsg.variants}
                                onkeydown={handleVariantEnter}/>
                            <button type="button" onclick={() => removeVariant(index)} class="btn-remove" aria-label="Удалить вариант">
                                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                    <line x1="18" y1="6" x2="6" y2="18"/>
                                    <line x1="6" y1="6" x2="18" y2="18"/>
                                </svg>
                            </button>
                        </div>
                    {/each}
                    {#if errorMsg.variants}
                        <span class="field-error">{errorMsg.variants}</span>
                    {/if}
                    <button type="button" onclick={addVariant} class="btn-add-variant">
                        <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <line x1="12" y1="5" x2="12" y2="19"/>
                            <line x1="5" y1="12" x2="19" y2="12"/>
                        </svg>
                        Добавить вариант
                    </button>
                </div>
            {/if}

            <button type="submit" class="btn-primary">
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M12 20h9"/>
                    <path d="M16.5 3.5a2.121 2.121 0 0 1 3 3L7 19l-4 1 1-4L16.5 3.5z"/>
                </svg>
                Создать задание
            </button>
        </form>

        <a href="../" class="btn-back">
            <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <line x1="19" y1="12" x2="5" y2="12"/>
                <polyline points="12 19 5 12 12 5"/>
            </svg>
            Назад к заданиям
        </a>
    </div>
</div>

<style>
    .new-assignment-container {
        min-height: 100vh;
        display: flex;
        align-items: flex-start;
        justify-content: center;
        background: linear-gradient(135deg, #f5f8fc 0%, #eef2f7 100%);
        padding: 2rem 1rem;
    }

    .new-assignment-card {
        background: white;
        border-radius: 16px;
        box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1),
                    0 2px 4px -1px rgba(0, 0, 0, 0.06),
                    0 0 0 1px rgba(142, 177, 209, 0.1);
        padding: 2.5rem;
        width: 100%;
        max-width: 560px;
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

    .form-group input,
    .form-group select {
        width: 100%;
        padding: 0.75rem 1rem;
        border: 2px solid #e5e7eb;
        border-radius: 8px;
        font-size: 1rem;
        transition: all 0.2s;
        outline: none;
        box-sizing: border-box;
        background: white;
    }

    .form-group input:focus,
    .form-group select:focus {
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

    .variants-section {
        margin-bottom: 1.5rem;
    }

    .section-label {
        display: block;
        margin-bottom: 0.75rem;
        font-size: 0.875rem;
        font-weight: 500;
        color: #374151;
    }

    .variant-row {
        display: flex;
        gap: 0.5rem;
        margin-bottom: 0.75rem;
    }

    .variant-row input {
        flex: 1;
        padding: 0.75rem 1rem;
        border: 2px solid #e5e7eb;
        border-radius: 8px;
        font-size: 1rem;
        transition: all 0.2s;
        outline: none;
        box-sizing: border-box;
    }

    .variant-row input:focus {
        border-color: #8eb1d1;
        box-shadow: 0 0 0 3px rgba(142, 177, 209, 0.1);
    }

    .btn-remove {
        width: 42px;
        height: 42px;
        display: flex;
        align-items: center;
        justify-content: center;
        background: #fef2f2;
        border: 2px solid #fecaca;
        border-radius: 8px;
        color: #dc2626;
        cursor: pointer;
        transition: all 0.2s;
        flex-shrink: 0;
    }

    .btn-remove:hover {
        background: #fee2e2;
        border-color: #dc2626;
    }

    .btn-add-variant {
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 0.5rem;
        width: 100%;
        padding: 0.75rem 1rem;
        background: white;
        border: 2px dashed #8eb1d1;
        border-radius: 8px;
        color: #8eb1d1;
        font-size: 0.9rem;
        font-weight: 500;
        cursor: pointer;
        transition: all 0.2s;
        margin-top: 0.5rem;
        box-sizing: border-box;
    }

    .btn-add-variant:hover {
        background: #f0f7ff;
        border-style: solid;
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
        margin-top: 1.5rem;
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
