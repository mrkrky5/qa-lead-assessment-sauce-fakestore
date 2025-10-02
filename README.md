QA LEAD ASSESSMENT – SAUCE + FAKESTORE (API & UI)
=================================================

OVERVIEW
--------
This repository provides a compact, production-style test suite that:
- Fetches product data from FakeStore API and validates the payload.
- Uses API data (title and price) to verify the SauceDemo web UI cart.
- Applies Page Object Model, explicit waits, and clear assertions.

TECH STACK
----------
- Java 17
- Maven
- JUnit 5
- Cucumber JVM
- REST Assured
- Selenium WebDriver (Chrome)

PROJECT STRUCTURE
-----------------
src
└─ test
   ├─ java
   │  ├─ common/           -> Config, DriverFactory, Waits, ScenarioContext
   │  ├─ pages/            -> Page Objects (Login, Products, Cart, Checkout, Done)
   │  ├─ steps/            -> Cucumber step definitions and Hooks
   │  └─ runners/          -> CucumberTestRunner (JUnit Platform)
   └─ resources
      ├─ features/         -> api_data.feature, ui_e2e.feature
      └─ config.properties -> default configuration

CONFIGURATION
-------------
File: src/test/resources/config.properties

web.baseUrl=https://www.saucedemo.com
api.baseUrl=https://fakestoreapi.com
headless=false
default.timeout.seconds=25

You can override any property at runtime via -D, for example:
mvn clean test -DskipTests=false -Dheadless=true -Ddefault.timeout.seconds=30

HOW TO RUN
----------
1) All tests
   mvn clean test -DskipTests=false

2) By tag
   API only:
   mvn test -DskipTests=false -Dcucumber.filter.tags="@api"

   UI only:
   mvn test -DskipTests=false -Dcucumber.filter.tags="@ui"

3) Headless mode
   mvn test -DskipTests=false -Dheadless=true

SCENARIOS
---------
1) API TEST – DATA RETRIEVAL (@api)
   - Sends GET https://fakestoreapi.com/products/1
   - Asserts status 200 and id = 1
   - Extracts and stores title and price for later UI validation

2) WEB UI TEST – END-TO-END FLOW (@ui)
   - Opens SauceDemo and logs in with standard credentials
   - Adds “Sauce Labs Backpack” to the cart
   - Asserts cart name and price against the API values
   - Completes checkout and verifies "Thank you for your order!"

Note:
The FakeStore product title and SauceDemo catalog are different systems. The suite allows strict or flexible verification using properties. To skip the name assertion for demo purposes:
- set -Dassert.name=false
To enforce strict matching, keep default settings.

DESIGN NOTES
------------
PAGE OBJECT MODEL
- Each page exposes a readiness assertion (for example, assertLoaded) and business actions
- No test logic inside page classes

EXPLICIT WAITS
- Centralized in common.Waits using WebDriverWait and ExpectedConditions
- No Thread.sleep usage

WEBDRIVER LIFECYCLE
- steps.Hooks starts and stops the driver per scenario
- DriverFactory uses ThreadLocal<WebDriver> and stable Chrome options:
  headless toggle, disabled password/info popups, temporary user-data-dir

API AND UI INTEGRATION
- ScenarioContext holds API title and price so UI steps can assert against them
- Assertions include clear failure messages

USEFUL COMMANDS
---------------
Run UI headless by tag:
mvn test -DskipTests=false -Dcucumber.filter.tags="@ui" -Dheadless=true

Increase default timeout:
mvn test -Ddefault.timeout.seconds=40

TROUBLESHOOTING
---------------
- Chrome DevTools warnings (CDP version) are informational; tests still run
- Ensure config.properties is under src/test/resources
- If any local Chrome popups appear in non-headless runs, check that no external extensions interfere

LICENSE
-------
Internal assessment project.
