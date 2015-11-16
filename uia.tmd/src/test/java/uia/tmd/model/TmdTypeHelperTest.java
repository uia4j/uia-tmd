package uia.tmd.model;

import java.io.File;
import java.util.List;

import org.junit.Test;

import uia.tmd.model.xml.ColumnType;
import uia.tmd.model.xml.DbServerType;
import uia.tmd.model.xml.PlanType;
import uia.tmd.model.xml.SourceSelectType;
import uia.tmd.model.xml.TargetUpdateType;
import uia.tmd.model.xml.TaskType;
import uia.tmd.model.xml.TmdType;

public class TmdTypeHelperTest {

    @Test
    public void testSample() throws Exception {
        TmdType tmd = TmdTypeHelper.load(new File(TmdTypeHelperTest.class.getResource("sample.xml").toURI()));
        for (TaskType t : tmd.getTaskSpace().getTask()) {
            System.out.println("source: " + t.getSource());
            System.out.println("target: " + t.getTarget());

            System.out.println("task:" + t.getName());
            print(t.getSourceSelect());
            print(t.getTargetUpdate());
            if (t.getNexts() != null) {
                for (PlanType p : t.getNexts().getPlan()) {
                    print(p);
                }
            }

            System.out.println();
        }

        for (DbServerType svr : tmd.getDatabaseSpace().getDbServer()) {
            System.out.println(svr.getId() + ":" + svr.getHost());
        }
    }

    private void print(PlanType plan) {
        System.out.println("");
        System.out.println("plan:" + plan.getName());
        print(plan.getSourceSelect());
        print(plan.getTargetUpdate());
        plan.getSourceSelect();
        if (plan.getNexts() != null) {
            for (PlanType p : plan.getNexts().getPlan()) {
                print(p);
            }
        }
    }

    private void print(SourceSelectType ss) {
        System.out.println("  table: " + ss.getTable());
        System.out.println("    columns: ");
        print(ss.getColumns().getColumn());
        System.out.println("    wehere: ");
        print(ss.getWhere().getColumn());
    }

    private void print(TargetUpdateType ts) {
        System.out.println("  table:" + ts.getTable());
        System.out.println("    columns: ");
        print(ts.getColumns().getColumn());
        System.out.println("    wehere: ");
        print(ts.getWhere().getColumn());
    }

    private void print(List<ColumnType> columns) {
        for (ColumnType c : columns) {
            System.out.println("      " + c.getValue() + "(source=" + c.getSource() + ")");
        }
    }
}
