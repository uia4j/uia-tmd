CREATE TABLE "zzt_tx_table"
(
 "id" character varying(64) NOT NULL,
 "tx_time" timestamp without time zone,
 "table_name" character varying(64),
 CONSTRAINT zzt_tx_table_pkey PRIMARY KEY (id)
)