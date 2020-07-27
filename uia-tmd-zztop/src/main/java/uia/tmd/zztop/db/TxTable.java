package uia.tmd.zztop.db;

import java.util.Date;
import java.util.UUID;

import uia.dao.annotation.ColumnInfo;
import uia.dao.annotation.TableInfo;

@TableInfo(name = "zzt_tx_table")
public class TxTable {

	public static final String KEY = "zzt_tx_table";
	
	@ColumnInfo(name = "id", primaryKey = true)
    private String id;

	@ColumnInfo(name = "tx_time")
    private Date txTime;

	@ColumnInfo(name = "table_name")
    private String tableName;

	@ColumnInfo(name = "exec_job_bo")
    private String execJobBo;

    public TxTable() {
        this.id = System.currentTimeMillis() + "-" + UUID.randomUUID().toString();
        this.txTime = new Date();
    }

    public TxTable(String tableName) {
        this.id = System.currentTimeMillis() + "-" + UUID.randomUUID().toString();
        this.txTime = new Date();
        this.tableName = tableName;
    }

    public TxTable(TxTable data) {
        this.id = data.id;
        this.txTime = data.txTime;
        this.tableName = data.tableName;
        this.execJobBo = data.execJobBo;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getTxTime() {
        return this.txTime;
    }

    public void setTxTime(Date txTime) {
        this.txTime = txTime;
    }

    public String getTableName() {
        return this.tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getExecJobBo() {
		return execJobBo;
	}

	public void setExecJobBo(String execJobBo) {
		this.execJobBo = execJobBo;
	}

	@Override
    public TxTable clone() {
        return new TxTable(this);
    }

    @Override
    public String toString() {
        return this.id;
    }
}
