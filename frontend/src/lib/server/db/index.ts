import { drizzle } from 'drizzle-orm/postgres-js';
//import postgres from 'postgres';
//import * as schema from './schema';
import { relations } from './relations';
import { DATABASE_URL } from './path';

if (!DATABASE_URL) throw new Error('DATABASE_URL is not set');

//console.log(relations);

export const db = drizzle(DATABASE_URL, { relations });
export type dbType = typeof db;

