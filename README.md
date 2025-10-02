QA Lead Assessment – Sauce + FakeStore
What it does

API: GET https://fakestoreapi.com/products/1, extract title and price, assert status 200 and id 1

UI on SauceDemo: log in, add item to cart, compare cart name and price to API data, complete checkout, verify “Thank you for your order!”

Tech

Java 17 · Maven · JUnit 5 · Cucumber · REST Assured · Selenium Chrome

Structure
src/test
 ├─ java
 │   ├─ common
 │   ├─ pages
 │   ├─ steps
 │   └─ runners
 └─ resources
     ├─ features
     └─ config.properties

Setup

src/test/resources/config.properties

web.baseUrl=https://www.saucedemo.com
api.baseUrl=https://fakestoreapi.com
headless=false
default.timeout.seconds=25

Run

All tests

mvn clean test -DskipTests=false


By tag

mvn test -DskipTests=false -Dcucumber.filter.tags="@api"
mvn test -DskipTests=false -Dcucumber.filter.tags="@ui"


Headless

mvn test -DskipTests=false -Dheadless=true

Evaluation checklist

Page Object Model

Clean, readable, maintainable code

Explicit waits and stable locators, no Thread.sleep

API to UI data integration through shared context

Clear assertions with helpful messages

Troubleshooting

Ensure config.properties is under src/test/resources

Chrome DevTools warnings do not block execution
