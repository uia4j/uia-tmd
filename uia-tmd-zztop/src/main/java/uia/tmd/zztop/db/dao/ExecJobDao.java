package uia.tmd.zztop.db.dao;

import java.sql.Connection;

import uia.dao.TableDao;
import uia.dao.TableDaoHelper;
import uia.dao.annotation.DaoInfo;
import uia.tmd.zztop.db.ExecJob;

@DaoInfo(type = ExecJob.class)
public class ExecJobDao extends TableDao<ExecJob> {

    public ExecJobDao(Connection conn, TableDaoHelper<ExecJob> tableHelper) {
    	super(conn, tableHelper);
    }
} 