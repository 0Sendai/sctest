<script lang="ts">
    import { enhance } from '$app/forms';

    let { data, form } = $props();

    interface Slide {
        type: 'quiz' | 'text';
        question: string;
        variants: string[];
        correctVariantIndex?: number;
    }

    interface LoadedSlide {
        id: number;
        type: string | null;
        position: number;
        content: unknown;
    }

    const init = (() => {
        const initialSlides: Slide[] = data?.assignment && data?.slides
            ? data.slides.map((s: LoadedSlide) => {
                const content = s.content as any;
                return {
                    type: (s.type || 'quiz') as 'quiz' | 'text',
                    question: content?.question || '',
                    variants: content?.variants || [],
                    correctVariantIndex: content?.correctVariantIndex ?? 0,
                };
            })
            : [{
                type: 'quiz',
                question: '',
                variants: [''],
                correctVariantIndex: 0,
            }];

        const initialTitle = data?.assignment?.title || '';

        return { slides: initialSlides, title: initialTitle };
    })();
    let slides: Slide[] = $state.snapshot(init.slides);
    let hiddenValue = $derived(JSON.stringify(slides));
    let title = $state(init.title);
    let errorMsg = $state({ title: '', slides: [] as string[] });

    function addSlide() {
        slides.push({
            type: 'quiz',
            question: '',
            variants: [''],
            correctVariantIndex: 0,
        });
    }

    function removeSlide(index: number) {
        if (slides.length <= 1) return;
        slides.splice(index, 1);
    }

    function updateVariant(slideIndex: number, variantIndex: number, value: string) {
        slides[slideIndex].variants[variantIndex] = value;
    }

    function addVariant(slideIndex: number) {
        slides[slideIndex].variants.push('');
    }

    function removeVariant(slideIndex: number, variantIndex: number) {
        slides[slideIndex].variants.splice(variantIndex, 1);
        if (slides[slideIndex].variants.length === 0) {
            slides[slideIndex].variants.push('');
        }
    }

    function handleVariantEnter(slideIndex: number, e: KeyboardEvent) {
        if (e.key === 'Enter') {
            e.preventDefault();
            addVariant(slideIndex);
        }
    }

    function validateSlide(index: number): boolean {
        const slide = slides[index];
        if (!slide.question) {
            errorMsg.slides[index] = 'Вопрос обязателен';
        } else {
            errorMsg.slides[index] = '';
        }
        if (slide.type === 'quiz') {
            errorMsg.slides[index] = slide.variants.some(v => !v)
                ? 'Заполните все варианты'
                : errorMsg.slides[index];
        }
        
        return !errorMsg.slides[index];
    }

    function validateAll(): boolean {
        errorMsg.title = !title ? 'Заголовок обязателен' : '';
        let allValid = true;
        for (let i = 0; i < slides.length; i++) {
            if (!validateSlide(i)) {
                allValid = false;
            }
        }
        return allValid;
    }

    function enhanceFunc({ cancel }: { cancel: () => void }) {
        if (!validateAll()) {
            cancel();
            return;
        }
    }

    function disableEnter(e: KeyboardEvent) {
        if (e.key === 'Enter') {
            e.preventDefault();
        }
    }
</script>

<div class="edit-assignment-container">
    <div class="edit-assignment-card">
        <div class="card-header">
            <div class="logo-icon">
                <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
                    <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
                </svg>
            </div>
            <h1>Редактирование задания</h1>
            <p class="subtitle">Измените заголовок и настройте слайды</p>
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
                <label for="title">Заголовок задания</label>
                <input
                    type="text"
                    id="title"
                    name="title"
                    bind:value={title}
                    placeholder="Введите название задания"
                    class:error={errorMsg.title}
                    onkeydown={disableEnter}
                    autocomplete="off"/>
                {#if errorMsg.title}
                    <span class="field-error">{errorMsg.title}</span>
                {/if}
            </div>

            <div class="slides-section">
                <div class="section-header">
                    <div class="section-label">Слайды ({slides.length})</div>
                </div>

                {#each slides as slide, index}
                    <div class="slide-card" style="animation-delay: {index * 0.05}s">
                        <div class="slide-header">
                            <div class="slide-number">Слайд {index + 1}</div>
                            <button type="button" onclick={() => removeSlide(index)} class="btn-remove-slide" aria-label="Удалить слайд">
                                <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                    <line x1="18" y1="6" x2="6" y2="18"/>
                                    <line x1="6" y1="6" x2="18" y2="18"/>
                                </svg>
                            </button>
                        </div>

                        <div class="form-group">
                            <label for={`slide-type-${index}`}>Тип слайда</label>
                            <select
                                id={`slide-type-${index}`}
                                bind:value={slide.type}>
                                <option value="quiz">Опрос (Quiz)</option>
                                <option value="text">Вопрос с текстовым ответом</option>
                            </select>
                        </div>

                        <div class="form-group">
                            <label for={`slide-question-${index}`}>Вопрос</label>
                            <input
                                type="text"
                                id={`slide-question-${index}`}
                                bind:value={slide.question}
                                placeholder="Введите вопрос"
                                class:error={errorMsg.slides[index]}
                                onkeydown={disableEnter}
                                autocomplete="off"/>
                            {#if errorMsg.slides[index]}
                                <span class="field-error">{errorMsg.slides[index]}</span>
                            {/if}
                        </div>

                        {#if slide.type === 'quiz'}
                            <div class="variants-section">
                                <div class="section-label">Варианты ответа</div>
                                {#each slide.variants as variant, variantIndex}
                                    <div class="variant-row">
                                        <input
                                            type="text"
                                            bind:value={slide.variants[variantIndex]}
                                            placeholder={`Вариант ${variantIndex + 1}`}
                                            class:error={errorMsg.slides[index]}
                                            onkeydown={(e) => handleVariantEnter(index, e)}/>
                                        <label class="correct-answer-label" title="Правильный ответ">
                                            <input
                                                type="radio"
                                                checked={slide.correctVariantIndex === variantIndex}
                                                onchange={() => { slide.correctVariantIndex = variantIndex; }}/>
                                            <span class="radio-custom"></span>
                                        </label>
                                        <button type="button" onclick={() => removeVariant(index, variantIndex)} class="btn-remove-variant" aria-label="Удалить вариант">
                                            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                                <line x1="18" y1="6" x2="6" y2="18"/>
                                                <line x1="6" y1="6" x2="18" y2="18"/>
                                            </svg>
                                        </button>
                                    </div>
                                {/each}
                                <button type="button" onclick={() => addVariant(index)} class="btn-add-variant">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                        <line x1="12" y1="5" x2="12" y2="19"/>
                                        <line x1="5" y1="12" x2="19" y2="12"/>
                                    </svg>
                                    Добавить вариант
                                </button>
                            </div>
                        {/if}

                    </div>
                {/each}
            </div>

            <input type="hidden" id="slides-json" name="slides" bind:value={hiddenValue}/>

            <button type="button" onclick={addSlide} class="btn-add-slide">
                <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <line x1="12" y1="5" x2="12" y2="19"/>
                    <line x1="5" y1="12" x2="19" y2="12"/>
                </svg>
                Добавить слайд
            </button>

            <button type="submit" class="btn-primary">
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M19 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11l5 5v11a2 2 0 0 1-2 2z"/>
                    <polyline points="17 21 17 13 7 13 7 21"/>
                    <polyline points="7 3 7 8 15 8"/>
                </svg>
                Сохранить изменения
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
    .edit-assignment-container {
        min-height: 100vh;
        display: flex;
        align-items: flex-start;
        justify-content: center;
        background: linear-gradient(135deg, #f5f8fc 0%, #eef2f7 100%);
        padding: 2rem 1rem;
    }

    .edit-assignment-card {
        background: white;
        border-radius: 16px;
        box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1),
                    0 2px 4px -1px rgba(0, 0, 0, 0.06),
                    0 0 0 1px rgba(142, 177, 209, 0.1);
        padding: 2.5rem;
        width: 100%;
        max-width: 640px;
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

    .slides-section {
        margin-bottom: 1.5rem;
    }

    .section-header {
        display: flex;
        align-items: center;
        justify-content: space-between;
        margin-bottom: 1rem;
    }

    .section-label {
        font-size: 1rem;
        font-weight: 600;
        color: #1a1a2e;
    }

    .btn-add-slide {
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 0.5rem;
        padding: 0.625rem 1rem;
        background: white;
        border: 2px dashed #8eb1d1;
        border-radius: 8px;
        color: #8eb1d1;
        font-size: 0.875rem;
        font-weight: 500;
        cursor: pointer;
        transition: all 0.2s;
        box-sizing: border-box;
    }

    .btn-add-slide:hover {
        background: #f0f7ff;
        border-style: solid;
    }

    .slide-card {
        background: #f8fafc;
        border: 2px solid #e5e7eb;
        border-radius: 12px;
        padding: 1.5rem;
        margin-bottom: 1rem;
        animation: fadeIn 0.3s ease-out forwards;
    }

    @keyframes fadeIn {
        from {
            opacity: 0;
            transform: translateY(10px);
        }
        to {
            opacity: 1;
            transform: translateY(0);
        }
    }

    .slide-header {
        display: flex;
        align-items: center;
        justify-content: space-between;
        margin-bottom: 1rem;
        padding-bottom: 0.75rem;
        border-bottom: 1px solid #e5e7eb;
    }

    .slide-number {
        font-size: 0.95rem;
        font-weight: 600;
        color: #8eb1d1;
    }

    .btn-remove-slide {
        display: flex;
        align-items: center;
        justify-content: center;
        width: 36px;
        height: 36px;
        background: #fef2f2;
        border: 2px solid #fecaca;
        border-radius: 8px;
        color: #dc2626;
        cursor: pointer;
        transition: all 0.2s;
    }

    .btn-remove-slide:hover {
        background: #fee2e2;
        border-color: #dc2626;
    }

    .variants-section {
        margin-top: 1rem;
    }

    .variants-section .section-label {
        margin-bottom: 0.75rem;
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

    .btn-remove-variant {
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

    .btn-remove-variant:hover {
        background: #fee2e2;
        border-color: #dc2626;
    }

    .btn-add-variant {
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 0.5rem;
        width: 100%;
        padding: 0.625rem 1rem;
        background: white;
        border: 2px dashed #8eb1d1;
        border-radius: 8px;
        color: #8eb1d1;
        font-size: 0.875rem;
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

    .correct-answer-label {
        display: flex;
        align-items: center;
        justify-content: center;
        width: 42px;
        flex-shrink: 0;
        cursor: pointer;
    }

    .correct-answer-label input[type="radio"] {
        position: absolute;
        opacity: 0;
        width: 0;
        height: 0;
    }

    .radio-custom {
        width: 20px;
        height: 20px;
        border: 2px solid #d1d5db;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        transition: all 0.2s;
    }

    .correct-answer-label input[type="radio"]:checked + .radio-custom {
        border-color: #7aa3c4;
        background: #7aa3c4;
    }

    .correct-answer-label input[type="radio"]:checked + .radio-custom::after {
        content: '';
        width: 8px;
        height: 8px;
        background: white;
        border-radius: 50%;
    }

    .correct-answer-label:hover .radio-custom {
        border-color: #8eb1d1;
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
