package uia.tmd;

import org.junit.Test;

public class WhereTest {

    @Test
    public void test() {
        WhereEq eq = new WhereEq("a", "10");
        System.out.println(eq.getParamName() + "> " + eq.sql());

        WhereBetween bt = new WhereBetween("a", "10", "12");
        System.out.println(bt.getParamName() + "> " + bt.sql());

        WhereSpecial sp = new WhereSpecial("time", "time between '*-2h' and '*'");
        System.out.println(sp.getParamName() + "> " + sp.sql());
    }
}
