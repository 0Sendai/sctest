<script lang="ts">
	import { apiFetch } from '#lib/api.js';

    let { data } = $props();

    interface Slide {
        id: number,
        type: string
    }
    // Извлекаем данные с проверкой
    const assignment = $derived(data.assignment);
    const slides = $derived(data.slides);
    const assignmentResult = $derived(data.assignmentResult);
    const slideAnswers = $derived(data.slideAnswers);
    const slideComments = $derived(data.slideComments);

    // Состояние приложения
    let started = $state(false);
    let currentSlideIndex = $state(0);
    let answers = $state<Record<number, any>>({});
    let isSubmitting = $state(false);
    let errorMessage = $state<string | null>(null);

    // Вычисляемые значения через $derived
    const currentSlide = $derived(slides && slides[currentSlideIndex]);
    const currentAnswer = $derived(currentSlide ? answers[currentSlide.id] : undefined);
    const isFirstSlide = $derived(currentSlideIndex === 0);
    const isLastSlide = $derived(slides && currentSlideIndex === slides.length - 1);
    const allSlidesCompleted = $derived(slides ? slides.every((s: Slide) => answers[s.id] !== undefined) : false);
    const isGraded = $derived(assignmentResult?.status === 'completed' && slides?.some((s: Slide) => slideComments?.has(s.id) ?? false));
    const hasComments = $derived(assignmentResult?.status === 'completed' && (slideComments?.size ?? 0) > 0);

    
    // Восстановление прогресса и автоматический старт при загрузке
    $effect(() => {
        if (slides && assignmentResult) {
            // Если задание уже начато, сразу показываем слайды
            if (assignmentResult.status === 'in_progress' && !started) {
                started = true;
            }
            // Восстанавливаем индекс слайда из сохраненного результата
            if (assignmentResult.currentSlideId) {
                const slideIndex = slides.findIndex((s: Slide) => s.id === assignmentResult.currentSlideId);
                if (slideIndex !== -1) {
                    currentSlideIndex = slideIndex;
                }
            }
            // Восстанавливаем ответы с сервера
            if (slideAnswers && Object.keys(slideAnswers).length > 0) {
                answers = { ...slideAnswers };
            }
        }
    });

    // Обработка ответа на слайд
    function handleAnswer(value: any) {
        if (currentSlide) {
            answers = { ...answers, [currentSlide.id]: value };
        }
    }

    // Отправка результата на сервер (асинхронно)
    async function submitSlideResult() {
        if (!currentSlide || !assignmentResult) return;

        isSubmitting = true;
        errorMessage = null;

        try {
            await apiFetch('/api/assignments/slideResult', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    resultId: assignmentResult.id,
                    slideId: currentSlide.id,
                    answer: answers[currentSlide.id],
                }),
            });
        } catch (error) {
            console.error('Failed to submit slide result:', error);
            errorMessage = 'Не удалось сохранить ответ. Попробуйте еще раз.';
        } finally {
            isSubmitting = false;
        }
    }

    // Переход к следующему слайду
    async function nextSlide() {
        if (!currentSlide) return;

        // Отправляем результат текущего слайда
        await submitSlideResult();

        if (isLastSlide) {
            // Последнее задание - завершаем работу
            await submitAssignmentResult();
        } else {
            currentSlideIndex++;
        }
    }

    // Переход к предыдущему слайду
    async function prevSlide() {
        if (isFirstSlide) return;

        // Отправляем результат текущего слайда
        await submitSlideResult();

        currentSlideIndex--;
    }

    // Завершение всего задания
    async function submitAssignmentResult() {
        if (!assignmentResult) return;

        isSubmitting = true;
        errorMessage = null;

        await submitSlideResult();

        try {
            await apiFetch('/api/assignments/assignmentResult', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    resultId: assignmentResult.id,
                    status: 'COMPLETED',
                    currentSlideId: currentSlide?.id,
                }),
            });

            // Перенаправляем на страницу студента
            window.location.href = '/student';
        } catch (error) {
            console.error('Failed to submit assignment result:', error);
            errorMessage = 'Не удалось завершить задание. Попробуйте еще раз.';
            isSubmitting = false;
        }
    }
</script>

<!-- Экран приветствия -->
{#if !started}
    <div class="welcome-screen">
        <div class="welcome-card">
            <div class="welcome-icon">
                <svg xmlns="http://www.w3.org/2000/svg" width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M22 10v6M2 10l10-5 10 5-10 5z"/>
                    <path d="M6 12v5c3 3 9 3 12 0v-5"/>
                </svg>
            </div>
            <h1 class="welcome-title">Добро пожаловать!</h1>
            <h2 class="assignment-title">{assignment?.title}</h2>
            <p class="welcome-description">
                Вас ждет {slides?.length} {slides?.length === 1 ? 'слайд' : slides?.length === 2 || slides?.length === 3 || slides?.length === 4 ? 'слайда' : 'слайдов'}.
                {#if slides?.some((s: Slide) => s.type === 'quiz')}
                    {#if slides.filter((s: Slide) => s.type === 'quiz').length > 0}
                        Будут вопросы с вариантами ответов.
                    {/if}
                {/if}
                {#if slides?.some((s: Slide) => s.type === 'text')}
                    {#if slides.filter((s: Slide)=> s.type === 'text').length > 0}
                        Также нужно будет написать ответы на вопросы.
                    {/if}
                {/if}
            </p>
            <button class="btn-start" onclick={async () => { started = true; await submitSlideResult(); }}>
                Начать работу
            </button>
            {#if errorMessage}
                <p class="error-message">{errorMessage}</p>
            {/if}
        </div>
    </div>
{/if}

<!-- Экран с слайдами -->
{#if started}
    <div class="slides-container">
        <div class="slides-header">
            <div class="slide-counter">
                Слайд {currentSlideIndex + 1} из {slides?.length}
            </div>
            <div class="progress-bar">
                <div class="progress-fill" style="width: {slides ? (currentSlideIndex + 1) / slides.length * 100 : 0}%"></div>
            </div>
        </div>

        <div class="slide-content">
            {#if currentSlide}
                <div class="slide-wrapper">
                    {#if currentSlide.type === 'quiz'}
                        <div class="quiz-slide">
                            <h3 class="quiz-question">{(currentSlide.content as any).question}</h3>
                            <div class="quiz-options">
                                {#each (currentSlide.content as any).variants as option, index (option)}
                                    <label class="quiz-option {answers[currentSlide.id] === option ? 'selected' : ''}">
                                        <input
                                            type="radio"
                                            name="quiz-answer"
                                            value={option}
                                            checked={answers[currentSlide.id] === option}
                                            onchange={(e: Event) => handleAnswer((e.target as HTMLInputElement).value)}
                                        />
                                        <span class="option-text">{option}</span>
                                    </label>
                                {/each}
                            </div>
                        </div>
                    {:else if currentSlide.type === 'text'}
                        <div class="text-slide">
                            <h3 class="text-question">{(currentSlide.content as any).question}</h3>
                            <textarea
                                class="text-input"
                                placeholder={(currentSlide.content as any).placeholder || 'Введите ваш ответ...'}
                                value={answers[currentSlide.id] || ''}
                                oninput={(e: Event) => handleAnswer((e.target as HTMLTextAreaElement).value)}
                            ></textarea>
                        </div>
                    {/if}
                </div>
            {/if}
        </div>

        <!-- Секция комментариев преподавателя (показывается после завершения) -->
        {#if hasComments && started}
            <div class="teacher-comments-section">
                <div class="comments-header">
                    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/>
                    </svg>
                    <h3>Комментарии преподавателя</h3>
                </div>
                <div class="comments-list">
                    {#each slides as slide, index}
                        {#if slideComments?.has(slide.id)}
                            <div class="comment-card">
                                <div class="comment-slide-number">
                                    Слайд {index + 1}
                                    <span class="comment-slide-type">{slide.type === 'quiz' ? '(Опрос)' : '(Вопрос)'}</span>
                                </div>
                                <div class="comment-question">
                                    <strong>Вопрос:</strong> {#if slide.type === 'quiz'}{(slide.content as any).question}{:else}{(slide.content as any).question}{/if}
                                </div>
                                <div class="comment-text">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                        <polyline points="9 18 15 12 9 6"/>
                                    </svg>
                                    {slideComments?.get(slide.id)}
                                </div>
                            </div>
                        {/if}
                    {/each}
                </div>
            </div>
        {/if}

        <div class="slide-footer">
            {#if errorMessage}
                <p class="error-message">{errorMessage}</p>
            {/if}

            <div class="navigation-buttons">
                <button
                    class="btn-nav btn-prev"
                    disabled={isFirstSlide || isSubmitting}
                    onclick={prevSlide}
                >
                    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <polyline points="15 18 9 12 15 6"/>
                    </svg>
                    Назад
                </button>

                <button
                    class="btn-nav btn-next"
                    disabled={!currentAnswer || isSubmitting}
                    onclick={nextSlide}
                >
                    {isSubmitting
                        ? 'Сохранение...'
                        : isLastSlide
                            ? 'Завершить'
                            : 'Далее'
                    }
                    {#if !isLastSlide}
                        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <polyline points="9 18 15 12 9 6"/>
                        </svg>
                    {/if}
                </button>
            </div>
        </div>
    </div>
{/if}

<style>
    /* Welcome Screen */
    .welcome-screen {
        min-height: 100vh;
        display: flex;
        align-items: center;
        justify-content: center;
        background: linear-gradient(135deg, #f5f8fc 0%, #eef2f7 100%);
        padding: 1rem;
    }

    .welcome-card {
        background: white;
        border-radius: 20px;
        box-shadow: 0 10px 40px rgba(122, 163, 196, 0.15),
                    0 4px 12px rgba(0, 0, 0, 0.08);
        padding: 3rem 2.5rem;
        max-width: 500px;
        width: 100%;
        text-align: center;
        animation: slideUp 0.5s ease-out;
    }

    @keyframes slideUp {
        from {
            opacity: 0;
            transform: translateY(30px);
        }
        to {
            opacity: 1;
            transform: translateY(0);
        }
    }

    .welcome-icon {
        width: 80px;
        height: 80px;
        background: linear-gradient(135deg, #8eb1d1 0%, #7aa3c4 100%);
        border-radius: 20px;
        display: flex;
        align-items: center;
        justify-content: center;
        margin: 0 auto 1.5rem;
        color: white;
    }

    .welcome-title {
        font-size: 1.75rem;
        font-weight: 700;
        color: #1a1a2e;
        margin: 0 0 0.5rem;
    }

    .assignment-title {
        font-size: 1.25rem;
        font-weight: 600;
        color: #7aa3c4;
        margin: 0 0 1rem;
    }

    .welcome-description {
        font-size: 1rem;
        color: #6b7280;
        line-height: 1.6;
        margin-bottom: 2rem;
    }

    .btn-start {
        width: 100%;
        padding: 1rem 2rem;
        background: linear-gradient(135deg, #8eb1d1 0%, #7aa3c4 100%);
        color: white;
        border: none;
        border-radius: 12px;
        font-size: 1.1rem;
        font-weight: 600;
        cursor: pointer;
        transition: all 0.3s ease;
        box-shadow: 0 4px 15px rgba(122, 163, 196, 0.4);
    }

    .btn-start:hover {
        transform: translateY(-2px);
        box-shadow: 0 6px 20px rgba(122, 163, 196, 0.5);
    }

    .btn-start:active {
        transform: translateY(0);
    }

    /* Slides Container */
    .slides-container {
        min-height: 100vh;
        display: flex;
        flex-direction: column;
        background: linear-gradient(135deg, #f5f8fc 0%, #eef2f7 100%);
    }

    .slides-header {
        background: white;
        padding: 1rem 2rem;
        box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
    }

    .slide-counter {
        font-size: 0.9rem;
        font-weight: 600;
        color: #6b7280;
        margin-bottom: 0.5rem;
        text-align: center;
    }

    .progress-bar {
        width: 100%;
        height: 6px;
        background: #e5e7eb;
        border-radius: 3px;
        overflow: hidden;
    }

    .progress-fill {
        height: 100%;
        background: linear-gradient(90deg, #8eb1d1, #7aa3c4);
        border-radius: 3px;
        transition: width 0.3s ease;
    }

    .slide-content {
        flex: 1;
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 2rem 1rem;
    }

    .slide-wrapper {
        max-width: 700px;
        width: 100%;
        animation: fadeIn 0.3s ease-out;
    }

    @keyframes fadeIn {
        from {
            opacity: 0;
            transform: translateX(20px);
        }
        to {
            opacity: 1;
            transform: translateX(0);
        }
    }

    /* Quiz Slide */
    .quiz-question {
        font-size: 1.25rem;
        font-weight: 600;
        color: #1a1a2e;
        margin-bottom: 1.5rem;
        line-height: 1.5;
    }

    .quiz-options {
        display: flex;
        flex-direction: column;
        gap: 0.75rem;
    }

    .quiz-option {
        display: flex;
        align-items: center;
        gap: 0.75rem;
        padding: 1rem 1.25rem;
        background: #f8fafc;
        border: 2px solid #e5e7eb;
        border-radius: 12px;
        cursor: pointer;
        transition: all 0.2s ease;
    }

    .quiz-option:hover {
        background: #f0f7ff;
        border-color: #8eb1d1;
    }

    .quiz-option.selected {
        background: #e8f4fd;
        border-color: #7aa3c4;
    }

    .quiz-option input[type="radio"] {
        width: 20px;
        height: 20px;
        accent-color: #7aa3c4;
        cursor: pointer;
    }

    .option-text {
        font-size: 1rem;
        color: #374151;
        flex: 1;
    }

    /* Text Slide */
    .text-question {
        font-size: 1.25rem;
        font-weight: 600;
        color: #1a1a2e;
        margin-bottom: 1.5rem;
        line-height: 1.5;
    }

    .text-input {
        width: 100%;
        min-height: 150px;
        padding: 1rem 1.25rem;
        font-size: 1rem;
        font-family: inherit;
        color: #374151;
        background: #f8fafc;
        border: 2px solid #e5e7eb;
        border-radius: 12px;
        resize: vertical;
        transition: all 0.2s ease;
    }

    .text-input:focus {
        outline: none;
        border-color: #7aa3c4;
        background: white;
        box-shadow: 0 0 0 3px rgba(122, 163, 196, 0.2);
    }

    .text-input::placeholder {
        color: #9ca3af;
    }

    /* Footer */
    .slide-footer {
        background: white;
        padding: 1.25rem 2rem;
        box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);
    }

    .error-message {
        color: #dc2626;
        font-size: 0.9rem;
        text-align: center;
        margin-bottom: 0.75rem;
        padding: 0.5rem;
        background: #fef2f2;
        border-radius: 8px;
    }

    .navigation-buttons {
        display: flex;
        justify-content: space-between;
        align-items: center;
        gap: 1rem;
    }

    .btn-nav {
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 0.5rem;
        padding: 0.875rem 1.5rem;
        border: none;
        border-radius: 12px;
        font-size: 1rem;
        font-weight: 600;
        cursor: pointer;
        transition: all 0.2s ease;
    }

    .btn-nav:disabled {
        opacity: 0.5;
        cursor: not-allowed;
    }

    .btn-prev {
        background: white;
        color: #6b7280;
        border: 2px solid #e5e7eb;
    }

    .btn-prev:hover:not(:disabled) {
        background: #f9fafb;
        border-color: #d1d5db;
        color: #374151;
    }

    .btn-next {
        background: linear-gradient(135deg, #8eb1d1 0%, #7aa3c4 100%);
        color: white;
        box-shadow: 0 4px 12px rgba(122, 163, 196, 0.3);
    }

    .btn-next:hover:not(:disabled) {
        transform: translateY(-1px);
        box-shadow: 0 6px 16px rgba(122, 163, 196, 0.4);
    }

    /* Teacher Comments Section */
    .teacher-comments-section {
        background: linear-gradient(135deg, #f0f7ff 0%, #e8f4fd 100%);
        border: 2px solid #8eb1d1;
        border-radius: 16px;
        padding: 1.5rem;
        margin: 1.5rem 2rem;
        animation: slideUp 0.4s ease-out;
    }

    .comments-header {
        display: flex;
        align-items: center;
        gap: 0.75rem;
        margin-bottom: 1.25rem;
        padding-bottom: 0.75rem;
        border-bottom: 2px solid #8eb1d1;
    }

    .comments-header svg {
        color: #7aa3c4;
    }

    .comments-header h3 {
        font-size: 1.125rem;
        font-weight: 700;
        color: #1a1a2e;
        margin: 0;
    }

    .comments-list {
        display: flex;
        flex-direction: column;
        gap: 1rem;
    }

    .comment-card {
        background: white;
        border: 2px solid #d1e7f5;
        border-radius: 12px;
        padding: 1.25rem;
        transition: all 0.2s ease;
    }

    .comment-card:hover {
        border-color: #8eb1d1;
        box-shadow: 0 4px 12px rgba(122, 163, 196, 0.15);
    }

    .comment-slide-number {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        margin-bottom: 0.75rem;
    }

    .comment-slide-type {
        font-size: 0.75rem;
        color: #6b7280;
        background: #f0f7ff;
        padding: 0.25rem 0.5rem;
        border-radius: 4px;
    }

    .comment-question {
        font-size: 0.9rem;
        color: #374151;
        margin-bottom: 0.75rem;
        line-height: 1.5;
        padding: 0.75rem;
        background: #f8fafc;
        border-radius: 8px;
    }

    .comment-question strong {
        color: #7aa3c4;
    }

    .comment-text {
        display: flex;
        align-items: flex-start;
        gap: 0.5rem;
        font-size: 0.95rem;
        color: #1a1a2e;
        line-height: 1.6;
        padding: 0.75rem;
        background: linear-gradient(135deg, #f0f7ff 0%, #e8f4fd 100%);
        border-left: 3px solid #7aa3c4;
        border-radius: 0 8px 8px 0;
    }

    .comment-text svg {
        color: #7aa3c4;
        flex-shrink: 0;
        margin-top: 2px;
    }

    @media (max-width: 640px) {
        .welcome-card {
            padding: 2rem 1.5rem;
        }

        .slides-header,
        .slide-footer {
            padding: 1rem;
        }

        .navigation-buttons {
            flex-direction: column;
        }

        .btn-nav {
            width: 100%;
        }
    }
</style>
