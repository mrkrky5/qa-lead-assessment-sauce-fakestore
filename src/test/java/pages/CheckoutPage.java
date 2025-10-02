package pages;

import common.Waits;
import org.openqa.selenium.By;

/**
 Checkout flow (step one -> step two -> finish).
 */
public class CheckoutPage {

    // --- Locators ---
    private final By firstName   = By.id("first-name");
    private final By lastName    = By.id("last-name");
    private final By postalCode  = By.id("postal-code");
    private final By continueBtn = By.id("continue");
    private final By finishBtn   = By.id("finish");

    /** Verifies we are on checkout step one by waiting for the first-name field. */
    public void assertOnStepOne() {
        Waits.untilVisible(firstName);
    }

    /**
     Fills step one form and continues to step two.
     */
    public void fillAndContinue(String f, String l, String z) {
        var first = Waits.untilVisible(firstName);
        first.clear();
        first.sendKeys(f);

        var last = Waits.untilVisible(lastName);
        last.clear();
        last.sendKeys(l);

        var zip = Waits.untilVisible(postalCode);
        zip.clear();
        zip.sendKeys(z);

        Waits.click(continueBtn);
        // Ensure we actually navigated to step two
        Waits.untilUrlContains("checkout-step-two");
    }

    /** Clicks Finish on step two and waits for completion page. */
    public void finish() {
        Waits.click(finishBtn);
        Waits.untilUrlContains("checkout-complete");
    }
}
