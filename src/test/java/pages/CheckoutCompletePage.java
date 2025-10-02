package pages;

import common.Waits;
import org.openqa.selenium.By;

/**
 Checkout completion page.
 */
public class CheckoutCompletePage {

    // --- Locators ---
    private final By completeHeader = By.className("complete-header");

    /** Ensures the completion page is loaded by waiting for the success header. */
    public void assertLoaded() {
        Waits.untilVisible(completeHeader);
    }

    /** Returns the visible completion message text (trimmed). */
    public String getCompleteMsg() {
        return Waits.untilVisible(completeHeader).getText().trim();
    }
}
