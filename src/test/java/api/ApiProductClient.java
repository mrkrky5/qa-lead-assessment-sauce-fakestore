package api;

import api.models.Product;
import common.Config;
import io.restassured.RestAssured;

public class ApiProductClient {
    private final String base;

    public ApiProductClient() {
        this.base = Config.getBoolean("stub.enabled") ? Config.get("stub.baseUrl") : Config.get("api.baseUrl");
    }

    public Product getProductById(int id) {
        return RestAssured.given().baseUri(base).basePath("/products/" + id)
            .when().get()
            .then().statusCode(200)
            .extract().as(Product.class);
    }
}
