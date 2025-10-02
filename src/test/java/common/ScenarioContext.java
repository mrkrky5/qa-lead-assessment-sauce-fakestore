package common;

import io.qameta.allure.Allure;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 Thread-safe, per-scenario key-value store backed by ThreadLocal.
 */
public class ScenarioContext {

    // Prevent instantiation — utility holder
    private ScenarioContext() {}

    // Per-thread (per-scenario) storage
    private static final ThreadLocal<Map<String, Object>> CTX =
        ThreadLocal.withInitial(HashMap::new);

    // Well-known keys to avoid magic strings
    private static final String API_TITLE_KEY = "api.title";
    private static final String API_PRICE_KEY = "api.price";

    /**
     Put an arbitrary value into the scenario-scoped context.
     */
    public static void put(String key, Object value) {
        CTX.get().put(key, value);
    }

    /**
     Get a value from the scenario-scoped context and cast it to the given type.
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(String key, Class<T> type) {
        return (T) CTX.get().get(key);
    }

    /** Clear the scenario-scoped storage. Call this on teardown if needed. */
    public static void clear() {
        CTX.get().clear();
    }

    /**
     Attach a plain text snippet to the Allure report.
     */
    public static void attachText(String name, String content) {
        try {
            Allure.addAttachment(name, content);
        } catch (Throwable ignore) {
            // No-op if Allure is not available
        }
    }

    /** Backward-compatible setter for API title (stored under a well-known key). */
    public static void setApiTitle(String title) {
        put(API_TITLE_KEY, title);
    }

    /** Backward-compatible setter for API price (stored under a well-known key). */
    public static void setApiPrice(BigDecimal price) {
        put(API_PRICE_KEY, price);
    }

    /** Backward-compatible getter for API title. */
    public static String getApiTitle() {
        return get(API_TITLE_KEY, String.class);
    }

    /** Backward-compatible getter for API price. */
    public static BigDecimal getApiPrice() {
        return get(API_PRICE_KEY, BigDecimal.class);
    }
}
