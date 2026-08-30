import { pgTable, pgEnum, serial, integer, text, timestamp, jsonb, unique, uuid } 
from 'drizzle-orm/pg-core';
import { defineRelations } from 'drizzle-orm';

export const userEnum = pgEnum('user_role', ['admin', 'teacher', 'student']);

export const users = pgTable('users', {
    id: serial().primaryKey(),
    name: text().notNull().unique(),
    password: text(),
    role: userEnum().notNull(),
    created_at: timestamp().notNull().defaultNow(),
});

export const assignments = pgTable('assignments', {
    id: uuid().primaryKey().defaultRandom(),
    title: text().notNull(),
    author_id: integer().notNull().references(() => users.id),
    created_at: timestamp().notNull().defaultNow(),
    updated_at: timestamp().notNull().defaultNow(),
});

export const slides = pgTable('slides', {
    id: serial().primaryKey(),
    assignment_id: uuid().notNull().references(() => assignments.id),
    type: text(),
    position: integer().notNull(),
    content: jsonb().notNull(),
    created_at: timestamp().notNull().defaultNow(),
    updated_at: timestamp().notNull().defaultNow(),
}, (table) => [
    unique().on(table.assignment_id, table.position),
]);

