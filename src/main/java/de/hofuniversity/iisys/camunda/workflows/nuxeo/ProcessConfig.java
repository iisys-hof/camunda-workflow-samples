package de.hofuniversity.iisys.camunda.workflows.nuxeo;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessConfig {
    public static final String PROPERTIES = "nuxeo-process-test";
    private static transient Logger LOG = LoggerFactory.getLogger(ProcessConfig.class);
    public static final String NUXEO_URL = "nuxeo.url";

    public static final String DEBUG_USER = "debug.user";
    public static final String DEBUG_PASSWORD = "debug.password";

    private static ProcessConfig fInstance;

    private final Map<String, String> fConfig;

    public static synchronized ProcessConfig getInstance() {
        if (fInstance == null) {
            fInstance = new ProcessConfig();
        }

        return fInstance;
    }

    public ProcessConfig() {
        fConfig = new HashMap<String, String>();

        readConfig();
    }

    private void readConfig() {
        try {
            final ClassLoader loader = Thread.currentThread().getContextClassLoader();
            ResourceBundle rb = ResourceBundle.getBundle(PROPERTIES, Locale.getDefault(), loader);
            LOG.debug("ResourceBundle:" + rb);
            String key = null;
            String value = null;

            Enumeration<String> keys = rb.getKeys();
            while (keys.hasMoreElements()) {
                key = keys.nextElement();
                value = rb.getString(key);

                fConfig.put(key, value);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // finally {
        // fConfig.put("nuxeo.url", "http://127.0.0.1:8080/nuxeo/");
        // fConfig.put("debug.user", "demo");
        // fConfig.put("debug.password", "secret");
        // }
    }

    public Map<String, String> getConfiguration() {
        return fConfig;
    }
}
