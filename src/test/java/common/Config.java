package common;

import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;

public class Config {
    private static final Properties p = new Properties();

    static {
        // Load config.properties from test classpath
        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            URL url = cl.getResource("config.properties");
            Objects.requireNonNull(
                url,
                "config.properties not found on test classpath (expected under src/test/resources)"
            );

            try (InputStream is = url.openStream()) {
                // Properties default ISO-8859-1; içerikte UTF-8 char yoksa sorun değil
                p.load(is);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load config.properties", e);
        }
    }

    /** System property varsa onu, yoksa dosyadakini döner. */
    public static String get(String key) {
        String sys = System.getProperty(key);
        if (sys != null && !sys.isBlank()) return sys;

        // Backward-compat: old key aliases (ui.baseUrl -> web.baseUrl)
        if ("web.baseUrl".equals(key) && p.getProperty("web.baseUrl") == null) {
            return p.getProperty("ui.baseUrl"); // allow old naming
        }
        return p.getProperty(key);
    }

    public static boolean getBoolean(String key) {
        String v = get(key);
        return v != null && Boolean.parseBoolean(v.trim());
    }

    public static int getInt(String key) {
        String v = requireNonBlank(get(key), key);
        return Integer.parseInt(v.trim());
    }

    public static String apiBaseUrl() {
        return requireNonBlank(get("api.baseUrl"), "api.baseUrl").trim();
    }

    public static String webBaseUrl() {
        // accepts web.baseUrl or (fallback) ui.baseUrl
        String v = get("web.baseUrl");
        if (v == null) v = get("ui.baseUrl");
        return requireNonBlank(v, "web.baseUrl (or legacy ui.baseUrl)").trim();
    }

    private static String requireNonBlank(String v, String key) {
        if (v == null || v.isBlank()) {
            throw new IllegalStateException(
                "Missing configuration key: " + key + " (in config.properties or as -D" + key + ")"
            );
        }
        return v;
    }

    private Config() {}
}
