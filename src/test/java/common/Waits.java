package common;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 Small collection of explicit wait helpers.
 */
public class Waits {

    // Utility class — prevent instantiation
    private Waits() { }

    /**
     Waits until the element located by the given locator becomes visible.
     */
    public static WebElement untilVisible(By locator) {
        WebDriverWait wait = new WebDriverWait(
            DriverFactory.get(),
            Duration.ofSeconds(Config.getInt("default.timeout.seconds"))
        );
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /**
     Waits until the element located by the given locator is clickable.
     */
    public static WebElement untilClickable(By locator) {
        WebDriverWait wait = new WebDriverWait(
            DriverFactory.get(),
            Duration.ofSeconds(Config.getInt("default.timeout.seconds"))
        );
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    /**
     Waits until the current URL contains the given substring.
     */
    public static void untilUrlContains(String fraction) {
        WebDriverWait wait = new WebDriverWait(
            DriverFactory.get(),
            Duration.ofSeconds(Config.getInt("default.timeout.seconds"))
        );
        wait.until(ExpectedConditions.urlContains(fraction));
    }

    /**
     Clicks an element located by the given locator.
     */
    public static void click(By locator) {
        WebDriver driver = DriverFactory.get();
        WebElement el = untilClickable(locator);
        try {
            ((JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView({block:'center'});", el);
            el.click();
        } catch (WebDriverException e) {
            // Fallback: JS click (useful when an overlay briefly intercepts the click)
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
        }
    }
}
