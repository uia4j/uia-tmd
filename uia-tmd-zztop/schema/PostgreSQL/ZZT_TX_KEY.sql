CREATE TABLE "zzt_tx_key"
(
 "id" character varying(1000) NOT NULL,
 "tx_time" timestamp without time zone,
 "table_name" character varying(64),
 CONSTRAINT zzt_tx_key_pkey PRIMARY KEY (id)
)
