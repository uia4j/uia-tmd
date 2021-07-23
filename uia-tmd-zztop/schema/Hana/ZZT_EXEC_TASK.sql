CREATE COLUMN TABLE "ZZT_EXEC_TASK"
(
 "ID" NVARCHAR(2048) NOT NULL,
 "TMD_TASK_BO" NVARCHAR(2048),
 "EXEC_JOB_BO" NVARCHAR(2048),
 "TABLE_NAME" NVARCHAR(128),
 "SQL_WHERE" NVARCHAR(2048),
 "TRIGGERED_BY" NVARCHAR(2048),
 "RESULT_COUNT" INT,
 "TASK_PATH" NVARCHAR(2048),
 "GROUP_ID" NVARCHAR(128),
 PRIMARY KEY ("ID")
);