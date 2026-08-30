import { fail } from '@sveltejs/kit';

async function parseForm(form: FormData) {
    const type = form.get('type');
    const question = form.get('question');
    console.log(form);
    if (!question || !(typeof question === 'string') ||
        !type     || !(typeof type     === 'string'))
        return {};

    console.log("after question");
    if (type === 'quiz') {
        const variants = await form.getAll('variants');
        
        if (!variants.every((variant: FormDataEntryValue) => 
                            (variant && (typeof variant === 'string'))))
             return {};
     
        console.log("ret quiz");
        return {
            question: question,
            variants: variants,
            type: type,
        };
    }
    console.log("ret text");
    return {
        question: question,
        type: type,
    };
}

export const actions = {
    default: async ({ request }) => {
        const form = await request.formData();
        const data = await parseForm(form);
        if (!data) {
            console.log("error while parsing ");
        //if (!validateData(data))
            return fail(406, {error: 'empty question or variants'});
        }
        console.log(data);
    },
};
