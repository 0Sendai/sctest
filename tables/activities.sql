--
-- PostgreSQL database dump
--

\restrict bgj3ov4Ct0P6OkgkkHewHQbqPZo7rfCdAjPKhfeP4GiG0fKvidUvQ5j3DVyNaKM

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
-- Data for Name: activities; Type: TABLE DATA; Schema: public; Owner: school-db-user
--

INSERT INTO public.activities VALUES (1, 'quiz', 1, 0, '{"title": "Тестовый"}', '2026-08-27 18:06:55.85073', '2026-08-27 18:06:55.85073');


--
-- Name: activities_id_seq; Type: SEQUENCE SET; Schema: public; Owner: school-db-user
--

SELECT pg_catalog.setval('public.activities_id_seq', 1, true);


--
-- PostgreSQL database dump complete
--

\unrestrict bgj3ov4Ct0P6OkgkkHewHQbqPZo7rfCdAjPKhfeP4GiG0fKvidUvQ5j3DVyNaKM

