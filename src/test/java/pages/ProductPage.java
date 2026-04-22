package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.TestDataUtils;

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

    /**
     * Добавляет товар в корзину и возвращает на главную страницу.
     * Используется для добавления первого товара в сценарии.
     *
     * @param listingPage страница списка товаров
     * @param index индекс товара в списке
     * @param qty количество товара (должно быть положительным)
     */
    @Step("Add product at index {index} with quantity {qty} and return to main page")
    public static void addProduct(ProductListingPage listingPage, int index, int qty) {
        ProductPage productPage = listingPage.navigateToProductByIndex(index);
        CartPage cartPage = productPage.setQuantity(qty).addToCart();
        MainPage mainPage = cartPage.goToHomePage();
    }

    /**
     * Добавляет товар в корзину без возврата на главную страницу.
     * Используется для добавления второго товара в сценарии.
     *
     * @param listingPage страница списка товаров
     * @param index индекс товара в списке
     * @param qty количество товара (должно быть положительным)
     */
    @Step("Add product at index {index} with quantity {qty} and stay in cart")
    public static void addProductToCart(ProductListingPage listingPage, int index, int qty) {
        ProductPage productPage = listingPage.navigateToProductByIndex(index);
        productPage.setQuantity(qty).addToCart();
    }

    /**
     * Добавляет указанное количество случайных товаров в корзину.
     * Для каждого товара генерирует случайное количество.
     *
     * @param mainPage начальная страница
     * @param amount количество товаров для добавления
     */
    @Step("Add {amount} random products to cart")
    public static void addRandomProductToCart(MainPage mainPage, int amount) {
        for (int i = 0; i < amount; i++) {
            ProductPage productPage = mainPage.navigateToRandomProduct();
            int qty = TestDataUtils.randomQuantityProduct();
            CartPage cartPage = productPage.setQuantity(qty).addToCart();
            mainPage = cartPage.goToHomePage();
        }
    }
}
