package uia.tmd.model;

import java.io.File;
import java.util.List;

import org.junit.Test;

import uia.tmd.model.xml.ColumnType;
import uia.tmd.model.xml.CriteriaType;
import uia.tmd.model.xml.DbServerType;
import uia.tmd.model.xml.PlanType;
import uia.tmd.model.xml.SourceSelectType;
import uia.tmd.model.xml.TableType;
import uia.tmd.model.xml.TargetUpdateType;
import uia.tmd.model.xml.TaskType;
import uia.tmd.model.xml.TmdType;

public class TmdTypeHelperTest {

    @Test
    public void testSample() throws Exception {
        TmdType tmd = TmdTypeHelper.load(new File(TmdTypeHelperTest.class.getResource("sample.xml").toURI()));
        for (TaskType t : tmd.getTaskSpace().getTask()) {
            System.out.println("-------------------------------------------");
            System.out.println("task: " + t.getName());

            print(t.getSourceSelect());
            print(t.getTargetUpdate());
            if (t.getNexts() != null) {
                for (PlanType p : t.getNexts().getPlan()) {
                    print(p);
                }
            }

            System.out.println();
        }
        for (TableType tt : tmd.getTableSpace().getTable()) {
            System.out.println(tt.getName() + ", pk:" + tt.getPks().getPk());
        }

        for (DbServerType svr : tmd.getDatabaseSpace().getDbServer()) {
            System.out.println(svr.getId() + ":" + svr.getHost());
        }
    }

    private void print(PlanType plan) {
        System.out.println("");
        System.out.println("plan:" + plan.getName());
        if (plan.getRule() != null) {
            for (CriteriaType criteria : plan.getRule().getCriteria()) {
                System.out.println("  criteria: " + criteria.getColumn() + "='" + criteria.getValue() + "'");
            }
            for (ColumnType column : plan.getJoin().getColumn()) {
                System.out.println("  column: " + column.getValue());
            }
        }
    }

    private void print(SourceSelectType ss) {
        System.out.println("  table: " + ss.getTable());
        System.out.println("    where: ");
        if (ss.getWhere() != null) {
            print(ss.getWhere().getColumn());
        }
    }

    private void print(TargetUpdateType ts) {
        System.out.println("  table: " + ts.getTable());
        System.out.println("    columns: ");
        if (ts.getColumns() != null) {
            print(ts.getColumns().getColumn());
        }
    }

    private void print(List<ColumnType> columns) {
        for (ColumnType c : columns) {
            System.out.println("      " + c.getValue() + "(source=" + c.getSource() + ") pk=" + c.isPk());
        }
    }
}
