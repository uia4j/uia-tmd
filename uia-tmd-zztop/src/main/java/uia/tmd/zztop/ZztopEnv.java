package uia.tmd.zztop;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uia.tmd.zztop.db.conf.ZZTOP;

public final class ZztopEnv {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZztopEnv.class);

    public static void initial() throws Exception {
        try {
            InputStream is = ZztopEnv.class.getClassLoader().getResourceAsStream("META-INF/maven/org.uia.solution/uia-tmd-zztop/pom.properties");
            if (is != null) {
                Properties pom = new Properties();
                pom.load(is);
                LOGGER.info("GROUP:       " + pom.get("groupId"));
                LOGGER.info("ARTIFACT:    " + pom.get("artifactId"));
                LOGGER.info("VERSION:     " + pom.get("version"));
            }
        }
        catch (Exception ex) {

        }

        String appConfig = System.getProperty("user.dir") + System.getProperty("file.separator") + "app.properties";
        
        try (FileInputStream fis = new FileInputStream(appConfig)) {
            Properties p = new Properties(System.getProperties());
            p.load(fis);
            System.setProperties(p);
        }
        
        try {
            Class.forName("com.sap.db.jdbc.Driver").newInstance();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        ZZTOP.initial(
                System.getProperty("tmd.zztop.db"),
                true,
                System.getProperty("tmd.zztop.db.conn"),
                System.getProperty("tmd.zztop.db.user"),
                System.getProperty("tmd.zztop.db.pwd"),
                System.getProperty("tmd.zztop.db.schema"));
    }
}
