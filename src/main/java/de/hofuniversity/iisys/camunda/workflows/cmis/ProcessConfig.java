package de.hofuniversity.iisys.camunda.workflows.cmis;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class ProcessConfig {
    public static final String PROPERTIES = "nuxeo-process-test";

    public static final String CMIS_URL = "cmis.url";

    public static final String DEBUG_USER = "debug.user";
    public static final String DEBUG_PASSWORD = "debug.password";

    public static final String BINDING_TYPE = "cmis.bindingtype";

    public static final String REPOSITORY = "cmis.repository";

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
            final ClassLoader loader = Thread.currentThread()
                    .getContextClassLoader();
            ResourceBundle rb = ResourceBundle.getBundle(PROPERTIES,
                    Locale.getDefault(), loader);

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
    }

    public Map<String, String> getConfiguration() {
        return fConfig;
    }
}
