package steps;

import common.Config;
import common.ScenarioContext;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import pages.CartPage;
import pages.CheckoutCompletePage;
import pages.CheckoutPage;
import pages.LoginPage;
import pages.ProductsPage;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 UI step definitions for the SauceDemo flow.
 */
public class UiSteps {
    private final LoginPage login = new LoginPage();
    private final ProductsPage products = new ProductsPage();
    private final CartPage cart = new CartPage();
    private final CheckoutPage checkout = new CheckoutPage();
    private final CheckoutCompletePage done = new CheckoutCompletePage();

    @When("I login to SauceDemo with {string} and {string}")
    public void loginSauce(String username, String password) {
        // Use the app’s web base URL from config
        login.open(Config.webBaseUrl());
        login.login(username, password);
    }

    @Then("I should be on the Products page")
    public void assertProducts() {
        products.assertLoaded();
    }

    @When("I add {string} to the cart")
    public void add(String name) {
        products.addToCartByName(name);
    }

    @When("I navigate to the cart")
    public void goCart() {
        products.goToCart();
    }

    @Then("the cart item name matches the API title")
    public void assertName() {
        // Read expected product title captured from the API
        String expected = common.ScenarioContext.getApiTitle();

        // Read actual product title from the UI (cart)
        String actual = cart.getItemName();

        // Fail fast if they don't match – we WANT to see the discrepancy
        org.junit.jupiter.api.Assertions.assertEquals(
            expected,
            actual,
            "Cart item name does not match API title."
        );
    }

    @Then("the cart item price matches the API price")
    public void assertPrice() {
        BigDecimal actual = cart.getItemPrice();
        BigDecimal expected = ScenarioContext.getApiPrice();
        // Use compareTo for BigDecimal equality (handles scale differences)
        assertEquals(0, expected.compareTo(actual),
            "UI price " + actual + " != API price " + expected);
    }

    @When("I checkout with dummy info")
    public void doCheckout() {
        cart.checkout();
        checkout.assertOnStepOne();
        checkout.fillAndContinue("John", "Doe", "10001");
        checkout.finish();
    }

    @Then("I should see {string}")
    public void assertDone(String expected) {
        done.assertLoaded();
        String actual = done.getCompleteMsg();
        assertEquals(expected, actual, "Unexpected completion message");
    }
}
