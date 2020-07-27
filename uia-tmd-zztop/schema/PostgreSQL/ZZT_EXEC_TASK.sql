CREATE TABLE "zzt_exec_task"
(
 "id" character varying(64) NOT NULL,
 "tmd_task_bo" character varying(64),
 "exec_job_bo" character varying(64),
 "table_name" character varying(64),
 "sql_where" character varying(2000),
 "triggered_by" character varying(64),
 "result_count" double precision,
 "task_path" character varying(200),
 CONSTRAINT zzt_exec_task_pkey PRIMARY KEY (id)
)
