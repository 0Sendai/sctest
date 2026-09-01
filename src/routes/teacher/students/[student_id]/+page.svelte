<script lang="ts">
    let { data } = $props();

    const student = $derived(data.student);
    const assignments = $derived(data.assignments);

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

    function getStatsSummary(assignment: any) {
        const total = assignment.correctCount + assignment.incorrectCount;
        if (total === 0) return 'Не отвечено';
        const percentage = Math.round((assignment.correctCount / total) * 100);
        return `${assignment.correctCount}/${total} (${percentage}%)`;
    }
</script>

<div class="student-profile-container">
    <div class="student-profile-card">
        <!-- Header -->
        <div class="profile-header">
            <a href="../" class="btn-back">
                <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <line x1="19" y1="12" x2="5" y2="12"/>
                    <polyline points="12 19 5 12 12 5"/>
                </svg>
                Назад к списку студентов
            </a>

            <div class="student-info">
                <div class="student-avatar-large">
                    {student?.name?.charAt(0).toUpperCase()}
                </div>
                <div class="student-details">
                    <h1 class="student-name">{student?.name}</h1>
                    <p class="student-role">Студент</p>
                </div>
            </div>
        </div>

        <!-- Assignments List -->
        <div class="assignments-section">
            <h2 class="section-title">Задания</h2>

            {#if !assignments || assignments.length === 0}
                <div class="empty-state">
                    <svg xmlns="http://www.w3.org/2000/svg" width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"/>
                        <path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"/>
                    </svg>
                    <p>У студента ещё нет начатых заданий</p>
                </div>
            {:else}
                <div class="assignments-grid">
                    {#each assignments as assignment, index}
                        <a href={`/teacher/students/${student?.id}/${assignment.id}`} class="assignment-card" style="animation-delay: {index * 0.05}s">
                            <div class="assignment-header">
                                <div class="assignment-title">{assignment.assignmentTitle || 'Без названия'}</div>
                                <div class="status-badge {getStatusBadge(assignment.status).class}">
                                    {getStatusBadge(assignment.status).text}
                                </div>
                            </div>

                            <div class="assignment-meta">
                                <div class="meta-item">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                        <rect x="3" y="4" width="18" height="18" rx="2" ry="2"/>
                                        <line x1="16" y1="2" x2="16" y2="6"/>
                                        <line x1="8" y1="2" x2="8" y2="6"/>
                                        <line x1="3" y1="10" x2="21" y2="10"/>
                                    </svg>
                                    <span>{new Date(assignment.startedAt).toLocaleDateString('ru-RU')}</span>
                                </div>

                                <div class="meta-item">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                        <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/>
                                        <polyline points="22 4 12 14.01 9 11.01"/>
                                    </svg>
                                    <span>{getStatsSummary(assignment)}</span>
                                </div>
                            </div>

                            <div class="assignment-stats">
                                <div class="stat-bar">
                                    {#if assignment.correctCount > 0}
                                        <div class="stat-segment stat-correct" style="width: {assignment.correctCount + assignment.incorrectCount > 0 ? (assignment.correctCount / (assignment.correctCount + assignment.incorrectCount)) * 100 : 0}%"></div>
                                    {/if}
                                    {#if assignment.incorrectCount > 0}
                                        <div class="stat-segment stat-incorrect" style="width: {assignment.correctCount + assignment.incorrectCount > 0 ? (assignment.incorrectCount / (assignment.correctCount + assignment.incorrectCount)) * 100 : 0}%"></div>
                                    {/if}
                                </div>
                                <div class="stat-labels">
                                    <span class="label-correct">✓ {assignment.correctCount}</span>
                                    <span class="label-incorrect">✗ {assignment.incorrectCount}</span>
                                </div>
                            </div>
                        </a>
                    {/each}
                </div>
            {/if}
        </div>
    </div>
</div>

<style>
    .student-profile-container {
        min-height: 100vh;
        display: flex;
        align-items: flex-start;
        justify-content: center;
        background: linear-gradient(135deg, #f5f8fc 0%, #eef2f7 100%);
        padding: 2rem 1rem;
    }

    .student-profile-card {
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

    .profile-header {
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

    .student-info {
        display: flex;
        align-items: center;
        gap: 1.5rem;
    }

    .student-avatar-large {
        width: 72px;
        height: 72px;
        background: linear-gradient(135deg, #8eb1d1 0%, #7aa3c4 100%);
        border-radius: 16px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: white;
        font-size: 2rem;
        font-weight: 700;
        flex-shrink: 0;
    }

    .student-details {
        display: flex;
        flex-direction: column;
        gap: 0.25rem;
    }

    .student-name {
        font-size: 1.75rem;
        font-weight: 700;
        color: #1a1a2e;
        margin: 0;
    }

    .student-role {
        color: #6b7280;
        margin: 0;
        font-size: 0.95rem;
    }

    /* Assignments Section */
    .assignments-section {
        display: flex;
        flex-direction: column;
        gap: 1rem;
    }

    .section-title {
        font-size: 1.25rem;
        font-weight: 600;
        color: #1a1a2e;
        margin: 0 0 0.5rem;
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

    .assignments-grid {
        display: flex;
        flex-direction: column;
        gap: 1rem;
    }

    .assignment-card {
        display: flex;
        flex-direction: column;
        gap: 0.75rem;
        padding: 1.25rem;
        background: #f8fafc;
        border: 2px solid #e5e7eb;
        border-radius: 12px;
        text-decoration: none;
        transition: all 0.2s;
        animation: fadeIn 0.3s ease-out forwards;
        opacity: 0;
    }

    @keyframes fadeIn {
        to {
            opacity: 1;
        }
    }

    .assignment-card:hover {
        border-color: #8eb1d1;
        box-shadow: 0 4px 12px rgba(142, 177, 209, 0.15);
        transform: translateY(-2px);
    }

    .assignment-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        gap: 1rem;
    }

    .assignment-title {
        font-size: 1.1rem;
        font-weight: 600;
        color: #1a1a2e;
        margin: 0;
    }

    .status-badge {
        padding: 0.375rem 0.75rem;
        border-radius: 6px;
        font-size: 0.8rem;
        font-weight: 600;
        white-space: nowrap;
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

    .assignment-meta {
        display: flex;
        gap: 1.5rem;
        color: #6b7280;
        font-size: 0.875rem;
    }

    .meta-item {
        display: flex;
        align-items: center;
        gap: 0.375rem;
    }

    .assignment-stats {
        display: flex;
        flex-direction: column;
        gap: 0.25rem;
    }

    .stat-bar {
        display: flex;
        height: 8px;
        background: #e5e7eb;
        border-radius: 4px;
        overflow: hidden;
    }

    .stat-segment {
        transition: width 0.3s ease;
    }

    .stat-correct {
        background: #16a34a;
    }

    .stat-incorrect {
        background: #dc2626;
    }

    .stat-labels {
        display: flex;
        gap: 1rem;
        font-size: 0.8rem;
    }

    .label-correct {
        color: #16a34a;
        font-weight: 500;
    }

    .label-incorrect {
        color: #dc2626;
        font-weight: 500;
    }

    @media (max-width: 640px) {
        .student-profile-card {
            padding: 1.5rem;
        }

        .student-info {
            flex-direction: column;
            text-align: center;
        }

        .assignment-meta {
            flex-direction: column;
            gap: 0.5rem;
        }
    }
</style>
