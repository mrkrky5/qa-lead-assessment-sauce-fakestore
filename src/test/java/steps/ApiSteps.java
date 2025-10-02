package steps;

import common.Config;
import common.ScenarioContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.restassured.response.Response;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 API step definitions for retrieving product data from FakeStore API.
 */
public class ApiSteps {

    private Response response;

    @Given("I retrieve product {int} from the API")
    public void getProduct(int id) {
        // Hit GET /products/{id} and do basic inline verifications
        response = given()
            .baseUri(Config.apiBaseUrl())
            .basePath("/products")
            .pathParam("id", id)
            .when()
            .get("/{id}")
            .then()
            // Basic happy-path checks here
            .statusCode(200)
            .body("id", equalTo(id))
            .extract()
            .response();

        // Optional: attach raw JSON to the report (safe if Allure not present)
        ScenarioContext.attachText("API response body", response.asPrettyString());
    }

    @And("the API response has status {int} and id {int}")
    public void assertId(Integer status, Integer id) {
        // This repeats the core checks explicitly (as per the task definition).
        // It’s fine even if the Given already validated them.
        assertNotNull(response, "API response is null. Did the GET step run?");
        response.then()
            .statusCode(status)
            .body("id", equalTo(id));
    }

    @And("I store the API product title and price")
    public void store() {
        assertNotNull(response, "API response is null. Cannot extract title/price.");

        // Extract fields in a type-safe manner
        String title = response.jsonPath().getString("title");
        BigDecimal price = new BigDecimal(response.jsonPath().getString("price"));

        assertNotNull(title, "API title is null!");
        assertNotNull(price, "API price is null!");

        // Store in scenario-scoped context for UI assertions
        ScenarioContext.setApiTitle(title);
        ScenarioContext.setApiPrice(price);

        // Small breadcrumb in the report
        ScenarioContext.attachText("Stored product", "title=" + title + ", price=" + price);
    }
}
