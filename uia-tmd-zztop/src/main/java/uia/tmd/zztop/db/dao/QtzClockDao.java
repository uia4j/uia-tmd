package uia.tmd.zztop.db.dao;

import java.sql.Connection;

import uia.dao.TableDao;
import uia.dao.TableDaoHelper;
import uia.dao.annotation.DaoInfo;
import uia.tmd.zztop.db.QtzClock;

@DaoInfo(type = QtzClock.class)
public class QtzClockDao extends TableDao<QtzClock> {

    public QtzClockDao(Connection conn, TableDaoHelper<QtzClock> tableHelper) {
    	super(conn, tableHelper);
    } 
}
