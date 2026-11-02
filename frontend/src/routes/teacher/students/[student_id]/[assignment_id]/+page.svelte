<script lang="ts">
    import { type ActionData } from './$types';
    let { data } = $props();

    const student = $derived(data.student);
    const assignment = $derived(data.assignment);
    const result = $derived(data.result);
    const slides = $derived(data.slides);
    const slideComments = $derived(data.slideComments);
    const stats = $derived(data.stats);

    function getStatusBadge(status: string) {
        switch (status) {
            case 'completed':
                return { text: 'Завершено', class: 'status-completed' };
            case 'in_progress':
                return { text: 'В процессе', class: 'status-in-progress' };
            default:
                return { text: status, class: '' };
        }
    }

    function formatAnswer(slide: any) {
        if (slide.studentAnswer === null || slide.studentAnswer === undefined) {
            return '<em>Нет ответа</em>';
        }
        return slide.studentAnswer;
    }

    function getCorrectnessIcon(isCorrect: boolean | null) {
        if (isCorrect === null) return null;
        return isCorrect ? '✓' : '✗';
    }

    function getCorrectnessClass(isCorrect: boolean | null) {
        if (isCorrect === null) return '';
        return isCorrect ? 'correct' : 'incorrect';
    }

    // Состояние комментариев
    let saveMessage = $state<{ type: 'success' | 'error'; text: string } | null>(null);
    let isSaving = $state(false);

    // Состояние отметок правильности для каждого слайда
    // slideId -> true (правильно) | false (неправильно) | null (не отмечено)
    let correctnessStates = $state<Record<number, boolean | null>>({});
    let _initialized = false;

    // Инициализируем из данных при загрузке
    $effect(() => {
        if (!_initialized && data.slides) {
            for (const slide of data.slides) {
                correctnessStates[slide.id] = slide.isCorrect !== null ? slide.isCorrect : null;
            }
            _initialized = true;
        }
    });

    function toggleCorrectness(slideId: number, value: boolean) {
        const current = correctnessStates[slideId];
        // Если уже стоит такая отметка — снимаем её
        if (current === value) {
            correctnessStates[slideId] = null;
        } else {
            correctnessStates[slideId] = value;
        }
    }

    // Собираем все отметки правильности
    function getAllCorrectnesses(): Array<{ slideId: number; isCorrect: boolean }> {
        const correctnesses: Array<{ slideId: number; isCorrect: boolean }> = [];
        for (const [slideId, isCorrect] of Object.entries(correctnessStates)) {
            if (isCorrect !== null) {
                correctnesses.push({ slideId: Number(slideId), isCorrect: isCorrect });
            }
        }
        return correctnesses;
    }

    function getComment(slideId: number): string {
        return slideComments?.[slideId] || '';
    }

    // Собираем все комментарии из textarea
    function getAllComments(): Array<{ slideId: number; comment: string }> {
        const textareas = document.querySelectorAll('.comment-input');
        const comments: Array<{ slideId: number; comment: string }> = [];
        textareas.forEach((textarea: Element) => {
            const ta = textarea as HTMLTextAreaElement;
            const slideId = parseInt(ta.dataset.slideId || '0');
            const comment = ta.value;
            if (slideId) {
                comments.push({ slideId: slideId, comment });
            }
        });
        return comments;
    }

    // Сохраняем все комментарии и отметки через fetch
    async function saveAllComments() {
        const comments = getAllComments();
        const correctnesses = getAllCorrectnesses();

        // Проверяем, есть ли что сохранять
        if (comments.length === 0 && correctnesses.length === 0) {
            saveMessage = { type: 'error', text: 'Нет данных для сохранения' };
            setTimeout(() => { saveMessage = null; }, 3000);
            return;
        }

        isSaving = true;
        saveMessage = null;

        try {
            // const formData = new FormData();
            // formData.append('assignmentId', assignment.id);
            // formData.append('studentId', student.id.toString());
            // if (comments.length > 0) {
            //     formData.append('comments', JSON.stringify(comments));
            // }
            // if (correctnesses.length > 0) {
            //     formData.append('correctnesses', JSON.stringify(correctnesses));
            // }

            const response = await fetch(`/api/teacher/students/student/assignment`, {
                method: 'POST',
                headers: {
                    "Content-Type": "application/json" 
                },
                body: JSON.stringify({
                    assignmentId: assignment.id,
                    studentId: student.id,
                    comments: comments,
                    correctnesses: correctnesses
                }),
            });

            if (response.ok) {
                const result = await response.json();
                // Обновляем состояния правильности из ответа сервера
                if (result.updatedSlides) {
                    for (const update of result.updatedSlides) {
                        correctnessStates[update.slideId] = update.isCorrect;
                    }
                }
                saveMessage = { type: 'success', text: 'Все изменения сохранены' };
                setTimeout(() => { saveMessage = null; }, 3000);
            } else {
                saveMessage = { type: 'error', text: 'Ошибка при сохранении' };
            }
        } catch (error) {
            console.error('Error saving:', error);
            saveMessage = { type: 'error', text: 'Ошибка сети' };
        } finally {
            isSaving = false;
        }
    }
</script>

<div class="results-container">
    <div class="results-card">
        <!-- Header -->
        <div class="results-header">
            <a href="../../" class="btn-back">
                <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <line x1="19" y1="12" x2="5" y2="12"/>
                    <polyline points="12 19 5 12 12 5"/>
                </svg>
                Назад к списку студентов
            </a>

            <div class="header-info">
                <div class="header-top">
                    <div class="student-badge">
                        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                            <circle cx="12" cy="7" r="4"/>
                        </svg>
                        <span>{student?.name}</span>
                    </div>

                    {#if result}
                        <div class="status-badge {getStatusBadge(result.status).class}">
                            {getStatusBadge(result.status).text}
                        </div>
                    {/if}
                </div>

                <h1 class="assignment-title">{assignment?.title}</h1>

                {#if result}
                    <div class="result-meta">
                        <span>Начало: {new Date(result.startedAt).toLocaleString('ru-RU')}</span>
                        {#if result.completedAt}
                            <span>Завершение: {new Date(result.completedAt).toLocaleString('ru-RU')}</span>
                        {/if}
                    </div>
                {/if}
            </div>
        </div>

        <!-- Stats -->
        {#if result && stats}
            <div class="stats-grid">
                <div class="stat-card stat-total">
                    <div class="stat-value">{stats.total}</div>
                    <div class="stat-label">Всего слайдов</div>
                </div>
                <div class="stat-card stat-answered">
                    <div class="stat-value">{stats.answered}</div>
                    <div class="stat-label">Отвечено</div>
                </div>
                <div class="stat-card stat-correct">
                    <div class="stat-value correct">{stats.correct}</div>
                    <div class="stat-label">Правильно</div>
                </div>
                <div class="stat-card stat-incorrect">
                    <div class="stat-value incorrect">{stats.incorrect}</div>
                    <div class="stat-label">Неправильно</div>
                </div>
                <div class="stat-card stat-unanswered">
                    <div class="stat-value">{stats.unanswered}</div>
                    <div class="stat-label">Пропущено</div>
                </div>
            </div>
        {/if}

        <!-- Slides Results -->
        <div class="slides-results">
            <h2 class="section-title">Ответы на слайды</h2>

            {#if !result}
                <div class="no-result">
                    <svg xmlns="http://www.w3.org/2000/svg" width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                        <circle cx="12" cy="12" r="10"/>
                        <line x1="12" y1="8" x2="12" y2="12"/>
                        <line x1="12" y1="16" x2="12.01" y2="16"/>
                    </svg>
                    <p>Студент ещё не начинал это задание</p>
                </div>
            {:else}
                {#each slides as slide, index (slide.id)}
                    <div class="slide-result-card">
                        <div class="slide-result-header">
                            <div class="slide-number">
                                Слайд {index + 1}
                                <span class="slide-type-badge">{slide.type === 'quiz' ? 'Опрос' : 'Текстовый'}</span>
                            </div>
                            <div class="correctness-controls">
                                {#if slide.type === 'text'}
                                    <!-- Кнопки для текстовых слайдов -->
                                    <button
                                        class="correctness-btn correctness-btn-correct {correctnessStates[slide.id] === true ? 'active' : ''}"
                                        onclick={() => toggleCorrectness(slide.id, true)}
                                        title="Отметить как правильный"
                                    >
                                        <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                            <polyline points="20 6 9 17 4 12"/>
                                        </svg>
                                        Правильно
                                    </button>
                                    <button
                                        class="correctness-btn correctness-btn-incorrect {correctnessStates[slide.id] === false ? 'active' : ''}"
                                        onclick={() => toggleCorrectness(slide.id, false)}
                                        title="Отметить как неправильный"
                                    >
                                        <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                            <line x1="18" y1="6" x2="6" y2="18"/>
                                            <line x1="6" y1="6" x2="18" y2="18"/>
                                        </svg>
                                        Неправильно
                                    </button>
                                {/if}
                            </div>
                        </div>

                        <div class="slide-question">
                            {#if slide.type === 'quiz'}
                                {(slide.content as any).question}
                                <div class="slide-variants">
                                    {#each (slide.content as any).variants as variant, vIndex (variant)}
                                        <div class="variant-item {slide.studentAnswer === variant ? (slide.isCorrect ? 'variant-correct' : 'variant-incorrect') : ''}">
                                            {variant}
                                            {#if slide.studentAnswer === variant}
                                                <span class="variant-icon">{slide.isCorrect ? '✓' : '✗'}</span>
                                            {/if}
                                        </div>
                                    {/each}
                                </div>
                            {:else}
                                {(slide.content as any).question}
                            {/if}
                        </div>

                        <div class="slide-answer">
                            <div class="answer-label">Ответ студента:</div>
                            <div class="answer-content {slide.isCorrect ?? ''}">
                                {#if slide.studentAnswer === null || slide.studentAnswer === undefined}
                                    <span class="no-answer">Нет ответа</span>
                                {:else}
                                    {slide.studentAnswer}
                                {/if}
                            </div>
                        </div>

                        {#if slide.answeredAt}
                            <div class="slide-timestamp">
                                Ответ дан: {new Date(slide.answeredAt).toLocaleString('ru-RU')}
                            </div>
                        {/if}

                        <!-- Поле для комментария преподавателя -->
                        <div class="comment-section">
                            <div class="comment-header">
                                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                    <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/>
                                </svg>
                                <span>Комментарий преподавателя:</span>
                            </div>
                            <textarea
                                class="comment-input"
                                data-slide-id={slide.id}
                                placeholder="Оставьте комментарий..."
                                rows={3}
                            >{getComment(slide.id)}</textarea>
                        </div>
                    </div>
                {/each}
            {/if}

            <!-- Кнопка сохранения всех комментариев -->
            <div class="save-all-section">
                {#if saveMessage}
                    <div class="save-message {saveMessage.type}">
                        {saveMessage.text}
                    </div>
                {/if}
                <button
                    class="btn-save-all"
                    onclick={saveAllComments}
                    disabled={isSaving}
                >
                    {#if isSaving}
                        <span class="spinner"></span>
                        Сохранение...
                    {:else}
                        <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <path d="M19 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11l5 5v11a2 2 0 0 1-2 2z"/>
                            <polyline points="17 21 17 13 12 18 7 13 12 21"/>
                        </svg>
                        Сохранить все комментарии
                    {/if}
                </button>
            </div>
        </div>
    </div>
</div>

<style>
    .results-container {
        min-height: 100vh;
        display: flex;
        align-items: flex-start;
        justify-content: center;
        background: linear-gradient(135deg, #f5f8fc 0%, #eef2f7 100%);
        padding: 2rem 1rem;
    }

    .results-card {
        background: white;
        border-radius: 16px;
        box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1),
                    0 2px 4px -1px rgba(0, 0, 0, 0.06),
                    0 0 0 1px rgba(142, 177, 209, 0.1);
        padding: 2.5rem;
        width: 100%;
        max-width: 800px;
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

    .results-header {
        margin-bottom: 2rem;
    }

    .btn-back {
        display: inline-flex;
        align-items: center;
        gap: 0.5rem;
        padding: 0.5rem 1rem;
        background: white;
        color: #6b7280;
        border: 2px solid #e5e7eb;
        border-radius: 8px;
        font-size: 0.875rem;
        font-weight: 500;
        text-decoration: none;
        transition: all 0.2s;
        margin-bottom: 1.5rem;
    }

    .btn-back:hover {
        background: #f9fafb;
        border-color: #d1d5db;
        color: #374151;
    }

    .header-info {
        display: flex;
        flex-direction: column;
        gap: 1rem;
    }

    .header-top {
        display: flex;
        align-items: center;
        gap: 1rem;
        flex-wrap: wrap;
    }

    .student-badge {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        padding: 0.5rem 1rem;
        background: #f0f7ff;
        border: 2px solid #8eb1d1;
        border-radius: 8px;
        color: #7aa3c4;
        font-weight: 600;
    }

    .status-badge {
        padding: 0.5rem 1rem;
        border-radius: 8px;
        font-weight: 600;
        font-size: 0.875rem;
    }

    .status-completed {
        background: #dcfce7;
        color: #16a34a;
        border: 2px solid #86efac;
    }

    .status-in-progress {
        background: #fef3c7;
        color: #d97706;
        border: 2px solid #fcd34d;
    }

    .assignment-title {
        font-size: 1.75rem;
        font-weight: 700;
        color: #1a1a2e;
        margin: 0;
    }

    .result-meta {
        display: flex;
        gap: 1.5rem;
        color: #6b7280;
        font-size: 0.875rem;
    }

    /* Stats Grid */
    .stats-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
        gap: 1rem;
        margin-bottom: 2rem;
    }

    .stat-card {
        padding: 1.25rem;
        border-radius: 12px;
        text-align: center;
        border: 2px solid #e5e7eb;
        background: #f8fafc;
    }

    .stat-value {
        font-size: 2rem;
        font-weight: 700;
        color: #1a1a2e;
        margin-bottom: 0.25rem;
    }

    .stat-value.correct {
        color: #16a34a;
    }

    .stat-value.incorrect {
        color: #dc2626;
    }

    .stat-label {
        font-size: 0.875rem;
        color: #6b7280;
    }

    /* Slides Results */
    .slides-results {
        display: flex;
        flex-direction: column;
        gap: 1.5rem;
    }

    .section-title {
        font-size: 1.25rem;
        font-weight: 600;
        color: #1a1a2e;
        margin: 0 0 0.5rem;
    }

    .slide-result-card {
        border: 2px solid #e5e7eb;
        border-radius: 12px;
        padding: 1.5rem;
        background: #f8fafc;
        transition: all 0.2s;
    }

    .slide-result-card:hover {
        border-color: #8eb1d1;
        box-shadow: 0 4px 12px rgba(142, 177, 209, 0.15);
    }

    .slide-result-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 1rem;
        padding-bottom: 0.75rem;
        border-bottom: 1px solid #e5e7eb;
        gap: 1rem;
        flex-wrap: wrap;
    }

    .correctness-controls {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        flex-wrap: wrap;
    }

    .correctness-btn {
        display: inline-flex;
        align-items: center;
        gap: 0.375rem;
        padding: 0.375rem 0.75rem;
        border: 2px solid #e5e7eb;
        border-radius: 8px;
        background: white;
        color: #6b7280;
        font-size: 0.8rem;
        font-weight: 500;
        cursor: pointer;
        transition: all 0.2s ease;
    }

    .correctness-btn:hover {
        transform: translateY(-1px);
    }

    .correctness-btn-correct:hover,
    .correctness-btn-correct.active {
        background: #dcfce7;
        border-color: #86efac;
        color: #16a34a;
    }

    .correctness-btn-incorrect:hover,
    .correctness-btn-incorrect.active {
        background: #fee2e2;
        border-color: #fecaca;
        color: #dc2626;
    }

    .slide-number {
        font-size: 1rem;
        font-weight: 600;
        color: #8eb1d1;
        display: flex;
        align-items: center;
        gap: 0.5rem;
    }

    .slide-type-badge {
        font-size: 0.75rem;
        padding: 0.25rem 0.5rem;
        background: white;
        border: 1px solid #e5e7eb;
        border-radius: 4px;
        color: #6b7280;
        font-weight: 500;
    }

    .correctness-badge {
        padding: 0.375rem 0.75rem;
        border-radius: 6px;
        font-size: 0.875rem;
        font-weight: 600;
    }

    .correctness-badge.correct {
        background: #dcfce7;
        color: #16a34a;
        border: 2px solid #86efac;
    }

    .correctness-badge.incorrect {
        background: #fee2e2;
        color: #dc2626;
        border: 2px solid #fecaca;
    }

    .slide-question {
        font-size: 1rem;
        font-weight: 500;
        color: #1a1a2e;
        margin-bottom: 1rem;
        line-height: 1.5;
    }

    .slide-variants {
        display: flex;
        flex-direction: column;
        gap: 0.5rem;
        margin-top: 0.75rem;
    }

    .variant-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 0.75rem 1rem;
        background: white;
        border: 2px solid #e5e7eb;
        border-radius: 8px;
        font-size: 0.95rem;
        color: #374151;
    }

    .variant-item.variant-correct {
        background: #dcfce7;
        border-color: #86efac;
        color: #16a34a;
        font-weight: 500;
    }

    .variant-item.variant-incorrect {
        background: #fee2e2;
        border-color: #fecaca;
        color: #dc2626;
        font-weight: 500;
    }

    .variant-icon {
        font-weight: 700;
        font-size: 1.1rem;
    }

    .slide-answer {
        margin-top: 1rem;
    }

    .answer-label {
        font-size: 0.875rem;
        font-weight: 500;
        color: #6b7280;
        margin-bottom: 0.5rem;
    }

    .answer-content {
        padding: 1rem;
        background: white;
        border: 2px solid #e5e7eb;
        border-radius: 8px;
        font-size: 0.95rem;
        color: #374151;
        line-height: 1.5;
    }

    .answer-content.correct {
        border-color: #86efac;
        background: #f0fdf4;
    }

    .answer-content.incorrect {
        border-color: #fecaca;
        background: #fef2f2;
    }

    .no-answer {
        color: #9ca3af;
        font-style: italic;
    }

    .slide-timestamp {
        margin-top: 0.75rem;
        font-size: 0.8rem;
        color: #9ca3af;
        text-align: right;
    }

    /* Comment Section */
    .comment-section {
        margin-top: 1.25rem;
        padding-top: 1.25rem;
        border-top: 2px dashed #e5e7eb;
    }

    .comment-header {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        font-size: 0.875rem;
        font-weight: 600;
        color: #7aa3c4;
        margin-bottom: 0.75rem;
    }

    .comment-header svg {
        color: #7aa3c4;
    }

    .comment-input {
        width: 100%;
        min-height: 80px;
        max-width: 100%;
        padding: 0.875rem 1rem;
        font-size: 0.95rem;
        font-family: inherit;
        color: #374151;
        background: #f8fafc;
        border: 2px solid #e5e7eb;
        border-radius: 10px;
        resize: vertical;
        transition: all 0.2s ease;
        line-height: 1.5;
        box-sizing: border-box;
    }

    .comment-input:focus {
        outline: none;
        border-color: #7aa3c4;
        background: white;
    }

    .comment-input::placeholder {
        color: #9ca3af;
    }

    .save-all-section {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 1rem;
        margin-top: 2rem;
        padding-top: 2rem;
        border-top: 2px solid #e5e7eb;
    }

    .btn-save-all {
        display: inline-flex;
        align-items: center;
        gap: 0.75rem;
        padding: 1rem 2rem;
        background: linear-gradient(135deg, #8eb1d1 0%, #7aa3c4 100%);
        color: white;
        border: none;
        border-radius: 12px;
        font-size: 1rem;
        font-weight: 600;
        cursor: pointer;
        transition: all 0.2s ease;
        box-shadow: 0 4px 12px rgba(122, 163, 196, 0.3);
        min-width: 280px;
        justify-content: center;
    }

    .btn-save-all:hover:not(:disabled) {
        transform: translateY(-2px);
        box-shadow: 0 6px 20px rgba(122, 163, 196, 0.4);
    }

    .btn-save-all:active:not(:disabled) {
        transform: translateY(0);
    }

    .btn-save-all:disabled {
        opacity: 0.7;
        cursor: not-allowed;
    }

    .spinner {
        width: 18px;
        height: 18px;
        border: 2px solid rgba(255, 255, 255, 0.3);
        border-top-color: white;
        border-radius: 50%;
        animation: spin 0.6s linear infinite;
    }

    @keyframes spin {
        to {
            transform: rotate(360deg);
        }
    }

    .save-message {
        font-size: 0.95rem;
        font-weight: 600;
        padding: 0.75rem 1.5rem;
        border-radius: 8px;
        text-align: center;
    }

    .save-message.success {
        color: #16a34a;
        background: #dcfce7;
        border: 2px solid #86efac;
    }

    .save-message.error {
        color: #dc2626;
        background: #fee2e2;
        border: 2px solid #fecaca;
    }

    .no-result {
        text-align: center;
        padding: 3rem 1rem;
        color: #6b7280;
    }

    .no-result svg {
        margin-bottom: 1rem;
        opacity: 0.5;
    }

    .no-result p {
        margin: 0;
        font-size: 1rem;
    }

    @media (max-width: 640px) {
        .results-card {
            padding: 1.5rem;
        }

        .stats-grid {
            grid-template-columns: repeat(2, 1fr);
        }

        .assignment-title {
            font-size: 1.5rem;
        }

        .result-meta {
            flex-direction: column;
            gap: 0.5rem;
        }
    }
</style>
