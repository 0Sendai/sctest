// See https://svelte.dev/docs/kit/types#app.d.ts
// for information about these interfaces
import type { UserPayload } from '#lib/server/jwt';

declare global {
	namespace App {
		// interface Error {}
		 interface Locals {
             user: UserPayload | null;
         }
		// interface PageData {}
		// interface PageState {}
		// interface Platform {}
	}
}

export {};
