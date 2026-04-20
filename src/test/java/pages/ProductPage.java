package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ProductPage extends BasePage {

    @FindBy(css = "input[name='quantity']")
    private WebElement quantityInput;

    @FindBy(css = ".cart")
    private WebElement addToCartBtn;

    @FindBy(css = ".cart_total")
    private WebElement cartTotalElement;

    public ProductPage(WebDriver driver, WebDriverWait waiter) {
        super(driver, waiter);
        PageFactory.initElements(driver, this);
    }

    public ProductPage setQuantity(int quantity) {
        waiter.until(ExpectedConditions.visibilityOf(quantityInput));
        quantityInput.clear();
        quantityInput.sendKeys(String.valueOf(quantity));
        return this;
    }

    public CartPage addToCart() {
        waiter.until(ExpectedConditions.elementToBeClickable(addToCartBtn)).click();
        return new CartPage(driver, waiter);
    }
}
