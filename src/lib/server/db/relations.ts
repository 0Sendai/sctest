import { defineRelations } from 'drizzle-orm';
import {assignments, users, slides} from './schema';

export const relations = defineRelations({assignments, users, slides}, (r) => ({
    slides: {
        assignment: r.one.assignments({
            from: r.slides.assignment_id,
            to: r.assignments.id,
        }),
    },
    users: {
        assignments: r.many.assignments(),
    },
    assignments: {
        slides: r.many.slides(),
        teacher: r.one.users({
            from: r.assignments.author_id,
            to: r.users.id,
        }),
    },
}));
