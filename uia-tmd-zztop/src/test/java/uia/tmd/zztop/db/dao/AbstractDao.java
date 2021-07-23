package uia.tmd.zztop.db.dao;

import uia.tmd.zztop.db.conf.ZZTOP;

public abstract class AbstractDao {

	public AbstractDao() throws Exception {
    	ZZTOP.initial();
    }
}
