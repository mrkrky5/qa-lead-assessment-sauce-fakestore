QA LEAD ASSESSMENT – SAUCE + FAKESTORE (API & UI)
====================================================

OVERVIEW
--------
This project has a lean, production-quality test suite that:
- Fetches product details from FakeStore API and verifies the payload.
- Uses API details (price and title) to verify the SauceDemo web UI cart.
- Relies on Page Object Model, explicit waits, and clear assertions.

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
│  └─ common/           -> Config, DriverFactory, Waits, ScenarioContext
   │  └─ pages/            -> Page Objects (Login, Products, Cart, Checkout, Done)
│  ├─ steps/            -> Cucumber step definitions and Hooks
   │  └─ runners/          -> CucumberTestRunner (JUnit Platform)
   └─ resources
      ├─ features/         -> ui_e2e.feature, api_data.feature
└─ config.properties -> default configuration

CONFIGURATION
-------------
File: src/test/resources/config.properties

web.baseUrl=https://www.saucedemo.com
api.baseUrl=https://fakestoreapi.com
headless=false
default.timeout.seconds=25

You can override any property at runtime by -D, i.e.:
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
- Verifies cart name and price against API values
- Does checkout and verifies "Thank you for your order!"

Note:
FakeStore product name and SauceDemo catalog are separate systems. The suite can have strict or loose verification using properties. Avoid name assertion for demo purposes:
- run -Dassert.name=false
To enforce strict match, keep default settings.

DESIGN NOTES
------------  
PAGE OBJECT MODEL
- Each page presents a readiness assertion (e.g., assertLoaded) and business actions
- No test logic in page classes

EXPLICIT WAITS
- Distributed in common.Waits with WebDriverWait and ExpectedConditions
- No use of Thread.sleep

WEBDRIVER LIFECYCLE
- steps.Hooks starts and stops the driver per test scenario
- DriverFactory uses ThreadLocal<WebDriver> and consistent Chrome parameters:
  headless toggle, disabled password/info popups, temporary user-data-dir

API AND UI INTEGRATION
- ScenarioContext remembers API title and price so UI steps can check against them
- Clear failure messages in assertions

USEFUL COMMANDS
---------------
Run UI headless by tag:
mvn test -DskipTests=false -Dcucumber.filter.tags="@ui" -Dheadless=true

Increase default timeout:
mvn test -Ddefault.timeout.seconds=40

TROUBLESHOOTING
---------------
- Chrome DevTools warnings (CDP version) are not a problem; tests run
- Ensure config.properties is in src/test/resources
- If local Chrome popups appear during non-headless runs, make sure no external extensions get in the way

LICENSE
-------
Internal assessment project.

## Part 2, Mobile Automation Strategy

### 1) Framework & tool selection
**Recommendation:** Appium (Java + JUnit 5) on BrowserStack App Automate.

**Why this rather than Espresso/XCUITest:**
- One codebase for **Android + iOS**. Espresso is fine for Android, and XCUITest for iOS. Hence, you need to keep two stacks.
- Common flows, like login, search, and checkout, and common assertions. Any platform-specific differences may be handled with minimal branching for capabilities and locators.
- Solid integrations: Selenium 4 APIs, Allure, Cucumber (optional), parallel execution on real devices, and easy CI setup.
- If we need quick, local device smoke checks in the future, we may add a thin Espresso/XCUITest layer without sacrificing Appium end-to-end.

### 2) High-level test plan (first wave)
1. **Authentication**
- Happy path and negative logout/login flow.
- Biometrics, either on emulator/simulator or BrowserStack profiles: allow/deny flows.
2. **Product discovery**
   - Auto-suggest search.
   - Combinations of Filter and Sort with result consistency checks.
3. **Cart & checkout**
   - Add/remove items, price totals, and coupon/discount application.
   - Address and payment via mock gateway, and order confirmation screen.
4. **State & connectivity**
   - Maintain cart state during **offline to online** transitions.
   - Resume the app in background with session alive.
5. **Notifications / deep links**
   - Permission flow.
   - Show a promo push notification, referencing the correct product detail page or product list page.

### 3) Test architecture (project structure)
- **Runner:** JUnit 5, using Cucumber as optional if behavior-driven development is to be used.
- **Layers:**
  - `drivers/` — Driver factory (BrowserStack capabilities, environment overrides).
  - `screens/` — Screen Objects, organized by screen, preferring accessibility/test IDs.
  - `flows/` — Declarative business flows like login and purchase.
  - `data/` — Test data and environment configuration in JSON/YAML format.
  - `utils/` — Waits, gestures, network toggles, and attachments.
- **Config:** all important parameters may be overridden via system properties
  `-Dbs.project -Dbs.build -Dbs.session -Dbs.device -Dbs.os -Dbs.app`
- **Reporting:** Allure with screenshots, console logs, and BrowserStack session/video links.

### 4) Mobile-specific issues and strategy
**Multiple screens/orientation**
- Use accessibility IDs or test IDs as primary locators. Use only `iOSClassChain` or `AndroidUIAutomator` as a fallback where necessary.
- Use gestures and scrolling in **relative** terms as a percentage of the container size.
- Wait for a stable state, e.g., visibility and non-stale content, before asserting after rotation.

**Gestures (swipe, pinch, zoom)**
- Use a central helper with short methods:
  `swipeUp/Down/Left/Right(fromPct, toPct, durationMs)` and `pinch/zoom(element)`.
- Wait for a expected state following a gesture, i.e., a new cell to become aquirable, instead of sleeping.

**Flaky tests (network, notifications)**
- Switch online and offline using network profiles, and **retry with backoff** for operations network-sensitive.
- Utilize auto-grant profiles or the first-run step for permission prompts at launch.
- Do not use `Thread.sleep`; use explicit waits with brief polling and suitable timeouts.
- Ensure tests are idempotent by flushing cart or account state via API helpers where possible.

### 5) CI/CD and device cloud
- **BrowserStack App Automate** for parallelization and real device testing.
- Pipeline layout:
  1) App artifacts (.apk/.aab/.ipa) build.
  2) Upload to BrowserStack (`/app-automate/upload`) to get `bs://…`.
  3) Run Appium tests with `-Dbs.app=bs://…` and desired device/OS configuration.
4) Publish the Allure report and approve the build using BrowserStack REST.
- For pull requests, smoke test a few devices. For `main`, test a bigger regression matrix.

### 6) Scaling and maintenance
- Use a quarantine tag for flaky tests and run them separately until they are resolved.
- Regularly review health of locators and device/OS portfolio by usage metrics.
- Keep the flows small and modular with a single focus per test as much as possible.
