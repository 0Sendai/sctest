<script lang="ts">
    import { enhance } from '$app/forms';
    import { apiFetch } from '#lib/api.ts';
    import LatexInput from '#lib/components/LatexInput.svelte';
    interface Student {
        id: number;
        name: string;
    }
    let { form, data } = $props();


    interface Slide {
        type: 'quiz' | 'text';
        question: string;
        variants: string[];
        correctVariantIndex?: number;  // индекс правильного варианта для quiz
    }

    let slides = $state<Slide[]>([{
        type: 'quiz',
        question: '',
        variants: [''],
        correctVariantIndex: 0,
    }]);

    //let hiddenValue = $derived(JSON.stringify(slides));
    let title = $state('');
    let errorMsg = $state({ title: '', slides: [] as string[] });
    let error = $state('');

    // Student selection state
    let selectedStudentIds = $state<number[]>([]);
    let searchQuery = $state('');

    function hasStudentId(id: number): boolean {
        return selectedStudentIds.includes(id);
    }

    const filteredStudents = $derived(
        data.students.filter((s: Student) =>
            s.name.toLowerCase().includes(searchQuery.toLowerCase())
        )
    );

    const allSelected = $derived(
        filteredStudents.length > 0 &&
        filteredStudents.every((s: Student) => hasStudentId(s.id))
    );

    const partiallySelected = $derived(
        !allSelected && filteredStudents.some((s: Student) => hasStudentId(s.id))
    );

    function toggleSelectAll() {
        if (allSelected) {
            selectedStudentIds = filteredStudents.map((s: Student) => s.id);
        } else {
            selectedStudentIds = [];
        }
    }

    function toggleStudent(studentId: number) {
        const index = selectedStudentIds.indexOf(studentId);
        if (index > -1) {
            selectedStudentIds = selectedStudentIds.filter(id => id !== studentId);
        } else {
            selectedStudentIds = [...selectedStudentIds, studentId];
        }
    }

    function getSelectedCount() {
        return selectedStudentIds.length;
    }

    //let studentIdsJson = $derived(JSON.stringify(selectedStudentIds));

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

    async function handleSubmit(event: SubmitEvent) {
        event.preventDefault();
        if (!validateAll()) return;
        const response = await apiFetch('/api/teacher/assignments/create', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json'},
            body: JSON.stringify({
                teacherId: data.teacherId,
                title: title,
                slides: slides,
                studentIds: selectedStudentIds
            })
        });
        const msg = await response.text();
        if (!response.ok) {
            error = msg;
        } else {
            window.location.href = "/teacher/assignments";
        }
    }

</script>

<div class="new-assignment-container">
    <div class="new-assignment-card">
        <div class="card-header">
            <div class="logo-icon">
                <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"/>
                    <path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"/>
                </svg>
            </div>
            <h1>Создание задания</h1>
            <p class="subtitle">Добавьте заголовок и настройте слайды</p>
        </div>

        {#if error}
            <div class="error-banner">
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <circle cx="12" cy="12" r="10"/>
                    <line x1="15" y1="9" x2="9" y2="15"/>
                    <line x1="9" y1="9" x2="15" y2="15"/>
                </svg>
                <span>{error}</span>
            </div>
        {/if}

        <form method="POST" 
         //use:enhance={enhanceFunc}>
         onsubmit={handleSubmit}>
        
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
                    <div class="section-label">Слайды</div>
                </div>

                {#each slides as slide, index}
                    <div class="slide-card" style="animation-delay: {index * 0.05}s">
                        <div class="slide-header">
                            <div class="slide-number">Слайд {index + 1}</div>
                            {#if index > 0}
                                <button type="button" onclick={() => removeSlide(index)} class="btn-remove-slide" aria-label="Удалить слайд">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                        <line x1="18" y1="6" x2="6" y2="18"/>
                                        <line x1="6" y1="6" x2="18" y2="18"/>
                                    </svg>
                                </button>
                            {/if}
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
                            <!-- <input
                                type="text"
                                id={`slide-question-${index}`}

                                bind:value={slide.question}
                                placeholder="Введите вопрос"
                                class:error={errorMsg.slides[index]}
                                onkeydown={disableEnter}
                                autocomplete="off"/> -->
                            <LatexInput bind:input={slide.question}
                                placeholder="Введите вопрос"
                                class={errorMsg.slides[index] ? 'error' : ''}
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
                                        <!-- <input
                                            type="text"
                                            bind:value={slide.variants[variantIndex]}
                                            placeholder={`Вариант ${variantIndex + 1}`}
                                            class:error={errorMsg.slides[index]}
                                            onkeydown={(e) => handleVariantEnter(index, e)}/> -->

                                        <LatexInput
                                            bind:input={slide.variants[variantIndex]}
                                            placeholder={`Вариант ${variantIndex + 1}`}
                                            class={errorMsg.slides[index] ? 'error': ''}
                                            onkeydown={(e: KeyboardEvent) => handleVariantEnter(index, e)}/>
                                        
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

            <!--
            <input type="hidden" id="slides-json" name="slides" bind:value={hiddenValue}/>
            -->
            <!-- Student Selection Section -->
            <div class="students-section">
                <div class="section-header">
                    <div class="section-label">
                        Назначить студентам
                        {#if getSelectedCount() > 0}
                            <span class="selected-count">({getSelectedCount()})</span>
                        {/if}
                    </div>
                </div>

                <!-- Search -->
                <div class="student-search">
                    <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <circle cx="11" cy="11" r="8"/>
                        <line x1="21" y1="21" x2="16.65" y2="16.65"/>
                    </svg>
                    <input
                        type="text"
                        bind:value={searchQuery}
                        placeholder="Поиск студента..."
                        class="student-search-input"/>
                </div>

                <!-- Select All -->
                <label class="select-all-row">
                    <div class="custom-checkbox" onclick={toggleSelectAll} onkeydown={(e) => { if (e.key === 'Enter' || e.key === ' ') { e.preventDefault(); toggleSelectAll(); } }} role="checkbox" aria-checked={allSelected} tabindex="0">
                        <input type="checkbox" checked={allSelected} readonly/>
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round">
                            <polyline points="20 6 9 17 4 12"/>
                        </svg>
                    </div>
                    <span class="select-all-label">Выбрать всех</span>
                </label>

                <!-- Students List -->
                <div class="students-list">
                    {#if filteredStudents.length === 0}
                        <div class="no-students">Студентов не найдено</div>
                    {:else}
                        {#each filteredStudents as student (student.id)}
                            <label class="student-row" class:student-selected={hasStudentId(student.id)}>
                                <div class="custom-checkbox" onclick={() => toggleStudent(student.id)} onkeydown={(e) => { if (e.key === 'Enter' || e.key === ' ') { e.preventDefault(); toggleStudent(student.id); } }} role="checkbox" aria-checked={hasStudentId(student.id)} tabindex="0">
                                    <input type="checkbox" checked={hasStudentId(student.id)} readonly/>
                                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round">
                                        <polyline points="20 6 9 17 4 12"/>
                                    </svg>
                                </div>
                                <div class="student-avatar-small">
                                    {student.name.charAt(0).toUpperCase()}
                                </div>
                                <span class="student-name-small">{student.name}</span>
                            </label>
                        {/each}
                    {/if}
                </div>
            </div>

            <button type="button" onclick={addSlide} class="btn-add-slide">
                <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <line x1="12" y1="5" x2="12" y2="19"/>
                    <line x1="5" y1="12" x2="19" y2="12"/>
                </svg>
                Добавить слайд
            </button>

            <!-- Hidden input for selected student IDs (JSON) -->
             <!--
            <input type="hidden" name="studentIdsJson" bind:value={studentIdsJson}/>
            -->
            <button type="submit" class="btn-primary">
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"/>
                    <path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"/>
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
        height: 42px;
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

    /* Student Selection Section */
    .students-section {
        margin-bottom: 1.5rem;
        padding-top: 1rem;
        border-top: 2px solid #e5e7eb;
    }

    .students-section .section-header {
        display: flex;
        align-items: center;
        justify-content: space-between;
        margin-bottom: 1rem;
    }

    .students-section .section-label {
        font-size: 1rem;
        font-weight: 600;
        color: #1a1a2e;
        display: flex;
        align-items: center;
        gap: 0.5rem;
    }

    .selected-count {
        font-size: 0.8rem;
        font-weight: 500;
        color: #7aa3c4;
        background: #e8f2f8;
        padding: 0.15rem 0.5rem;
        border-radius: 9999px;
    }

    /* Student Search */
    .student-search {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        background: #f8fafc;
        border: 2px solid #e5e7eb;
        border-radius: 8px;
        padding: 0.5rem 0.75rem;
        margin-bottom: 0.75rem;
        transition: all 0.2s;
    }

    .student-search:focus-within {
        border-color: #8eb1d1;
        box-shadow: 0 0 0 3px rgba(142, 177, 209, 0.1);
        background: white;
    }

    .student-search svg {
        color: #9ca3af;
        flex-shrink: 0;
    }

    .student-search-input {
        flex: 1;
        border: none;
        outline: none;
        background: transparent;
        font-size: 0.9rem;
        color: #374151;
    }

    .student-search-input::placeholder {
        color: #9ca3af;
    }

    /* Select All Row */
    .select-all-row {
        display: flex;
        align-items: center;
        gap: 0.75rem;
        padding: 0.625rem 0.75rem;
        background: #f0f7ff;
        border: 2px solid #dbeafe;
        border-radius: 8px;
        margin-bottom: 0.5rem;
        cursor: pointer;
        transition: all 0.2s;
    }

    .select-all-row:hover {
        background: #e0efff;
        border-color: #8eb1d1;
    }

    .select-all-label {
        font-size: 0.875rem;
        font-weight: 600;
        color: #374151;
    }

    /* Custom Checkbox */
    .custom-checkbox {
        position: relative;
        width: 20px;
        height: 20px;
        flex-shrink: 0;
        cursor: pointer;
    }

    .custom-checkbox input {
        position: absolute;
        opacity: 0;
        width: 0;
        height: 0;
        pointer-events: none;
    }

    .custom-checkbox svg {
        position: absolute;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        width: 20px;
        height: 20px;
        opacity: 0;
        transition: opacity 0.15s;
        color: white;
    }

    .custom-checkbox input:checked + svg {
        opacity: 1;
    }

    .custom-checkbox::before {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        width: 20px;
        height: 20px;
        border: 2px solid #d1d5db;
        border-radius: 4px;
        background: white;
        transition: all 0.15s;
    }

    .custom-checkbox:hover::before {
        border-color: #8eb1d1;
    }

    .custom-checkbox:has(input:checked)::before {
        background: #7aa3c4;
        border-color: #7aa3c4;
    }

    /* Students List */
    .students-list {
        max-height: 240px;
        overflow-y: auto;
        border: 2px solid #e5e7eb;
        border-radius: 8px;
        background: white;
    }

    .students-list::-webkit-scrollbar {
        width: 6px;
    }

    .students-list::-webkit-scrollbar-track {
        background: #f1f5f9;
        border-radius: 3px;
    }

    .students-list::-webkit-scrollbar-thumb {
        background: #cbd5e1;
        border-radius: 3px;
    }

    .students-list::-webkit-scrollbar-thumb:hover {
        background: #94a3b8;
    }

    /* Student Row */
    .student-row {
        display: flex;
        align-items: center;
        gap: 0.75rem;
        padding: 0.625rem 0.75rem;
        cursor: pointer;
        transition: all 0.15s;
        border-bottom: 1px solid #f1f5f9;
    }

    .student-row:last-child {
        border-bottom: none;
    }

    .student-row:hover {
        background: #f8fafc;
    }

    .student-row.student-selected {
        background: #f0f7ff;
    }

    .student-row.student-selected:hover {
        background: #e0efff;
    }

    .student-avatar-small {
        width: 32px;
        height: 32px;
        background: linear-gradient(135deg, #8eb1d1 0%, #7aa3c4 100%);
        border-radius: 8px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: white;
        font-weight: 600;
        font-size: 0.875rem;
        flex-shrink: 0;
    }

    .student-name-small {
        font-size: 0.9rem;
        color: #374151;
        font-weight: 500;
    }

    .no-students {
        padding: 1.5rem;
        text-align: center;
        color: #9ca3af;
        font-size: 0.9rem;
    }

    @media (max-width: 640px) {
        .new-assignment-card {
            padding: 1.5rem;
        }
    }
</style>
