--
-- PostgreSQL database dump
--

\restrict X6cFnfIhXqyuMz1GYcMcCDD7YP0KC8GhG2C3afp2edwAeFTfNEUlKikKMbnsMD2

-- Dumped from database version 18.6 (Debian 18.6-1.pgdg13+2)
-- Dumped by pg_dump version 18.6 (Debian 18.6-1.pgdg13+2)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: school-db-user
--

INSERT INTO public.users (id, name, role, created_at) VALUES (1, 'sendai', 'admin', '2026-08-27 15:03:51.851498');

INSERT INTO public.users (name, role) VALUES ('kat', 'teacher');

--
-- Name: users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: school-db-user
--

SELECT pg_catalog.setval('public.users_id_seq', 2, true);


--
-- PostgreSQL database dump complete
--

\unrestrict X6cFnfIhXqyuMz1GYcMcCDD7YP0KC8GhG2C3afp2edwAeFTfNEUlKikKMbnsMD2

