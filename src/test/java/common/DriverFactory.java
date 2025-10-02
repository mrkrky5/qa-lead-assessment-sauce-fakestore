package common;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 Creates and manages a single WebDriver per thread.
 */
public class DriverFactory {

    private static final ThreadLocal<WebDriver> TL = new ThreadLocal<>();

    private DriverFactory() {
        // Utility class
    }

    /** Initialize a ChromeDriver for the current thread if not already present. */
    public static void init() {
        if (TL.get() != null) {
            return;
        }

        ChromeOptions opts = new ChromeOptions();

        // Headless mode (new engine is faster/stabler)
        if (Config.getBoolean("headless")) {
            opts.addArguments("--headless=new");
        }

        // Keep viewport predictable
        opts.addArguments("--window-size=1366,800");

        // Reduce flakiness from Chrome UI surfaces
        opts.addArguments("--disable-notifications");
        opts.addArguments("--disable-popup-blocking");
        opts.addArguments("--disable-infobars"); // fixed flag name (was '--disable-info bars')
        opts.addArguments("--no-first-run");
        opts.addArguments("--no-default-browser-check");
        opts.addArguments("--password-store=basic");
        opts.addArguments("--incognito");

        // Disable features that trigger credential / leak detection modals
        opts.addArguments(
            "--disable-features=PasswordLeakDetection," +
                "PasswordManagerOnboarding,AutofillServerCommunication," +
                "CredentialManager,MediaRouter"
        );

        // Use an isolated, temporary profile so every run is clean
        try {
            Path tmpProfile = Files.createTempDirectory("chrome-profile-");
            opts.addArguments("--user-data-dir=" + tmpProfile.toAbsolutePath());
        } catch (IOException ignored) {
            // If this fails, Chrome will fall back to its default ephemeral profile
        }

        // Preference toggles to suppress Google password manager & notifications
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        // 1=allow, 2=block
        prefs.put("profile.default_content_setting_values.notifications", 2);
        opts.setExperimentalOption("prefs", prefs);

        // Hide "Chrome is being controlled by automated test software" banner
        opts.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        opts.setExperimentalOption("useAutomationExtension", false);

        // Selenium Manager resolves a compatible ChromeDriver automatically
        TL.set(new ChromeDriver(opts));
    }

    /** Returns the thread’s WebDriver (call {@link #init()} first). */
    public static WebDriver get() {
        return TL.get();
    }

    /** Quits and clears the thread’s WebDriver. Safe to call multiple times. */
    public static void quit() {
        WebDriver driver = TL.get();
        if (driver != null) {
            try {
                driver.quit();
            } finally {
                TL.remove();
            }
        }
    }
}
