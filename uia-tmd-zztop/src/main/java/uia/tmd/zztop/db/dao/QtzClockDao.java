package uia.tmd.zztop.db.dao;

import java.sql.Connection;

import uia.dao.TableDao;
import uia.tmd.zztop.db.QtzClock;
import uia.tmd.zztop.db.conf.TmdDB;

public class QtzClockDao extends TableDao<QtzClock> {

    public QtzClockDao(Connection conn) {
    	super(conn, TmdDB.forTable(QtzClock.class));
    }
}
