package uia.tmd.zztop.db.dao;

import java.sql.Connection;
import java.util.List;

import uia.dao.TableDao;
import uia.dao.TableDaoHelper;
import uia.dao.annotation.DaoInfo;
import uia.dao.annotation.DeleteInfo;
import uia.dao.annotation.SelectInfo;
import uia.tmd.zztop.db.ExecTask;

@DaoInfo(type = ExecTask.class)
public abstract class ExecTaskDao extends TableDao<ExecTask> {

    public ExecTaskDao(Connection conn, TableDaoHelper<ExecTask> tableHelper) {
    	super(conn, tableHelper);
    }

    @SelectInfo(sql = "WHERE exec_job_bo=? ORDER BY group_id,task_path")
    public abstract List<ExecTask> selectByJob(String execJobId);

    @DeleteInfo(sql = "WHERE exec_job_bo=?")
    public abstract int deleteByJob(String execJobId);
}
