package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Страница конкретного товара (Product Page).
 * Содержит элементы для установки количества товара и добавления в корзину.
 */
public class ProductPage extends BasePage {

    @FindBy(css = "input[name='quantity']")
    private WebElement quantityInput;

    @FindBy(css = ".cart")
    private WebElement addToCartBtn;

    @FindBy(css = ".cart_total")
    private WebElement cartTotalElement;

    /**
     * Конструктор страницы товара.
     *
     * @param driver WebDriver экземпляр для работы с браузером
     * @param waiter WebDriverWait для явных ожиданий
     */
    public ProductPage(WebDriver driver, WebDriverWait waiter) {
        super(driver, waiter);
        PageFactory.initElements(driver, this);
    }


    /**
     * Устанавливает количество товара в поле ввода.
     * Очищает поле перед вводом нового значения.
     *
     * @param quantity количество товара
     * @return текущий экземпляр {@link ProductPage} для цепочки вызовов
     */
    @Step("Set quantity: {quantity}")
    public ProductPage setQuantity(int quantity) {
        waiter.until(ExpectedConditions.visibilityOf(quantityInput));
        quantityInput.clear();
        quantityInput.sendKeys(String.valueOf(quantity));
        return this;
    }

    /**
     * Добавляет товар в корзину, нажимая соответствующую кнопку.
     * Ждет кликабельности кнопки перед выполнением действия.
     *
     * @return новый экземпляр {@link CartPage}
     */
    @Step("Add to cart")
    public CartPage addToCart() {
        waiter.until(ExpectedConditions.elementToBeClickable(addToCartBtn)).click();
        return new CartPage(driver, waiter);
    }
}
