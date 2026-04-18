package pages;

import constants.AppConstants;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import utils.WaitHelper;

public class ProductPage {

    private WebDriver driver;
    protected WaitHelper waitHelper;

    @FindBy(css = "input[name='quantity']")
    private WebElement quantityInput;

    @FindBy(css = ".cart")
    private WebElement addToCartBtn;

    public ProductPage(WebDriver driver) {
        this.driver = driver;
        this.waitHelper = new WaitHelper(driver, AppConstants.DEFAULT_TIMEOUT_SECONDS);
        PageFactory.initElements(driver, this);
    }

    public ProductPage setQuantity(int quantity) {
        waitHelper.waitForVisibility(quantityInput);
        quantityInput.clear();
        quantityInput.sendKeys(String.valueOf(quantity));
        return this;
    }

    public CartPage addToCart() {
        waitHelper.waitForClickable(addToCartBtn).click();
        return new CartPage(driver);
    }

    public ProductListingPage addToCartAndReturnToListing(String listingUrl) {
        waitHelper.waitForClickable(addToCartBtn).click();
        driver.get(listingUrl); // переходим обратно на список
        return new ProductListingPage(driver);
    }
}
