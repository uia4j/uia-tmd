package uia.tmd.zztop;

import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uia.tmd.zztop.qtz.QtzJobFactory;

public final class ZztopConsole {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZztopConsole.class);

    private static Scanner SCANNER;

    public static void main(String[] args) {
        try {
            LOGGER.info("=============== ZZTOP Console ===============");
            ZztopEnv.initial();

            QtzJobFactory factory = new QtzJobFactory();
            factory.start();

            try {
                String cmd = "";
                SCANNER = new Scanner(System.in);
                while (!"q".equalsIgnoreCase(cmd)) {
                    cmd = SCANNER.nextLine();
                }
            }
            catch (Exception ex) {
                LOGGER.error("broken #1", ex);
            }

            factory.shutdown();

        }
        catch (Exception ex) {
            LOGGER.error("broken #2", ex);
        }

    }
}
