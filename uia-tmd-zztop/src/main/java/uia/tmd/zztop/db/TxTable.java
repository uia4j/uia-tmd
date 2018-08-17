package uia.tmd.zztop.db;

import java.util.Date;
import java.util.UUID;

public class TxTable {

    private String id;

    private Date txTime;

    private String tableName;

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
    public TxTable clone() {
        return new TxTable(this);
    }

    @Override
    public String toString() {
        return this.id;
    }
}
