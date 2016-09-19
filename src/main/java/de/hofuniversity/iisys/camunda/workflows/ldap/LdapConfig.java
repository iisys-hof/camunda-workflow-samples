package de.hofuniversity.iisys.camunda.workflows.ldap;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class LdapConfig {
    public static final String PROPERTIES = "ldap-config";

    public static final String LDAP_HOST = "ldap.host";
    public static final String LDAP_PORT = "ldap.port";

    public static final String LDAP_USER = "ldap.user";
    public static final String LDAP_PASSWORD = "ldap.password";

    public static final String LDAP_BASEDN = "ldap.basedn";

    private static LdapConfig fInstance;

    private final Map<String, String> fConfig;

    public static synchronized LdapConfig getInstance() {
        if (fInstance == null) {
            fInstance = new LdapConfig();
        }

        return fInstance;
    }

    public LdapConfig() {
        fConfig = new HashMap<String, String>();

        readConfig();
    }

    private void readConfig() {
        try {
            final ClassLoader loader = Thread.currentThread().getContextClassLoader();
            ResourceBundle rb = ResourceBundle.getBundle(PROPERTIES, Locale.getDefault(), loader);

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
