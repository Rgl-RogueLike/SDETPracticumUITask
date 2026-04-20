package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;

public class CategoryPage extends BasePage {

    @FindBy(id = "sort")
    private WebElement sortDropdown;

    @FindBy(css = ".thumbnails.grid a.prdocutname")
    private List<WebElement> productNameElements;

    @FindBy(css = ".thumbnails.grid .oneprice")
    private List<WebElement> productPriceElements;

    public CategoryPage(WebDriver driver, WebDriverWait waiter) {
        super(driver, waiter);
        PageFactory.initElements(driver, this);
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".thumbnails.grid")));
        waiter.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.cssSelector("a.prdocutname"), 0));
        waiter.until(ExpectedConditions.visibilityOf(sortDropdown));
    }

    @Step("Select sort option: {optionText}")
    public CategoryPage selectSortBy(String optionText) {
        waiter.until(ExpectedConditions.visibilityOf(sortDropdown));
        Select select = new Select(sortDropdown);
        select.selectByVisibleText(optionText);
        return this;
    }

    @Step("Get product names list")
    public List<String> getProductNames() {
        List<String> names = new ArrayList<>();
        for (WebElement element : productNameElements) {
            names.add(element.getText().trim());
        }
        return names;
    }

    @Step("Get product prices list")
    public List<Double> getProductPrices() {
        List<Double> prices = new ArrayList<>();
        for (WebElement element : productPriceElements) {
            String text = element.getText().replace("$", "").replace(",", "").trim();
            try {
                prices.add(Double.parseDouble(text));
            } catch (NumberFormatException e) {
                prices.add(0.0);
            }
        }
        return prices;
    }
}
