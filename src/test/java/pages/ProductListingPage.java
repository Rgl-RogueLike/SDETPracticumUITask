package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public class ProductListingPage extends BasePage {

    @FindBy(id = "sort")
    private WebElement sortDropdown;

    @FindBy(css = ".thumbnails.grid")
    private WebElement productGridContainer;

    @FindBy(css = ".thumbnails.grid .prdocutname")
    private List<WebElement> productLinks;

    //private final By productCardLocator = By.cssSelector(".thumbnails.grid > div");

    public ProductListingPage(WebDriver driver, WebDriverWait waiter) {
        super(driver, waiter);
        PageFactory.initElements(driver, this);
        waiter.until(ExpectedConditions.visibilityOf(productGridContainer));
    }

    public ProductListingPage selectSortBy(String optionText) {
        waiter.until(ExpectedConditions.visibilityOf(sortDropdown));
        Select select = new Select(sortDropdown);
        select.selectByVisibleText(optionText);
        waiter.until(ExpectedConditions.visibilityOf(productGridContainer));
        return this;
    }

    public ProductPage navigateToProductByIndex(int index) {
        if (index < 0 || index >= productLinks.size()) {
            throw new IllegalArgumentException("Товар с индексом " + index + " не найден");
        }
        productLinks.get(index).click();
        return new ProductPage(driver, waiter);
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}
