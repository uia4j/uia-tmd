CREATE TABLE "zzt_qtz_clock"
(
 "id" character varying(64) NOT NULL,
 "start_time" timestamp without time zone,
 "end_time" timestamp without time zone,
 "clock_type" character varying(10),
 "clock_interval" double precision,
 "tmd_job_bo" character varying(64),
 "trigger_startup" character varying(1),
 CONSTRAINT zzt_qtz_clock_pkey PRIMARY KEY (id)
)
