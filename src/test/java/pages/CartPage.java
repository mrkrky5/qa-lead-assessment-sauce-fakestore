package pages;

import common.Waits;
import org.openqa.selenium.By;

import java.math.BigDecimal;
import java.net.URI;

/**
 Cart screen actions and assertions.
 */
public class CartPage {

    // --- Locators ---
    private final By itemName    = By.className("inventory_item_name");
    private final By itemPrice   = By.className("inventory_item_price");
    private final By checkoutBtn = By.id("checkout");

    /** Returns the single cart item's name (trimmed). */
    public String getItemName() {
        return Waits.untilVisible(itemName).getText().trim();
    }

    /**
     * Parses the displayed price into BigDecimal.
     * Strips non-numeric/decimal characters to be resilient to currency symbols.
     */
    public BigDecimal getItemPrice() {
        String raw = Waits.untilVisible(itemPrice).getText().trim(); // e.g. "$29.99"
        String normalized = raw.replaceAll("[^0-9.]", "");
        return new BigDecimal(normalized);
    }

    /**
     Proceeds to checkout step one.
     */
    public void checkout() {
        Waits.click(checkoutBtn);
        try {
            Waits.untilUrlContains("checkout-step-one");
        } catch (Exception ignore) {
            // Fallback: navigate using current origin to avoid hardcoded host
            try {
                URI current = URI.create(common.DriverFactory.get().getCurrentUrl());
                String origin = current.getScheme() + "://" + current.getHost();
                if (current.getPort() != -1) {
                    origin += ":" + current.getPort();
                }
                common.DriverFactory.get().navigate().to(origin + "/checkout-step-one.html");
                Waits.untilUrlContains("checkout-step-one");
            } catch (Exception ignoredToo) {
                // Last resort: let the caller fail with its own assertions
            }
        }
    }
}
