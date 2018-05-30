package uia.tmd.model;

import java.io.File;
import java.util.List;

import org.junit.Test;

import uia.tmd.model.xml.DbServerType;
import uia.tmd.model.xml.MColumnType;
import uia.tmd.model.xml.MCriteriaType;
import uia.tmd.model.xml.MTableType;
import uia.tmd.model.xml.PlanType;
import uia.tmd.model.xml.SourceSelectType;
import uia.tmd.model.xml.TargetUpdateType;
import uia.tmd.model.xml.TaskType;
import uia.tmd.model.xml.TmdType;

public class TmdTypeHelperTest {

    @Test
    public void testSample() throws Exception {
        TmdType tmd = TmdTypeHelper.load(new File(TmdTypeHelperTest.class.getResource("sample2.xml").toURI()));
        for (TaskType t : tmd.getTaskSpace().getTask()) {
            System.out.println("-------------------------------------------");
            System.out.println("task: " + t.getName());

            print(t.getSourceSelect());
            print(t.getTargetUpdate());
            if (t.getNext() != null) {
                for (PlanType p : t.getNext().getPlan()) {
                    print(p);
                }
            }

            System.out.println();
        }
        for (MTableType tt : tmd.getTableSpace().getTable()) {
            System.out.println(tt.getName() + ", pk:" + tt.getPk().getName());
        }

        for (DbServerType svr : tmd.getDatabaseSpace().getDbServer()) {
            System.out.println(svr.getId() + ":" + svr.getHost());
        }
    }

    private void print(PlanType plan) {
        System.out.println("");
        System.out.println("plan:" + plan.getTaskName());
        if (plan.getRule() != null) {
            for (MCriteriaType criteria : plan.getRule().getCriteria()) {
                System.out.println("  criteria: " + criteria.getColumn() + "='" + criteria.getValue() + "'");
            }
        }
        if (plan.getJoin() != null) {
            for (MColumnType column : plan.getJoin().getColumn()) {
                System.out.println("  column: " + column.getValue());
            }
        }
        if (plan.getWhere() != null) {
            for (MCriteriaType criteria : plan.getWhere().getCriteria()) {
                System.out.println("  where: " + criteria.getColumn() + "='" + criteria.getValue() + "'");
            }
        }
    }

    private void print(SourceSelectType ss) {
        System.out.println("  table: " + ss.getTable());
        System.out.print("    pk: ");
        if (ss.getPk() != null) {
            for (String name : ss.getPk().getName()) {
                System.out.print(name + ",");
            }
        }
        System.out.println();
    }

    private void print(TargetUpdateType ts) {
        System.out.println("  table: " + ts.getTable());
        System.out.print("    pk: ");
        if (ts.getPk() != null) {
            for (String name : ts.getPk().getName()) {
                System.out.print(name + ",");
            }
        }
        System.out.println();
        System.out.println("    columns: ");
        if (ts.getColumns() != null) {
            print(ts.getColumns().getColumn());
        }
    }

    private void print(List<MColumnType> columns) {
        for (MColumnType c : columns) {
            System.out.println("      " + c.getValue() + "(source=" + c.getSource() + ") pk=" + c.isPk());
        }
    }
}
