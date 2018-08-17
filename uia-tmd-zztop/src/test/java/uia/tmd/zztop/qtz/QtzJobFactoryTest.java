package uia.tmd.zztop.qtz;

import org.junit.Test;

import ui.tmd.zztop.qtz.QtzJobFactory;

public class QtzJobFactoryTest {

    @Test
    public void test() throws Exception {
        QtzJobFactory factory = new QtzJobFactory();
        factory.start();
        Thread.sleep(145000);

    }
}
