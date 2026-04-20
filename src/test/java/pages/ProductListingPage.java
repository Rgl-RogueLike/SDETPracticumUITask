package pages;

import io.qameta.allure.Step;
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

    public ProductListingPage(WebDriver driver, WebDriverWait waiter) {
        super(driver, waiter);
        PageFactory.initElements(driver, this);
        waiter.until(ExpectedConditions.visibilityOf(productGridContainer));
    }

    @Step("Sort products by: {optionText}")
    public ProductListingPage selectSortBy(String optionText) {
        waiter.until(ExpectedConditions.visibilityOf(sortDropdown));
        Select select = new Select(sortDropdown);
        select.selectByVisibleText(optionText);
        waiter.until(ExpectedConditions.visibilityOf(productGridContainer));
        return this;
    }

    @Step("Go to product by index: {index}")
    public ProductPage navigateToProductByIndex(int index) {
        if (index < 0 || index >= productLinks.size()) {
            throw new IllegalArgumentException("Товар с индексом " + index + " не найден");
        }
        productLinks.get(index).click();
        return new ProductPage(driver, waiter);
    }
}
