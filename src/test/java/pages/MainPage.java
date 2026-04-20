package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainPage extends BasePage {

    @FindBy(id = "categorymenu")
    private WebElement categoryMenu;

    @FindBy(id = "filter_keyword")
    private WebElement searchInput;

    @FindBy(xpath = "//section[@id='categorymenu']//ul[contains(@class, 'nav-pills categorymenu')]/li/a")
    private List<WebElement> categoryLinks;

    @FindBy(css = ".prdocutname")
    private List<WebElement> productLinks;

    public MainPage(WebDriver driver, WebDriverWait waiter) {
        super(driver, waiter);
        PageFactory.initElements(driver, this);
        waiter.until(ExpectedConditions.visibilityOf(categoryMenu));
    }


    public List<String> getAvailableCategories() {
        List<String> categories = new ArrayList<>();
        for (WebElement link : categoryLinks) {
            categories.add(link.getText().trim());
        }
        return categories;
    }

    public CategoryPage navigateToCategory(String categoryName) {
        By linkLocator = By.linkText(categoryName);
        WebElement categoryLink = waiter.until(ExpectedConditions.elementToBeClickable(linkLocator));
        categoryLink.click();
        return new CategoryPage(driver, waiter);
    }

    public ProductListingPage searchFor(String text) {
        waiter.until(ExpectedConditions.visibilityOf(searchInput));
        searchInput.clear();
        searchInput.sendKeys(text + Keys.ENTER);
        return new ProductListingPage(driver, waiter);
    }

    public void waitForProductsToLoad() {
        waiter.until(ExpectedConditions.numberOfElementsToBeMoreThan(
                By.cssSelector(".prdocutname"), 0
        ));
    }

    public ProductPage navigateToRandomProduct() {
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".prdocutname")));
        if (productLinks.isEmpty()) {
            throw new RuntimeException("На главной странице нет товаров");
        }
        Random random = new Random();
        int randomIndex = random.nextInt(productLinks.size());
        productLinks.get(randomIndex).click();
        return new ProductPage(driver, waiter);
    }
}