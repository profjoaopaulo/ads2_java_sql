--
-- PostgreSQL database dump
--

-- Dumped from database version 16.9 (Ubuntu 16.9-0ubuntu0.24.04.1)
-- Dumped by pg_dump version 16.9 (Ubuntu 16.9-0ubuntu0.24.04.1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: pedidos; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.pedidos (
    id integer NOT NULL,
    cliente_nome text NOT NULL,
    data_pedido date DEFAULT CURRENT_DATE,
    valor_total numeric(10,2) NOT NULL,
    quantidade_itens integer NOT NULL,
    status text DEFAULT 'pendente'::text,
    desconto numeric(5,2) DEFAULT 0.00
);


ALTER TABLE public.pedidos OWNER TO postgres;

--
-- Name: pedidos_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.pedidos_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.pedidos_id_seq OWNER TO postgres;

--
-- Name: pedidos_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.pedidos_id_seq OWNED BY public.pedidos.id;


--
-- Name: pedidos id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.pedidos ALTER COLUMN id SET DEFAULT nextval('public.pedidos_id_seq'::regclass);


--
-- Data for Name: pedidos; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.pedidos (id, cliente_nome, data_pedido, valor_total, quantidade_itens, status, desconto) FROM stdin;
1	Ana Silva	2025-07-01	150.75	2	processado	5.00
2	Bruno Costa	2025-07-01	300.50	5	pendente	0.00
3	Carlos Souza	2025-07-02	80.00	1	entregue	2.50
4	Ana Silva	2025-07-03	220.00	3	processado	10.00
5	Daniel Lima	2025-07-03	45.90	1	pendente	0.00
6	Bruno Costa	2025-07-04	500.20	7	entregue	15.00
7	Fernanda Dias	2025-07-05	99.99	2	pendente	0.00
8	Carlos Souza	2025-07-06	120.00	1	cancelado	0.00
10	Gabriel Rocha	2025-07-07	350.00	4	entregue	20.00
13	Fernanda	2025-07-30	1000000.00	5	pago	20.00
\.


--
-- Name: pedidos_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.pedidos_id_seq', 13, true);


--
-- Name: pedidos pedidos_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.pedidos
    ADD CONSTRAINT pedidos_pkey PRIMARY KEY (id);


--
-- PostgreSQL database dump complete
--

