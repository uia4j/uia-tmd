package uia.tmd;

import java.util.Arrays;

import uia.tmd.model.xml.ItemType;
import uia.tmd.model.xml.TaskType;
import uia.utils.dao.where.Where;

public interface ItemRunner {

    public WhereType prepare(JobRunner jobRunner, ItemType itemType, TaskType taskType, String whereBase) throws TmdException;

    public void run(JobRunner jobRunner, ItemType itemType, TaskType taskType, TaskRunner taskRunner, WhereType where) throws TmdException;

    public static class WhereType {

        public String sql;

        public Object[] paramValues;

        public WhereType(String sql, Object... paramValues) {
            this.sql = sql;
            this.paramValues = paramValues;
        }

        public WhereType and(String sql) {
            if (sql == null) {
                return this;
            }

            if (this.sql != null) {
                this.sql += (" and " + sql);
            }
            else {
                this.sql = sql;
            }
            return this;
        }

        @Override
        public String toString() {
            return "where: " + Where.toString(this.sql, Arrays.asList(paramValues));
        }
    }

}
