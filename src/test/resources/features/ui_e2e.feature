@ui
Feature: Web UI Test - End-to-End Flow

  Background:
    Given I retrieve product 1 from the API
    And I store the API product title and price

  Scenario: Checkout with API-driven product validation
    When I login to SauceDemo with "standard_user" and "secret_sauce"
    Then I should be on the Products page
    When I add "Sauce Labs Backpack" to the cart
    And I navigate to the cart
    Then the cart item name matches the API title
    And the cart item price matches the API price
    When I checkout with dummy info
    Then I should see "Thank you for your order!"
