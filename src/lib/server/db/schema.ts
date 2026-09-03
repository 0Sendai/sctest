import { pgTable, pgEnum, serial, integer, text, timestamp, jsonb, unique, uuid, boolean } 
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

export const assignmentResults = pgTable('assignment_results', {
    id: serial().primaryKey(),
    studentId: integer().notNull().references(() => users.id),
    assignmentId: uuid().notNull().references(() => assignments.id),
    currentSlideId: integer().references(() => slides.id),
    status: text().notNull().$type<'in_progress' | 'completed'>(),
    startedAt: timestamp().notNull().defaultNow(),
    completedAt: timestamp(),
    updatedAt: timestamp().notNull().defaultNow(),
}, (table) => [
    unique().on(table.studentId, table.assignmentId)
]);

export const slideResults = pgTable('slide_results', {
    id: serial().primaryKey(),
    result_id: integer()
        .notNull()
        .references(() => assignmentResults.id),
    slide_id: integer()
        .notNull()
        .references(() => slides.id),
    answer: jsonb(),
    is_correct: boolean(),
    answered_at: timestamp().notNull().defaultNow(),
}, (table) => [
    unique().on(table.result_id, table.slide_id),
]);

export const slideComments = pgTable('slide_comments', {
    id: serial().primaryKey(),
    assignment_id: uuid().notNull().references(() => assignments.id),
    slide_id: integer().notNull().references(() => slides.id),
    student_id: integer().notNull().references(() => users.id),
    comment: text(),
}, (table) => [
    unique().on(table.assignment_id, table.student_id, table.slide_id),
]);

