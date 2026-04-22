package utils;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;


/**
 * Фабрика для создания WebDriver, WebDriverWait и настройки размера окна браузера.
 */
public class WebDriverFactory {

    private WebDriverFactory() {}

    /**
     * Создаёт экземпляр WebDriver указанного типа браузера.
     *
     * @param browserType строка с названием браузера
     * @return новый экземпляр WebDriver
     * @throws UnsupportedOperationException если браузер не поддерживается
     */
    public static WebDriver createDriver(String browserType) {
        return switch (browserType.toLowerCase()) {
            case "chrome" -> createChromeDriver();
            default -> throw new UnsupportedOperationException("Browser \"" + browserType + "\" not supported");
        };
    }

    /**
     * Создаёт экземпляр ChromeDriver с предустановленными опциями.
     *
     * @return новый экземпляр ChromeDriver
     */
    private static WebDriver createChromeDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-blink-features=AutomationControlled");
        if ("true".equals(System.getenv("HEADLESS"))) {
            options.addArguments("--headless=new");
        }
        return new ChromeDriver(options);
    }

    /**
     * Создаёт экземпляр WebDriverWait с заданным временем ожидания.
     *
     * @param driver WebDriver, для которого создаётся ожидание
     * @param explicitWaitSeconds строка, содержащая секунды ожидания
     * @return новый экземпляр WebDriverWait
     */
    public static WebDriverWait createWebDriverWait(WebDriver driver, String explicitWaitSeconds) {
        long seconds = Long.parseLong(explicitWaitSeconds);
        return new WebDriverWait(driver, Duration.ofSeconds(seconds));
    }

    /**
     * Устанавливает размер окна браузера.
     *
     * @param driver WebDriver, окно которого нужно изменить
     * @param width ширина окна в пикселях
     * @param height высота окна в пикселях
     */
    public static void setWindowSize(WebDriver driver, int width, int height) {
        driver.manage().window().setSize(new Dimension(width, height));
    }
}
