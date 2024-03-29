CREATE COLUMN TABLE "ZZT_EXEC_JOB"
(
 "ID" NVARCHAR(2048) NOT NULL,
 "TMD_JOB_BO" NVARCHAR(2048),
 "DATABASE_SOURCE" NVARCHAR(128),
 "DATABASE_TARGET" NVARCHAR(128),
 "TMD_TASK_LOG_BO" NVARCHAR(2048),
 "EXECUTED_DATE" TIMESTAMP,
 "EXECUTED_TIME" TIMESTAMP,
 "EXECUTED_RESULT" NVARCHAR(128),
 "RUN_STATE" NVARCHAR(128),
 "DELETE_AFTER" TIMESTAMP,
 PRIMARY KEY ("ID")
);