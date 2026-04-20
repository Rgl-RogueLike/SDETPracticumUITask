package tests;

import helpers.ParameterProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pages.CategoryPage;
import pages.MainPage;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CategoryTest extends BaseTest {

    @Test
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