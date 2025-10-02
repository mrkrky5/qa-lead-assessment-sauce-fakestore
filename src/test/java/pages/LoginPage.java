package pages;

import common.DriverFactory;
import common.Waits;
import org.openqa.selenium.By;

/**
 Login screen interactions for https://www.saucedemo.com/
 */
public class LoginPage {

    // --- Locators (stable IDs on SauceDemo) ---
    private final By userInput = By.id("user-name");
    private final By passInput = By.id("password");
    private final By loginBtn  = By.id("login-button");

    /**
     Navigates to the application and waits until the username field is ready.
     */
    public void open(String baseUrl) {
        DriverFactory.get().get(baseUrl);
        Waits.untilVisible(userInput); // page readiness
    }

    /**
     Types credentials and signs in. Uses explicit waits; no Thread.sleep.
     */
    public void login(String username, String password) {
        Waits.untilVisible(userInput).clear();
        Waits.untilVisible(userInput).sendKeys(username);

        Waits.untilVisible(passInput).clear();
        Waits.untilVisible(passInput).sendKeys(password);

        // Centralized click handles scroll/JS fallback when needed
        Waits.click(loginBtn);
    }
}
