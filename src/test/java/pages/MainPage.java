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
import utils.TestDataUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Главная страница магазина.
 * Содержит методы для поиска, навигации и выбора категорий.
 */
public class MainPage extends BasePage {

    @FindBy(id = "categorymenu")
    private WebElement categoryMenu;

    @FindBy(id = "filter_keyword")
    private WebElement searchInput;

    @FindBy(xpath = "//section[@id='categorymenu']//ul[contains(@class, 'nav-pills categorymenu')]/li/a")
    private List<WebElement> categoryLinks;

    @FindBy(css = ".prdocutname")
    private List<WebElement> productLinks;

    /**
     * Конструктор главной страницы.
     *
     * @param driver драйвер.
     * @param waiter экземпляр ожиданий.
     */
    public MainPage(WebDriver driver, WebDriverWait waiter) {
        super(driver, waiter);
        PageFactory.initElements(driver, this);
        waiter.until(ExpectedConditions.visibilityOf(categoryMenu));
    }


    /**
     * Получает список доступных категорий меню.
     *
     * @return список названий категорий.
     */
    public List<String> getAvailableCategories() {
        List<String> categories = new ArrayList<>();
        for (WebElement link : categoryLinks) {
            String text = link.getText().trim();
            if (!text.equals("Cart") &&
                    !text.equals("Checkout") &&
                    !text.equals("Login or register") &&
                    !text.equals("Specials") &&
                    !text.equals("Account")) {
                categories.add(text);
            }
        }
        return categories;
    }

    /**
     * Переходит в категорию по названию.
     *
     * @param categoryName имя категории.
     * @return новая страница категории.
     */
    @Step("Navigate to category: {categoryName}")
    public CategoryPage navigateToCategory(String categoryName) {
        By linkLocator = By.linkText(categoryName);
        WebElement categoryLink = waiter.until(ExpectedConditions.elementToBeClickable(linkLocator));
        categoryLink.click();
        return new CategoryPage(driver, waiter);
    }

    /**
     * Ищет товар по названию.
     *
     * @param text поисковый запрос.
     * @return страница результатов поиска.
     */
    @Step("Search for product: {text}")
    public ProductListingPage searchFor(String text) {
        waiter.until(ExpectedConditions.visibilityOf(searchInput));
        searchInput.clear();
        searchInput.sendKeys(text + Keys.ENTER);
        return new ProductListingPage(driver, waiter);
    }

    /**
     * Переходит на страницу случайного товара с главной страницы.
     *
     * @return страница товара.
     */
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