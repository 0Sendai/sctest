
CREATE TYPE public.assignment_result_status AS ENUM (
    'COMPLETED',
    'NOT_STARTED',
    'IN_PROGRESS'
);


ALTER TYPE public.assignment_result_status OWNER TO "school-db-user";

--
-- Name: user_role; Type: TYPE; Schema: public; Owner: school-db-user
--

CREATE TYPE public.user_role AS ENUM (
    'ROLE_ADMIN',
    'ROLE_TEACHER',
    'ROLE_STUDENT'
);


ALTER TYPE public.user_role OWNER TO "school-db-user";

--
-- Name: assignment_results; Type: TABLE; Schema: public; Owner: school-db-user
--

CREATE TABLE public.assignment_results (
    id integer NOT NULL,
    student_id integer CONSTRAINT "assignment_results_studentId_not_null" NOT NULL,
    assignment_id uuid CONSTRAINT "assignment_results_assignmentId_not_null" NOT NULL,
    current_slide_id integer,
    status public.assignment_result_status NOT NULL,
    started_at timestamp without time zone DEFAULT now() CONSTRAINT "assignment_results_startedAt_not_null" NOT NULL,
    completed_at timestamp without time zone,
    updated_at timestamp without time zone DEFAULT now() CONSTRAINT "assignment_results_updatedAt_not_null" NOT NULL,
    is_graded boolean NOT NULL
);


ALTER TABLE public.assignment_results OWNER TO "school-db-user";

--
-- Name: assignment_results_id_seq; Type: SEQUENCE; Schema: public; Owner: school-db-user
--

CREATE SEQUENCE public.assignment_results_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.assignment_results_id_seq OWNER TO "school-db-user";

--
-- Name: assignment_results_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: school-db-user
--

ALTER SEQUENCE public.assignment_results_id_seq OWNED BY public.assignment_results.id;


--
-- Name: assignments; Type: TABLE; Schema: public; Owner: school-db-user
--

CREATE TABLE public.assignments (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    title text NOT NULL,
    author_id integer NOT NULL,
    created_at timestamp without time zone DEFAULT now() NOT NULL,
    updated_at timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.assignments OWNER TO "school-db-user";

--
-- Name: slide_comments; Type: TABLE; Schema: public; Owner: school-db-user
--

CREATE TABLE public.slide_comments (
    id integer NOT NULL,
    assignment_id uuid NOT NULL,
    slide_id integer NOT NULL,
    student_id integer NOT NULL,
    comment text,
    assignment_result_id integer
);


ALTER TABLE public.slide_comments OWNER TO "school-db-user";

--
-- Name: slide_comments_id_seq; Type: SEQUENCE; Schema: public; Owner: school-db-user
--

CREATE SEQUENCE public.slide_comments_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.slide_comments_id_seq OWNER TO "school-db-user";

--
-- Name: slide_comments_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: school-db-user
--

ALTER SEQUENCE public.slide_comments_id_seq OWNED BY public.slide_comments.id;


--
-- Name: slide_results; Type: TABLE; Schema: public; Owner: school-db-user
--

CREATE TABLE public.slide_results (
    id integer NOT NULL,
    result_id integer NOT NULL,
    slide_id integer NOT NULL,
    answer jsonb,
    is_correct boolean,
    answered_at timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.slide_results OWNER TO "school-db-user";

--
-- Name: slide_results_id_seq; Type: SEQUENCE; Schema: public; Owner: school-db-user
--

CREATE SEQUENCE public.slide_results_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.slide_results_id_seq OWNER TO "school-db-user";

--
-- Name: slide_results_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: school-db-user
--

ALTER SEQUENCE public.slide_results_id_seq OWNED BY public.slide_results.id;


--
-- Name: slides; Type: TABLE; Schema: public; Owner: school-db-user
--

CREATE TABLE public.slides (
    id integer NOT NULL,
    assignment_id uuid NOT NULL,
    type text,
    "position" integer NOT NULL,
    content jsonb NOT NULL,
    created_at timestamp without time zone DEFAULT now() NOT NULL,
    updated_at timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.slides OWNER TO "school-db-user";

--
-- Name: slides_id_seq; Type: SEQUENCE; Schema: public; Owner: school-db-user
--

CREATE SEQUENCE public.slides_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.slides_id_seq OWNER TO "school-db-user";

--
-- Name: slides_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: school-db-user
--

ALTER SEQUENCE public.slides_id_seq OWNED BY public.slides.id;


--
-- Name: users; Type: TABLE; Schema: public; Owner: school-db-user
--

CREATE TABLE public.users (
    id integer NOT NULL,
    name text NOT NULL,
    password text,
    role public.user_role NOT NULL,
    created_at timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.users OWNER TO "school-db-user";

--
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: school-db-user
--

CREATE SEQUENCE public.users_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.users_id_seq OWNER TO "school-db-user";

--
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: school-db-user
--

ALTER SEQUENCE public.users_id_seq OWNED BY public.users.id;

--
-- Name: assignment_results id; Type: DEFAULT; Schema: public; Owner: school-db-user
--

ALTER TABLE ONLY public.assignment_results ALTER COLUMN id SET DEFAULT nextval('public.assignment_results_id_seq'::regclass);


--
-- Name: slide_comments id; Type: DEFAULT; Schema: public; Owner: school-db-user
--

ALTER TABLE ONLY public.slide_comments ALTER COLUMN id SET DEFAULT nextval('public.slide_comments_id_seq'::regclass);


--
-- Name: slide_results id; Type: DEFAULT; Schema: public; Owner: school-db-user
--

ALTER TABLE ONLY public.slide_results ALTER COLUMN id SET DEFAULT nextval('public.slide_results_id_seq'::regclass);


--
-- Name: slides id; Type: DEFAULT; Schema: public; Owner: school-db-user
--

ALTER TABLE ONLY public.slides ALTER COLUMN id SET DEFAULT nextval('public.slides_id_seq'::regclass);


--
-- Name: users id; Type: DEFAULT; Schema: public; Owner: school-db-user
--

ALTER TABLE ONLY public.users ALTER COLUMN id SET DEFAULT nextval('public.users_id_seq'::regclass);

--
-- Name: assignment_results assignment_results_pkey; Type: CONSTRAINT; Schema: public; Owner: school-db-user
--

ALTER TABLE ONLY public.assignment_results
    ADD CONSTRAINT assignment_results_pkey PRIMARY KEY (id);


--
-- Name: assignment_results assignment_results_studentId_assignmentId_unique; Type: CONSTRAINT; Schema: public; Owner: school-db-user
--

ALTER TABLE ONLY public.assignment_results
    ADD CONSTRAINT "assignment_results_studentId_assignmentId_unique" UNIQUE (student_id, assignment_id);


--
-- Name: assignments assignments_pkey; Type: CONSTRAINT; Schema: public; Owner: school-db-user
--

ALTER TABLE ONLY public.assignments
    ADD CONSTRAINT assignments_pkey PRIMARY KEY (id);


--
-- Name: slide_comments slide_comments_assignment_id_student_id_slide_id_unique; Type: CONSTRAINT; Schema: public; Owner: school-db-user
--

ALTER TABLE ONLY public.slide_comments
    ADD CONSTRAINT slide_comments_assignment_id_student_id_slide_id_unique UNIQUE (assignment_id, student_id, slide_id);


--
-- Name: slide_comments slide_comments_pkey; Type: CONSTRAINT; Schema: public; Owner: school-db-user
--

ALTER TABLE ONLY public.slide_comments
    ADD CONSTRAINT slide_comments_pkey PRIMARY KEY (id);


--
-- Name: slide_results slide_results_pkey; Type: CONSTRAINT; Schema: public; Owner: school-db-user
--

ALTER TABLE ONLY public.slide_results
    ADD CONSTRAINT slide_results_pkey PRIMARY KEY (id);


--
-- Name: slide_results slide_results_result_id_slide_id_unique; Type: CONSTRAINT; Schema: public; Owner: school-db-user
--

ALTER TABLE ONLY public.slide_results
    ADD CONSTRAINT slide_results_result_id_slide_id_unique UNIQUE (result_id, slide_id);


--
-- Name: slides slides_assignment_id_position_unique; Type: CONSTRAINT; Schema: public; Owner: school-db-user
--

ALTER TABLE ONLY public.slides
    ADD CONSTRAINT slides_assignment_id_position_unique UNIQUE (assignment_id, "position");


--
-- Name: slides slides_pkey; Type: CONSTRAINT; Schema: public; Owner: school-db-user
--

ALTER TABLE ONLY public.slides
    ADD CONSTRAINT slides_pkey PRIMARY KEY (id);


--
-- Name: users users_name_key; Type: CONSTRAINT; Schema: public; Owner: school-db-user
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_name_key UNIQUE (name);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: school-db-user
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: assignment_results assignment_results_assignmentId_assignments_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: school-db-user
--

ALTER TABLE ONLY public.assignment_results
    ADD CONSTRAINT "assignment_results_assignmentId_assignments_id_fkey" FOREIGN KEY (assignment_id) REFERENCES public.assignments(id);


--
-- Name: assignment_results assignment_results_currentSlideId_slides_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: school-db-user
--

ALTER TABLE ONLY public.assignment_results
    ADD CONSTRAINT "assignment_results_currentSlideId_slides_id_fkey" FOREIGN KEY (current_slide_id) REFERENCES public.slides(id);


--
-- Name: assignment_results assignment_results_studentId_users_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: school-db-user
--

ALTER TABLE ONLY public.assignment_results
    ADD CONSTRAINT "assignment_results_studentId_users_id_fkey" FOREIGN KEY (student_id) REFERENCES public.users(id);


--
-- Name: assignments assignments_author_id_users_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: school-db-user
--

ALTER TABLE ONLY public.assignments
    ADD CONSTRAINT assignments_author_id_users_id_fkey FOREIGN KEY (author_id) REFERENCES public.users(id);


--
-- Name: slide_comments slide_comments_assignment_id_assignments_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: school-db-user
--

ALTER TABLE ONLY public.slide_comments
    ADD CONSTRAINT slide_comments_assignment_id_assignments_id_fkey FOREIGN KEY (assignment_id) REFERENCES public.assignments(id);


--
-- Name: slide_comments slide_comments_slide_id_slides_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: school-db-user
--

ALTER TABLE ONLY public.slide_comments
    ADD CONSTRAINT slide_comments_slide_id_slides_id_fkey FOREIGN KEY (slide_id) REFERENCES public.slides(id);


--
-- Name: slide_comments slide_comments_student_id_users_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: school-db-user
--

ALTER TABLE ONLY public.slide_comments
    ADD CONSTRAINT slide_comments_student_id_users_id_fkey FOREIGN KEY (student_id) REFERENCES public.users(id);


--
-- Name: slide_results slide_results_result_id_assignment_results_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: school-db-user
--

ALTER TABLE ONLY public.slide_results
    ADD CONSTRAINT slide_results_result_id_assignment_results_id_fkey FOREIGN KEY (result_id) REFERENCES public.assignment_results(id);


--
-- Name: slide_results slide_results_slide_id_slides_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: school-db-user
--

ALTER TABLE ONLY public.slide_results
    ADD CONSTRAINT slide_results_slide_id_slides_id_fkey FOREIGN KEY (slide_id) REFERENCES public.slides(id);


--
-- Name: slides slides_assignment_id_assignments_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: school-db-user
--

ALTER TABLE ONLY public.slides
    ADD CONSTRAINT slides_assignment_id_assignments_id_fkey FOREIGN KEY (assignment_id) REFERENCES public.assignments(id);
