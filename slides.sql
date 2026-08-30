--
-- PostgreSQL database dump
--

\restrict UHEPdR1YVTzzbyRV96XPJh0gOFu1iHCBar5mGSnfxUgexO2vq0iC6KxpIyrUVlV

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
-- Data for Name: slides; Type: TABLE DATA; Schema: public; Owner: school-db-user
--

INSERT INTO public.slides VALUES (1, 1, 0, '{"correct": 2, "question": "Сколько углов в треугольнике", "variants": ["2", "3", "5", "7"]}', '2026-08-27 18:31:24.915455', '2026-08-27 18:31:24.915455');
INSERT INTO public.slides VALUES (2, 1, 1, '{"correct": 4, "question": "Сколько градусив в тупом улике", "variants": ["45", "234", "10 < угол < 15", "ду ноу"]}', '2026-08-27 18:31:24.942389', '2026-08-27 18:31:24.942389');
INSERT INTO public.slides VALUES (3, 1, 2, '{"correct": 2, "question": "Какое из следующих утверждений о прямоугольном треугольнике является верным?", "variants": ["Сумма острых углов всегда равна 180 градусам.", "Квадрат гипотенузы равен сумме квадратов катетов.", "Гипотенуза всегда короче любого из катетов.", "В нем может быть два прямых угла."]}', '2026-08-27 18:31:24.948673', '2026-08-27 18:31:24.948673');
INSERT INTO public.slides VALUES (4, 1, 3, '{"correct": "3", "question": "Что происходит с графиком функции y=f(x), если к аргументу добавить положительное число a, то есть функция примет вид y=f(x+a)?", "variants": ["График смещается на a единиц вверх по оси y.", "График смещается на a единиц вниз по оси y.", "График смещается н a единиц вправо по оси x.", "График смещается на a единиц влево по оси x ."]}', '2026-08-27 18:31:24.955423', '2026-08-27 18:31:24.955423');


--
-- Name: slides_id_seq; Type: SEQUENCE SET; Schema: public; Owner: school-db-user
--

SELECT pg_catalog.setval('public.slides_id_seq', 4, true);


--
-- PostgreSQL database dump complete
--

\unrestrict UHEPdR1YVTzzbyRV96XPJh0gOFu1iHCBar5mGSnfxUgexO2vq0iC6KxpIyrUVlV

