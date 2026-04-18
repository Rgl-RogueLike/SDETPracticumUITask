package pages;

import constants.AppConstants;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;
import utils.WaitHelper;

import java.util.List;

public class ProductListingPage {

    private WebDriver driver;
    protected WaitHelper waitHelper;

    @FindBy(id = "sort")
    private WebElement sortDropdown;

    private final By productCardLocator = By.cssSelector(".thumbnails.grid > div");

    public ProductListingPage(WebDriver driver) {
        this.driver = driver;
        this.waitHelper = new WaitHelper(driver, AppConstants.DEFAULT_TIMEOUT_SECONDS);
        PageFactory.initElements(driver, this);
        waitHelper.waitForVisibility(productCardLocator);
    }

    public ProductListingPage selectSortBy(String optionText) {
        waitHelper.waitForVisibility(sortDropdown);
        Select select = new Select(sortDropdown);
        select.selectByVisibleText(optionText);
        waitHelper.waitForVisibility(productCardLocator);
        return this;
    }

    public ProductPage navigateToProductByIndex(int index) {
        List<WebElement> products = driver.findElements(productCardLocator);
        if (index < 0 || index >= products.size()) {
            throw new IllegalArgumentException("Товар с индексом " + index + " не найден. Всего товаров: " + products.size());
        }

        WebElement card = products.get(index);
        WebElement link = card.findElement(By.tagName("a"));
        String productUrl = link.getAttribute("href");
        driver.get(productUrl);
        return new ProductPage(driver);
    }
}
