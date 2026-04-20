package tests;

import helpers.ParameterProvider;
import io.qameta.allure.Attachment;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public abstract class BaseTest {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();
    private static final ThreadLocal<WebDriverWait> WAITER = new ThreadLocal<>();

    protected WebDriver driver;
    protected WebDriverWait waiter;

    @BeforeEach
    public void setUp() {
        DRIVER.set(new ChromeDriver());
        WAITER.set(new WebDriverWait(DRIVER.get(),
                Duration.ofSeconds(Long.parseLong(ParameterProvider.get("explicit.wait.time")))));
        driver = DRIVER.get();
        waiter = WAITER.get();
        driver.get(ParameterProvider.get("base.url"));
    }

    @AfterEach
    public void tearDown() {
        WebDriver drv = DRIVER.get();
        if (drv != null) {
            attachScreenshot();
            drv.quit();
            DRIVER.remove();
            WAITER.remove();
        }
    }

    @Attachment(value = "Screenshot on failure", type = "image/png")
    public byte[] attachScreenshot() {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }
}
