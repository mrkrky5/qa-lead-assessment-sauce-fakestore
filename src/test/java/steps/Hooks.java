package steps;

import common.DriverFactory;
import io.cucumber.java.After;
import io.cucumber.java.Before;

public class Hooks {

    // Run WebDriver only for UI-tagged scenarios.
    @Before("@ui")
    public void setUpUi() {
        DriverFactory.init();
    }

    @After("@ui")
    public void tearDownUi() {
        DriverFactory.quit();
    }
}
