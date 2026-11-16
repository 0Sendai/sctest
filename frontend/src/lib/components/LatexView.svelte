<script lang="ts">
    import katex from 'katex';
    import 'katex/dist/katex.min.css';

    let { input } = $props();
    let renderredLatex = $derived(
        katex.renderToString(input, {
            //displayMode: true,
            throwOnError: false,
            errorColor: '#ef4444'
        })
    );
</script>


<span class="latex-content">
    {@html renderredLatex}
</span>

<style>
    .latex-content {
        display: inline-block;
        max-width: 100%;
        overflow-x: auto; /* Чтобы длинные формулы не ломали верстку на телефонах */
        vertical-align: middle;
    }
</style>