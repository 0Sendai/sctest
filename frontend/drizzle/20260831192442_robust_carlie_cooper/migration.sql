CREATE TABLE "assignment_results" (
	"id" serial PRIMARY KEY,
	"studentId" integer NOT NULL,
	"assignmentId" uuid NOT NULL,
	"currentSlideId" integer,
	"status" text NOT NULL,
	"startedAt" timestamp DEFAULT now() NOT NULL,
	"completedAt" timestamp,
	"updatedAt" timestamp DEFAULT now() NOT NULL,
	CONSTRAINT "assignment_results_studentId_assignmentId_unique" UNIQUE("studentId","assignmentId")
);
--> statement-breakpoint
CREATE TABLE "slide_results" (
	"id" serial PRIMARY KEY,
	"result_id" integer NOT NULL,
	"slide_id" integer NOT NULL,
	"answer" jsonb,
	"is_correct" boolean,
	"answered_at" timestamp DEFAULT now() NOT NULL,
	CONSTRAINT "slide_results_result_id_slide_id_unique" UNIQUE("result_id","slide_id")
);
--> statement-breakpoint
ALTER TABLE "assignment_results" ADD CONSTRAINT "assignment_results_studentId_users_id_fkey" FOREIGN KEY ("studentId") REFERENCES "users"("id");--> statement-breakpoint
ALTER TABLE "assignment_results" ADD CONSTRAINT "assignment_results_assignmentId_assignments_id_fkey" FOREIGN KEY ("assignmentId") REFERENCES "assignments"("id");--> statement-breakpoint
ALTER TABLE "assignment_results" ADD CONSTRAINT "assignment_results_currentSlideId_slides_id_fkey" FOREIGN KEY ("currentSlideId") REFERENCES "slides"("id");--> statement-breakpoint
ALTER TABLE "slide_results" ADD CONSTRAINT "slide_results_result_id_assignment_results_id_fkey" FOREIGN KEY ("result_id") REFERENCES "assignment_results"("id");--> statement-breakpoint
ALTER TABLE "slide_results" ADD CONSTRAINT "slide_results_slide_id_slides_id_fkey" FOREIGN KEY ("slide_id") REFERENCES "slides"("id");