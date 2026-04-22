package pages;

import helpers.ParameterProvider;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

    /**
     * Находит категорию с достаточным количеством товаров для тестирования.
     *
     * @param mainPage начальная страница
     * @param categories список доступных категорий
     * @param minProducts минимальное количество товаров в категории
     * @return страница категории с достаточным количеством товаров
     */
    @Step("Find category with at least {minProducts} products")
    public static CategoryPage findCategoryWithMinProducts(MainPage mainPage, List<String> categories, int minProducts, WebDriver driver, WebDriverWait waiter) {
        Collections.shuffle(categories);
        for (String categoryName : categories) {
            CategoryPage categoryPage = mainPage.navigateToCategory(categoryName);
            int count = categoryPage.getProductNames().size();
            if (count >= minProducts) {
                return categoryPage;
            } else {
                driver.get(ParameterProvider.get("base.url"));
                mainPage = new MainPage(driver, waiter);
            }
        }
        Assertions.fail("Не найдено ни одной категории с количеством товаров >= " + minProducts);
        return null;
    }

    /**
     * Проверяет корректность сортировки товаров в категории.
     *
     * @param categoryPage страница категории
     * @param sortType тип сортировки
     * @param isAscending true для сортировки по возрастанию, false для убывания
     */
    @Step("Check {sortType} sorting. Ascending: {isAscending}")
    public static boolean checkSorting(CategoryPage categoryPage, String sortType, boolean isAscending) {
        categoryPage.selectSortBy(sortType);

        if (sortType.startsWith("Name")) {
            List<String> items = categoryPage.getProductNames();
            return verifyListIsSorted(items, isAscending, "Имена товаров");
        } else {
            List<Double> prices = categoryPage.getProductPrices();
            return verifyListIsSorted(prices, isAscending, "Цены товаров");
        }

    }

    /**
     * Проверяет корректность сортировки списка элементов.
     *
     * @param <T> тип элементов списка (должен реализовывать Comparable)
     * @param list исходный список
     * @param isAscending true для проверки по возрастанию, false для убывания
     * @param messagePrefix префикс сообщения об ошибке
     */
    @Step("Verify list is sorted by {isAscending}")
    public static <T extends Comparable> boolean verifyListIsSorted(List list, boolean isAscending, String messagePrefix) {
        List sortedList;
        if (isAscending) {
            sortedList = list.stream().sorted().toList();
        } else {
            sortedList = list.stream().sorted(Comparator.reverseOrder()).toList();
        }
        return sortedList.equals(list);
    }
}
