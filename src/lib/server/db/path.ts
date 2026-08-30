import 'dotenv/config';

if (!process.env.POSTGRES_DB || !process.env.POSTGRES_USER || !process.env.POSTGRES_PASSWORD) throw new Error("can't generate DATABASE_URL");

export const DATABASE_URL = 
    `postgresql://${process.env.POSTGRES_USER}:` +
    `${process.env.POSTGRES_PASSWORD}@localhost:5432/` +
    `${process.env.POSTGRES_DB}`;

