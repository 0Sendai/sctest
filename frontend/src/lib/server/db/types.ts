import type { InferSelectModel, InferInsertModel } from 'drizzle-orm';
import { users, assignments, slides } from './schema';

export type User          = typeof users.$inferSelect;
export type NewUser       = typeof users.$inferInsert;

export type Assignment    = typeof assignments.$inferSelect;
export type NewAssignment = typeof assignments.$inferInsert;

export type Slide         = typeof slides.$inferSelect;
export type NewSlide      = typeof slides.$inferInsert;

