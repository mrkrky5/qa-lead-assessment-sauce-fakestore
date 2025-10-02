@api
Feature: API Test - Data Retrieval

  Scenario: Get product 1 and validate
    Given I retrieve product 1 from the API
    Then the API response has status 200 and id 1
    And I store the API product title and price
