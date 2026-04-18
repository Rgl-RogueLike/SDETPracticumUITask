package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;
import utils.WaitHelper;

import java.util.ArrayList;
import java.util.List;

public class CategoryPage {

    private WebDriver driver;
    protected WaitHelper waitHelper;

    @FindBy(id = "sort")
    private WebElement sortDropdown;

    private final By productNamesLocator = By.cssSelector(".thumbnails.grid .prdocutname");
    private final By productPricesLocator = By.cssSelector(".thumbnails.grid .oneprice, thumbnails.grid .pricenew");

    public CategoryPage(WebDriver driver) {
        this.driver = driver;
        this.waitHelper = new WaitHelper(driver, 10);
        PageFactory.initElements(driver, this);
        waitHelper.waitForVisibility(sortDropdown);
    }

    public CategoryPage selectSortBy(String optionText) {
        waitHelper.waitForVisibility(sortDropdown);
        Select select = new Select(sortDropdown);
        select.selectByVisibleText(optionText);
        waitHelper.waitForVisibility(productNamesLocator);
        return this;
    }

    public List<String> getProductNames() {
        List<String> names = new ArrayList<>();
        List<WebElement> elements = driver.findElements(productNamesLocator);
        for (WebElement element : elements) {
            names.add(element.getText().trim());
        }
        return names;
    }

    public List<Double> getProductPrices() {
        List<Double> prices = new ArrayList<>();
        List<WebElement> elements = driver.findElements(productPricesLocator);
        for (WebElement element : elements) {
            String text = element.getText().replace("$", "").replace(",", "").trim();
            try {
                prices.add(Double.parseDouble(text));
            } catch (NumberFormatException e) {
                prices.add(0.0);
            }
        }
        return prices;
    }

    public List<String> getAvailableSortOptions() {
        waitHelper.waitForVisibility(sortDropdown);
        Select select = new Select(sortDropdown);
        return select.getOptions().stream()
                .map(WebElement::getText)
                .toList();
    }
}
