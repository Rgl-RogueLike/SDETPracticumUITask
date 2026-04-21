package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Базовый класс для всех страниц приложения.
 * Предоставляет драйвер и WebDriverWait, которые наследуются всеми остальными страницами.
 */
public abstract class BasePage {

    /**
     * Экземпляр WebDriver для веб-тестирования.
     */
    WebDriver driver;

    /**
     * Экземпляр WebDriverWait для явных ожиданий.
     */
    WebDriverWait waiter;

    /**
     * Инициализирует драйвер и ожидание для страницы.
     *
     * @param driver драйвер браузера.
     * @param waiter экземпляр WebDriverWait.
     */
    public BasePage(WebDriver driver, WebDriverWait waiter) {
        this.driver = driver;
        this.waiter = waiter;
    }
}
