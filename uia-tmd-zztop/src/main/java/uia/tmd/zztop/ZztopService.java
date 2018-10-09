package uia.tmd.zztop;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import uia.tmd.zztop.qtz.QtzJobFactory;

public final class ZztopService {

    private static final Logger LOGGER = LogManager.getLogger(ZztopService.class);

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
