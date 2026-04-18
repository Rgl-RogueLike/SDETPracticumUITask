package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import utils.WaitHelper;

import java.util.ArrayList;
import java.util.List;

public class MainPage {

    private WebDriver driver;
    protected WaitHelper waitHelper;

    @FindBy(id = "categorymenu")
    private WebElement categoryMenu;

    public MainPage(WebDriver driver) {
        this.driver = driver;
        this.waitHelper = new WaitHelper(driver, 10);
        PageFactory.initElements(driver, this);
    }


    public List<String> getAvailableCategories() {
        waitHelper.waitForVisibility(categoryMenu);

        List<WebElement> categoryLinks = driver.findElements(
                By.xpath("//section[@id='categorymenu']//ul[contains(@class, 'nav-pills categorymenu')]/li/a")
        );

        List<String> categories = new ArrayList<>();
        for (WebElement link : categoryLinks) {
            categories.add(link.getText().trim());
        }
        return categories;
    }

    public CategoryPage navigateToCategory(String categoryName) {
        By linkLocator = By.linkText(categoryName);
        WebElement categoryLink = waitHelper.waitForClickable(linkLocator);
        categoryLink.click();
        return new CategoryPage(driver);
    }
}