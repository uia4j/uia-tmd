package uia.tmd.zztop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uia.tmd.zztop.qtz.QtzJobFactory;

public final class ZztopService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZztopService.class);

    public static void main(String[] args) {
        try {
            LOGGER.info("=============== ZZTOP Service ===============");
            ZztopEnv.initial();

            QtzJobFactory factory = new QtzJobFactory();
            factory.start();

        }
        catch (Exception ex) {
            LOGGER.error("broken #2", ex);
        }

    }
}
