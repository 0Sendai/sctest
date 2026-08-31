import { fail } from '@sveltejs/kit';

type Quiz = {
    type: string;
    question: string;
    variants: string[];
};

type TextAnswer = {
    type: string;
    question: string;
};

async function parseForm(form: FormData): Promise<Quiz | TextAnswer | null> {
    const type = form.get('type');
    const question = form.get('question');
    console.log(form);
    if (!question || !(typeof question === 'string') ||
        !type     || !(typeof type     === 'string'))
        return null;

    console.log("after question");
    if (type === 'quiz') {
        const variants = await form.getAll('variants');
        
        if (!variants.every((variant: FormDataEntryValue) => 
                            (variant && (typeof variant === 'string'))))
             return null;
     
        console.log("ret quiz");
        return {
            question: question,
            variants: variants as string[],
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
    default: async ({ request, locals }) => {
        const form = await request.formData();
        //const data: TextAnswer | Quiz | null = await parseForm(form);
        // if (!data) {
        //     console.log("error while parsing ");
        //if (!validateData(data))
        if (!form) {
            return fail(406, {error: 'empty question or variants'});
        }

        console.log(form)
        // if (data.type === 'quiz') {
        //     console.log("quiz: ", data);
        // }
        // if (data.type === 'text') {
        //     console.log("text: ", data);
        // }
        //console.log(data);
        
    },
};
