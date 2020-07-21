package uia.tmd.zztop;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        FileInputStream fis = new FileInputStream(appConfig);
        Properties PROPS = System.getProperties();
        PROPS.load(fis);
        System.setProperties(PROPS);
    }
}
