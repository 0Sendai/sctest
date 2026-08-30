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
        console.log(errorMsg);
    }

    function validateInput(): boolean {
        errorMsg.question = !question ? 'Question is required' : '';
        if (slide_type === 'quiz') {
            errorMsg.variants = !variants.every((variant) => variant)
                                ? 'Enter all variants' : '';
            return !errorMsg.question && !errorMsg.variants;
        }
        return !errorMsg.question;
    }

    function disableEnter(e: KeyboardEvent) { 
        if (e.key === 'Enter') e.preventDefault(); 
    }
</script>


<h1>Create question</h1>


<div>
    
    <form method="POST" use:enhance={enhanceFunc}>
        <label for="type">Choose a type:</label>
        <select name="type" bind:value={slide_type}>
            <option value="quiz">Quiz</option>
            <option value="text">Text</option>
        </select>
        <br>

        <label for="question">Enter question: </label>
        <input type="text" 
               name="question" 
               bind:value={question}
               onkeydown={disableEnter}
               autocomplete="off"/>
        <br/>
        {#if slide_type==='quiz'}
            {#each variants as _, index}
                <div>
                    <input type="text" 
                           name="variants"
                           autocomplete="off"
                           onkeydown={handleVariantEnter}
                           bind:value={variants[index]}/>
                    <button onclick={() => removeVariant(index)}>remove variant</button>
                </div>
                <br/>
            {/each}
            <button onclick={addVariant}>Add new variant</button>
        {/if}
        <button type="submit">Send form</button>
    </form>

    {#if form?.error}
        <p>Error: {form.error}</p>
    {/if}

    {#if errorMsg.question}
        <p>{errorMsg.question}</p>
    {/if}
    {#if errorMsg.variants}
        <p>{errorMsg.variants}</p>
    {/if}

</div>
