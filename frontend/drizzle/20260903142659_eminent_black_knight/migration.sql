CREATE TABLE "slide_comments" (
	"id" serial PRIMARY KEY,
	"assignment_id" uuid NOT NULL,
	"slide_id" integer NOT NULL,
	"student_id" integer NOT NULL,
	"comment" text,
	CONSTRAINT "slide_comments_assignment_id_student_id_slide_id_unique" UNIQUE("assignment_id","student_id","slide_id")
);
--> statement-breakpoint
ALTER TABLE "slide_comments" ADD CONSTRAINT "slide_comments_assignment_id_assignments_id_fkey" FOREIGN KEY ("assignment_id") REFERENCES "assignments"("id");--> statement-breakpoint
ALTER TABLE "slide_comments" ADD CONSTRAINT "slide_comments_slide_id_slides_id_fkey" FOREIGN KEY ("slide_id") REFERENCES "slides"("id");--> statement-breakpoint
ALTER TABLE "slide_comments" ADD CONSTRAINT "slide_comments_student_id_users_id_fkey" FOREIGN KEY ("student_id") REFERENCES "users"("id");