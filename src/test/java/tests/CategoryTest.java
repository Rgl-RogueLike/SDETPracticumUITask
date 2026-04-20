package tests;

import helpers.ParameterProvider;
import io.qameta.allure.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pages.CategoryPage;
import pages.MainPage;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Epic("Store navigation")
@Feature("Product Catalog")
public class CategoryTest extends BaseTest {

    @Test
    @Story("Verify catalog sorting functionality")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Проверка сортировки товаров в категории")
    public void testCategorySorting() {
        MainPage mainPage = new MainPage(driver, waiter);
        List<String> categories = mainPage.getAvailableCategories();
        categories.removeIf(name -> name.equalsIgnoreCase("HOME"));

        int minProducts = Integer.parseInt(ParameterProvider.get("min.products.count"));

        CategoryPage categoryPage = findCategoryWithMinProducts(mainPage, categories, minProducts);

        checkSorting(categoryPage, ParameterProvider.get("sort.products.name.a.z"), true);
        checkSorting(categoryPage, ParameterProvider.get("sort.products.name.z.a"), false);
        checkSorting(categoryPage, ParameterProvider.get("sort.products.price.low.high"), true);
        checkSorting(categoryPage, ParameterProvider.get("sort.products.price.high.low"), false);
    }

    @Step("Find category with at least {minProducts} products")
    private CategoryPage findCategoryWithMinProducts(MainPage mainPage, List<String> categories, int minProducts) {
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

    @Step("Check {sortType} sorting. Ascending: {isAscending}")
    private void checkSorting(CategoryPage categoryPage, String sortType, boolean isAscending) {
        categoryPage.selectSortBy(sortType);

        if (sortType.startsWith("Name")) {
            List<String> items = categoryPage.getProductNames();
            verifyListIsSorted(items, isAscending, "Имена товаров");
        } else {
            List<Double> prices = categoryPage.getProductPrices();
            verifyListIsSorted(prices, isAscending, "Цены товаров");
        }

    }

    @Step("Verify list is sorted by {isAscending}")
    private <T extends Comparable> void verifyListIsSorted(List list, boolean isAscending, String messagePrefix) {
        List sortedList;
        if (isAscending) {
            sortedList = list.stream().sorted().toList();
            Assertions.assertEquals(sortedList, list, messagePrefix + " не отсортированы по возрастанию");
        } else {
            sortedList = list.stream().sorted(Comparator.reverseOrder()).toList();
            Assertions.assertEquals(sortedList, list, messagePrefix + " не отсортированы по убыванию");
        }
    }
}