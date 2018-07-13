package uia.tmd.zztop.db;

import java.util.Date;
import java.util.UUID;

public class TmdTxTable {

    private String id;

    private Date txTime;

    private String tableName;

    public TmdTxTable() {
        this.id = System.currentTimeMillis() + "-" + UUID.randomUUID().toString();
        this.txTime = new Date();
    }

    public TmdTxTable(String tableName) {
        this.id = System.currentTimeMillis() + "-" + UUID.randomUUID().toString();
        this.txTime = new Date();
        this.tableName = tableName;
    }

    public TmdTxTable(TmdTxTable data) {
        this.id = data.id;
        this.txTime = data.txTime;
        this.tableName = data.tableName;
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

    @Override
    public TmdTxTable clone() {
        return new TmdTxTable(this);
    }

    @Override
    public String toString() {
        return this.id;
    }
}
