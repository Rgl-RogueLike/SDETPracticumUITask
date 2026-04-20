package tests;

import helpers.ParameterProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public abstract class BaseTest {

    static WebDriver driver;
    static WebDriverWait waiter;

    @BeforeEach
    public void setUp() {
        driver = new ChromeDriver();
        waiter = new WebDriverWait(driver, Duration.ofSeconds(Long.parseLong(ParameterProvider.get("explicit.wait.time"))));
        driver.get(ParameterProvider.get("base.url"));
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}
