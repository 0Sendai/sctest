<script lang="ts">
    let { data } = $props();

    const title    = $derived(data.result.title);
    const result   = $derived(data.result.result);
    const slides   = $derived(data.result.slides);
    const answers  = $derived(data.result.answers);
    const comments = $derived(data.result.comments);

    function formatDate(dateValue: string): string {
        return new Date(dateValue).toLocaleDateString('ru-RU', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit',
        });
    }

    function getSlideAnswer(slideId: number): any {
        return answers?.[slideId];
    }

    function getSlideComment(slideId: number): string | undefined {
        return comments?.[slideId];
    }
</script>

<div class="results-page-container">
    <header class="results-header">
        <div class="results-header-inner">
            <div class="header-left">
                <a href="/student" class="btn-back">
                    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <polyline points="15 18 9 12 15 6"/>
                    </svg>
                    Назад к заданиям
                </a>
            </div>
            <div class="header-right">
                <span class="status-badge status-graded">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <polyline points="20 6 9 17 4 12"/>
                    </svg>
                    Оценено
                </span>
            </div>
        </div>
    </header>

    <main class="results-main">
        <div class="results-container">
            <div class="results-card">
                <div class="results-header-section">
                    <div class="results-icon">
                        <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"/>
                            <path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"/>
                        </svg>
                    </div>
                    <h1 class="results-title">{title}</h1>
                    {#if result}
                    <div class="results-dates">
                        <span class="date-item">
                            <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                <circle cx="12" cy="12" r="10"/>
                                <polyline points="12 6 12 12 16 14"/>
                            </svg>
                            Начато: {result.startedAt ? formatDate(result.startedAt) : '—'}
                        </span>
                        {#if result.completedAt}
                        <span class="date-item">
                            <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                <polyline points="20 6 9 17 4 12"/>
                            </svg>
                            Завершено: {formatDate(result.completedAt!)}
                        </span>
                        {/if}
                    </div>
                    {/if}
                </div>

                <div class="slides-review">
                    {#each slides as slide, index (slide.id)}
                        <div class="slide-review-card">
                            <div class="slide-review-header">
                                <span class="slide-number">Слайд {index + 1}</span>
                                <span class="slide-type-badge">{slide.type === 'quiz' ? 'Опрос' : 'Вопрос с ответом'}</span>
                            </div>

                            <div class="slide-question">
                                {#if slide.type === 'quiz'}
                                    <p>{(slide.content as any).question}</p>
                                    <div class="quiz-answers">
                                        {#each (slide.content as any).variants as option, optIndex (option)}
                                            <div class="quiz-option {getSlideAnswer(slide.id) === option ? 'option-correct' : ''}">
                                                <span class="option-marker">{getSlideAnswer(slide.id) === option ? '✓' : '○'}</span>
                                                <span class="option-text">{option}</span>
                                            </div>
                                        {/each}
                                    </div>
                                {:else}
                                    <p>{(slide.content as any).question}</p>
                                    <div class="text-answer">
                                        <p>{getSlideAnswer(slide.id) ?? '—'}</p>
                                    </div>
                                {/if}
                            </div>

                            {#if getSlideComment(slide.id)}
                                <div class="teacher-comment">
                                    <div class="comment-header">
                                        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                            <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/>
                                        </svg>
                                        <span>Комментарий преподавателя</span>
                                    </div>
                                    <div class="comment-body">
                                        {getSlideComment(slide.id)}
                                    </div>
                                </div>
                            {/if}
                        </div>
                    {/each}
                </div>
            </div>
        </div>
    </main>
</div>

<style>
    .results-page-container {
        min-height: 100vh;
        display: flex;
        flex-direction: column;
        background: linear-gradient(135deg, #f5f8fc 0%, #eef2f7 100%);
    }

    .results-header {
        background: white;
        border-bottom: 1px solid #e5e7eb;
        padding: 1rem 2rem;
        box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
    }

    .results-header-inner {
        max-width: 900px;
        margin: 0 auto;
        display: flex;
        align-items: center;
        justify-content: space-between;
    }

    .header-left {
        flex: 1;
    }

    .header-right {
        margin-left: 1rem;
    }

    .btn-back {
        display: inline-flex;
        align-items: center;
        gap: 0.5rem;
        color: #6b7280;
        text-decoration: none;
        font-size: 0.95rem;
        font-weight: 500;
        transition: color 0.2s;
    }

    .btn-back:hover {
        color: #7aa3c4;
    }

    .status-badge {
        display: inline-flex;
        align-items: center;
        gap: 0.375rem;
        padding: 0.375rem 0.875rem;
        border-radius: 9999px;
        font-size: 0.85rem;
        font-weight: 600;
        background: #d1fae5;
        color: #059669;
        border: 1px solid #6ee7b7;
    }

    .status-graded {
        background: #d1fae5;
        color: #059669;
    }

    .results-main {
        flex: 1;
        padding: 2rem 1rem;
    }

    .results-container {
        max-width: 900px;
        margin: 0 auto;
    }

    .results-card {
        background: white;
        border-radius: 16px;
        box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1),
                    0 2px 4px -1px rgba(0, 0, 0, 0.06),
                    0 0 0 1px rgba(142, 177, 209, 0.1);
        padding: 2.5rem;
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

    .results-header-section {
        text-align: center;
        margin-bottom: 2.5rem;
        padding-bottom: 2rem;
        border-bottom: 2px solid #f0f7ff;
    }

    .results-icon {
        width: 64px;
        height: 64px;
        background: linear-gradient(135deg, #8eb1d1 0%, #7aa3c4 100%);
        border-radius: 16px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: white;
        margin: 0 auto 1.25rem;
    }

    .results-title {
        font-size: 1.5rem;
        font-weight: 700;
        color: #1a1a2e;
        margin: 0 0 1rem;
    }

    .results-dates {
        display: flex;
        flex-direction: column;
        gap: 0.375rem;
        align-items: center;
    }

    .date-item {
        display: inline-flex;
        align-items: center;
        gap: 0.375rem;
        font-size: 0.875rem;
        color: #6b7280;
    }

    .date-item svg {
        flex-shrink: 0;
    }

    .slides-review {
        display: flex;
        flex-direction: column;
        gap: 1.5rem;
    }

    .slide-review-card {
        background: #f8fafc;
        border: 2px solid #e5e7eb;
        border-radius: 12px;
        padding: 1.5rem;
        transition: all 0.2s;
    }

    .slide-review-card:hover {
        border-color: #8eb1d1;
        box-shadow: 0 4px 12px rgba(142, 177, 209, 0.1);
    }

    .slide-review-header {
        display: flex;
        align-items: center;
        gap: 0.75rem;
        margin-bottom: 1.25rem;
        padding-bottom: 0.75rem;
        border-bottom: 1px solid #e5e7eb;
    }

    .slide-number {
        font-size: 0.875rem;
        font-weight: 600;
        color: #7aa3c4;
        background: #e8f4fd;
        padding: 0.25rem 0.75rem;
        border-radius: 9999px;
    }

    .slide-type-badge {
        font-size: 0.75rem;
        color: #6b7280;
        background: #f3f4f6;
        padding: 0.25rem 0.625rem;
        border-radius: 9999px;
    }

    .slide-question {
        margin-bottom: 1.25rem;
    }

    .slide-question p {
        font-size: 1rem;
        font-weight: 600;
        color: #1a1a2e;
        margin: 0 0 1rem;
        line-height: 1.5;
    }

    .quiz-answers {
        display: flex;
        flex-direction: column;
        gap: 0.5rem;
    }

    .quiz-option {
        display: flex;
        align-items: center;
        gap: 0.75rem;
        padding: 0.875rem 1rem;
        background: white;
        border: 2px solid #e5e7eb;
        border-radius: 10px;
        transition: all 0.2s;
    }

    .option-correct {
        background: #ecfdf5;
        border-color: #6ee7b7;
    }

    .option-marker {
        width: 24px;
        height: 24px;
        display: flex;
        align-items: center;
        justify-content: center;
        border-radius: 50%;
        font-size: 0.875rem;
        font-weight: 600;
        flex-shrink: 0;
    }

    .option-correct .option-marker {
        background: #10b981;
        color: white;
    }

    .option-text {
        font-size: 0.95rem;
        color: #374151;
    }

    .text-answer {
        background: white;
        border: 2px solid #e5e7eb;
        border-radius: 10px;
        padding: 1rem;
    }

    .text-answer p {
        margin: 0;
        color: #374151;
        line-height: 1.6;
    }

    .teacher-comment {
        background: linear-gradient(135deg, #f0f7ff 0%, #e8f4fd 100%);
        border: 2px solid #8eb1d1;
        border-radius: 10px;
        overflow: hidden;
    }

    .comment-header {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        padding: 0.75rem 1rem;
        background: rgba(122, 163, 196, 0.1);
        border-bottom: 1px solid #8eb1d1;
    }

    .comment-header svg {
        color: #7aa3c4;
    }

    .comment-header span {
        font-size: 0.875rem;
        font-weight: 600;
        color: #7aa3c4;
    }

    .comment-body {
        padding: 1rem;
        font-size: 0.95rem;
        color: #1a1a2e;
        line-height: 1.6;
    }

    @media (max-width: 640px) {
        .results-header {
            padding: 1rem;
        }

        .results-header-inner {
            flex-direction: column;
            gap: 1rem;
            align-items: flex-start;
        }

        .header-right {
            margin-left: 0;
        }

        .results-card {
            padding: 1.5rem;
        }

        .results-title {
            font-size: 1.25rem;
        }
    }
</style>
