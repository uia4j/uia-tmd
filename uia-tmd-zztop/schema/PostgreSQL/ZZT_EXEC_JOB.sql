CREATE TABLE "zzt_exec_job"
(
 "id" character varying(64) NOT NULL,
 "tmd_job_bo" character varying(64),
 "database_source" character varying(100),
 "database_target" character varying(100),
 "tmd_task_log_bo" character varying(64),
 "executed_date" timestamp without time zone,
 "executed_time" timestamp without time zone,
 "executed_result" character varying(20),
 "run_state" character varying(20),
 "delete_after" timestamp without time zone,
 CONSTRAINT zzt_exec_job_pkey PRIMARY KEY (id)
)
