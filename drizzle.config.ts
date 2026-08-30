import { defineConfig } from 'drizzle-kit';
import { DATABASE_URL } from './src/lib/server/db/path';
import 'dotenv/config';


export default defineConfig({
	schema: './src/lib/server/db/schema.ts',
	dialect: 'postgresql',
	dbCredentials: { url: DATABASE_URL! },
	verbose: true,
	strict: true
});
