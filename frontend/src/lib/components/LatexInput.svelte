<script lang="ts">
    import katex from 'katex';
    import 'katex/dist/katex.min.css';

    let { input = $bindable(''), ...rest } = $props();
    let renderredLatex = $derived(
        katex.renderToString(input, {
            //displayMode: true,
            throwOnError: false,
            errorColor: '#ef4444'
        })
    );
</script>


<div class="math-editor">
  <input
    id="latex-input"
    type="text"
    bind:value={input}
    { ...rest }
  />
  <div class="label">LaTeX output</div>
  <div class="preview-box" aria-live="polite">
    {@html renderredLatex}
  </div>
</div>

<style>
    .preview-box,
    input {
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

    input:focus {
        border-color: #8eb1d1;
        box-shadow: 0 0 0 3px rgba(142, 177, 209, 0.1);
    }

    input.error {
        border-color: #dc2626;
    }

    input.error:focus {
        box-shadow: 0 0 0 3px rgba(220, 38, 38, 0.1);
    }

    .label {
        display: block;
        margin-top: 0.5rem;
        margin-bottom: 0.5rem;
        font-size: 0.875rem;
        font-weight: 500;
        color: #374151;
    }

    .math-editor {
        margin-bottom: 0.5rem;
        /* padding-top: 1rem; */
        flex: 1;
    }
</style>