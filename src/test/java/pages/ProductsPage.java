package pages;

import common.Waits;
import org.openqa.selenium.By;

import java.util.Locale;

/**
 Products (inventory) screen actions.
 */
public class ProductsPage {

    // --- Locators ---
    private final By container = By.id("inventory_container");
    private final By cartLink  = By.className("shopping_cart_link");

    /** Verifies the products grid is present (= page loaded). */
    public void assertLoaded() {
        Waits.untilVisible(container);
    }

    /**
     Adds an item to cart by visible name.
     */
    public void addToCartByName(String name) {
        String slug = name.trim()
            .toLowerCase(Locale.ROOT)
            .replaceAll("[^a-z0-9]+", "-"); // normalize spaces/punctuations
        By addBtn = By.cssSelector("[data-test='add-to-cart-" + slug + "']");
        Waits.click(addBtn); // centralized click with JS fallback
    }

    /** Opens the cart from the top-right icon/link. */
    public void goToCart() {
        Waits.click(cartLink);
    }
}
