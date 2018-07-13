package uia.tmd.zztop.db;

import java.util.Date;

public class TmdTxKey {

    private String id;

    private Date txTime;

    private String tableName;

    public TmdTxKey() {
    }

    public TmdTxKey(TmdTxKey data) {
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
    public TmdTxKey clone() {
        return new TmdTxKey(this);
    }

    @Override
    public String toString() {
        return this.id;
    }
}
