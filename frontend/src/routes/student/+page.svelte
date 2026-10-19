<script lang="ts">
    import { page } from '$app/state';

    let { data } = $props();
    const path = page.url.pathname;

    const statusLabels: Record<string, string> = {
        in_progress: 'В процессе',
        completed: 'Пройдено',
        not_started: 'Не начато',
    };

    const statusClasses: Record<string, string> = {
        in_progress: 'status-in-progress',
        completed: 'status-completed',
        not_started: 'status-not-started',
    };

    const gradedLabels: Record<string, string> = {
        'true': 'Оценено',
        'false': 'Не оценено',
    };

    const gradedClasses: Record<string, string> = {
        'true': 'graded-yes',
        'false': 'graded-no',
    };

    function formatDate(date: Date): string {
        return date.toLocaleDateString('ru-RU', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric',
        });
    }
</script>

<div class="student-page-container">
    <header class="student-header">
        <div class="student-header-inner">
            <div class="header-left">
                <div class="logo-icon">
                    <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M22 10v6M2 10l10-5 10 5-10 5z"/>
                        <path d="M6 12v5c3 3 9 3 12 0v-5"/>
                    </svg>
                </div>
                <h1 class="page-title">Кабинет студента</h1>
            </div>
            <form method="POST" action="?/logout">
                <button type="submit" class="btn-logout">
                    <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/>
                        <polyline points="16 17 21 12 16 7 16 11"/>
                        <line x1="21" y1="12" x2="9" y2="12"/>
                    </svg>
                    Выйти
                </button>
            </form>
        </div>
    </header>

    <main class="student-main">
        <div class="student-page-card">
            <div class="assignments-list">
                {#if data.results?.length === 0}
                    <div class="empty-state">
                        <svg xmlns="http://www.w3.org/2000/svg" width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                            <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"/>
                            <path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"/>
                        </svg>
                        <p>Заданий пока нет</p>
                    </div>
                {:else}
                    <ul>
                        {#each data.results as result, index}
                            <li class="assignment-item" style="animation-delay: {index * 0.05}s">
                                <div class="assignment-icon">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                        <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"/>
                                        <path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"/>
                                    </svg>
                                </div>
                                <div class="assignment-info">
                                    {#if result.status === 'completed' && result.isGraded}
                                        <a href="/student/results/{result.assignment.id}" class="assignment-title-link">
                                            <span class="assignment-title">{result.assignment.title}</span>
                                        </a>
                                    {:else}
                                        <span class="assignment-title">{result.assignment.title}</span>
                                    {/if}
                                    <span class="assignment-date">
                                        {formatDate(result.startedAt)}
                                    </span>
                                    {#if result.status === 'completed'}
                                        <span class="graded-badge {gradedClasses[String(result.isGraded)]}">
                                            {gradedLabels[String(result.isGraded)]}
                                        </span>
                                    {/if}
                                </div>
                                <span class="status-badge {statusClasses[result.displayStatus ?? result.status]}">
                                    {statusLabels[result.displayStatus ?? result.status]}
                                </span>
                                {#if result.displayStatus === 'not_started'}
                                    <a href="{path}/{result.assignment.id}" class="btn-continue">
                                        Начать
                                    </a>
                                {:else if result.status === 'in_progress'}
                                    <a href="{path}/{result.assignment.id}" class="btn-continue">
                                        Продолжить
                                    </a>
                                {:else if result.status === 'completed' && result.isGraded}
                                    <a href="/student/results/{result.assignment.id}" class="btn-continue btn-results">
                                        Результаты
                                    </a>
                                {/if}
                            </li>
                        {/each}
                    </ul>
                {/if}
            </div>
        </div>
    </main>
</div>

<style>
    .student-page-container {
        min-height: 100vh;
        display: flex;
        flex-direction: column;
        background: linear-gradient(135deg, #f5f8fc 0%, #eef2f7 100%);
    }

    .student-header {
        background: white;
        border-bottom: 1px solid #e5e7eb;
        padding: 1rem 2rem;
        box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
    }

    .student-header-inner {
        max-width: 1200px;
        margin: 0 auto;
        display: flex;
        align-items: center;
        justify-content: space-between;
    }

    .header-left {
        display: flex;
        align-items: center;
        gap: 0.75rem;
    }

    .logo-icon {
        width: 40px;
        height: 40px;
        background: linear-gradient(135deg, #8eb1d1 0%, #7aa3c4 100%);
        border-radius: 10px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: white;
        flex-shrink: 0;
    }

    .page-title {
        font-size: 1.25rem;
        font-weight: 700;
        color: #1a1a2e;
        margin: 0;
    }

    .btn-logout {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        padding: 0.625rem 1.25rem;
        background: white;
        color: #6b7280;
        border: 2px solid #e5e7eb;
        border-radius: 10px;
        font-size: 0.9rem;
        font-weight: 500;
        cursor: pointer;
        transition: all 0.2s;
    }

    .btn-logout:hover {
        background: #fef2f2;
        border-color: #fecaca;
        color: #dc2626;
    }

    .student-main {
        flex: 1;
        padding: 2rem 1rem;
    }

    .student-page-card {
        background: white;
        border-radius: 16px;
        box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1),
                    0 2px 4px -1px rgba(0, 0, 0, 0.06),
                    0 0 0 1px rgba(142, 177, 209, 0.1);
        padding: 2.5rem;
        width: 100%;
        max-width: 800px;
        margin: 0 auto;
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

    .assignments-list {
        margin-top: 1.5rem;
    }

    .empty-state {
        text-align: center;
        padding: 3rem 1rem;
        color: #6b7280;
    }

    .empty-state svg {
        margin-bottom: 1rem;
        opacity: 0.5;
    }

    .empty-state p {
        margin: 0;
        font-size: 1rem;
    }

    ul {
        list-style: none;
        padding: 0;
        margin: 0;
    }

    .assignment-item {
        display: flex;
        align-items: center;
        gap: 1rem;
        padding: 1rem 1.25rem;
        background: #f8fafc;
        border: 2px solid #e5e7eb;
        border-radius: 12px;
        margin-bottom: 0.75rem;
        transition: all 0.2s;
        animation: fadeIn 0.3s ease-out forwards;
        opacity: 0;
    }

    @keyframes fadeIn {
        to {
            opacity: 1;
        }
    }

    .assignment-item:hover {
        background: #f0f7ff;
        border-color: #8eb1d1;
        transform: translateX(4px);
    }

    .assignment-icon {
        width: 40px;
        height: 40px;
        background: linear-gradient(135deg, #8eb1d1 0%, #7aa3c4 100%);
        border-radius: 10px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: white;
        flex-shrink: 0;
    }

    .assignment-info {
        flex: 1;
        display: flex;
        flex-direction: column;
        gap: 0.25rem;
    }

    .assignment-title {
        font-size: 1rem;
        font-weight: 600;
        color: #374151;
    }

    .assignment-title-link {
        font-size: 1rem;
        font-weight: 600;
        color: #7aa3c4;
        text-decoration: none;
        transition: color 0.2s;
    }

    .assignment-title-link:hover {
        color: #5a8aad;
        text-decoration: underline;
    }

    .assignment-date {
        font-size: 0.85rem;
        color: #6b7280;
    }

    .graded-badge {
        font-size: 0.75rem;
        padding: 0.2rem 0.5rem;
        border-radius: 9999px;
        font-weight: 600;
        display: inline-block;
    }

    .graded-badge.graded-yes {
        background: #d1fae5;
        color: #059669;
    }

    .graded-badge.graded-no {
        background: #fef3c7;
        color: #d97706;
    }

    .status-badge {
        padding: 0.375rem 0.75rem;
        border-radius: 9999px;
        font-size: 0.85rem;
        font-weight: 600;
        flex-shrink: 0;
    }

    .status-in-progress {
        background: #fef3c7;
        color: #d97706;
        border: 1px solid #fcd34d;
    }

    .status-not-started {
        background: #f3f4f6;
        color: #6b7280;
        border: 1px solid #d1d5db;
    }

    .status-completed {
        background: #d1fae5;
        color: #059669;
        border: 1px solid #6ee7b7;
    }

    .btn-continue {
        padding: 0.5rem 1rem;
        background: linear-gradient(135deg, #8eb1d1 0%, #7aa3c4 100%);
        color: white;
        border: none;
        border-radius: 8px;
        font-size: 0.875rem;
        font-weight: 600;
        text-decoration: none;
        cursor: pointer;
        transition: all 0.2s;
        flex-shrink: 0;
    }

    .btn-continue:hover {
        transform: translateY(-1px);
        box-shadow: 0 4px 12px rgba(142, 177, 209, 0.4);
    }

    .btn-results {
        background: linear-gradient(135deg, #10b981 0%, #059669 100%);
    }

    .btn-results:hover {
        box-shadow: 0 4px 12px rgba(16, 185, 129, 0.4);
    }

    @media (max-width: 640px) {
        .student-header {
            padding: 1rem;
        }

        .student-header-inner {
            flex-direction: column;
            gap: 1rem;
            align-items: stretch;
        }

        .header-left {
            justify-content: center;
        }

        .btn-logout {
            justify-content: center;
        }

        .student-page-card {
            padding: 1.5rem;
        }
    }
</style>
