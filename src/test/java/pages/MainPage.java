package pages;

import io.qameta.allure.Step;
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
            String text = link.getText().trim();
            if (!text.equals("Cart") &&
                !text.equals("Checkout") &&
                !text.equals("Login or register"))
            {
                categories.add(text);
            }
        }
        return categories;
    }

    @Step("Navigate to category: {categoryName}")
    public CategoryPage navigateToCategory(String categoryName) {
        By linkLocator = By.linkText(categoryName);
        WebElement categoryLink = waiter.until(ExpectedConditions.elementToBeClickable(linkLocator));
        categoryLink.click();
        return new CategoryPage(driver, waiter);
    }

    @Step("Search for product: {text}")
    public ProductListingPage searchFor(String text) {
        waiter.until(ExpectedConditions.visibilityOf(searchInput));
        searchInput.clear();
        searchInput.sendKeys(text + Keys.ENTER);
        return new ProductListingPage(driver, waiter);
    }

    @Step("Navigate to random product from main page")
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