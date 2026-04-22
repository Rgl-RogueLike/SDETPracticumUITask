package tests;

import helpers.ParameterProvider;
import io.qameta.allure.Attachment;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.WebDriverFactory;

/**
 * Абстрактный базовый класс для всех UI-тестов.
 * Предоставляет общую настройку WebDriver, Chrome браузера и WebDriverWait.
 * Использует ThreadLocal для thread-safe работы в параллельных тестах.
 */
public abstract class BaseTest {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();
    private static final ThreadLocal<WebDriverWait> WAITER = new ThreadLocal<>();

    protected WebDriver driver;
    protected WebDriverWait waiter;

    private boolean isTestFailed = false;

    /**
     * Если тест завершается с исключением (падает), флаг {@code isTestFailed} устанавливается в {@code true}.
     */
    @RegisterExtension
    AfterTestExecutionCallback wathman = context -> {
        if (context.getExecutionException().isPresent()) {
            isTestFailed = true;
        }
    };

    /**
     * Настройка тестового окружения перед каждым тестом.
     */
    @BeforeEach
    public void setUp() {
        WebDriver driver = WebDriverFactory.createDriver(ParameterProvider.get("browser.chrome"));
        int windowWidth = Integer.parseInt(ParameterProvider.get("screen.width.resolution"));
        int windowHeight = Integer.parseInt(ParameterProvider.get("screen.height.resolution"));
        WebDriverFactory.setWindowSize(driver, windowWidth, windowHeight);

        DRIVER.set(driver);
        WAITER.set(WebDriverFactory.createWebDriverWait(driver, ParameterProvider.get("explicit.wait.time")));

        this.driver = DRIVER.get();
        this.waiter = WAITER.get();
        driver.get(ParameterProvider.get("base.url"));
    }

    /**
     * Очистка ресурсов после каждого теста.
     */
    @AfterEach
    public void tearDown() {
        WebDriver drv = DRIVER.get();
        if (drv != null) {
            if (isTestFailed) {
                attachScreenshot();
            }
            drv.quit();
            DRIVER.remove();
            WAITER.remove();
        }
    }

    /**
     * Создает скриншот текущего состояния браузера.
     * Автоматически прикрепляется к Allure отчету при падении теста.
     *
     * @return скриншот в формате PNG байтового массива
     */
    @Attachment(value = "Screenshot on failure", type = "image/png")
    public byte[] attachScreenshot() {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }
}
