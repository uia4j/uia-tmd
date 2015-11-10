package uia.tmd.model;

import java.io.File;

import org.junit.Test;

import uia.tmd.model.xml.PlanType;
import uia.tmd.model.xml.TmdType;

public class TmdTypeHelperTest {

    @Test
    public void testSample() throws Exception {
        TmdType tmd = TmdTypeHelper.load(new File(TmdTypeHelperTest.class.getResource("sample.xml").toURI()));
        tmd.getTaskSpace().getTask().stream().forEach(t -> {
            System.out.println(t.getName() + ":");
            System.out.println("    source: " + t.getSource());
            System.out.println("    target: " + t.getTarget());
            System.out.println("    select: " + t.getSourceSelect().getSql());
            System.out.println("    insert: " + t.getTargetInsert().getSql());
            if (t.getNexts() != null) {
                t.getNexts().getPlan().stream().forEach(p -> print(p, t.getName()));
            }
            System.out.println();
        });

        tmd.getDatabaseSpace().getDbServer().stream().forEach(s -> {
            System.out.println(s.getId() + ":" + s.getHost());
        });
    }

    private void print(PlanType plan, String master) {
        System.out.println();
        System.out.println("  " + plan.getName() + ":");
        System.out.println("    select: " + plan.getSourceSelect().getSql());
        System.out.println("    insert: " + plan.getTargetInsert().getSql());
        System.out.println("    link:");
        plan.getSourceSelect().getWhere().getColumn().stream().forEach(c -> {
            System.out.println("      {" + master + "}." + c.getSource() + " <---> " + c.getValue());
        });
        if (plan.getNexts() != null) {
            plan.getNexts().getPlan().stream().forEach(p -> print(p, plan.getName()));
        }
    }
}
