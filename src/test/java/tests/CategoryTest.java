package tests;

import base.BaseTest;
import constants.AppConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pages.CategoryPage;
import pages.MainPage;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CategoryTest extends BaseTest {

    @Test
    public void testCategorySorting() {
        driver.get(AppConstants.BASE_URL);
        MainPage mainPage = new MainPage(driver);
        List<String> categories = mainPage.getAvailableCategories();
        categories.removeIf(name -> name.equalsIgnoreCase("HOME"));

        CategoryPage categoryPage = findCategoryWithMinProducts(mainPage, categories, AppConstants.MIN_PRODUCTS_COUNT);

        checkSorting(categoryPage, AppConstants.SORT_NAME_A_Z);
        checkSorting(categoryPage, AppConstants.SORT_NAME_Z_A);
        checkSorting(categoryPage, AppConstants.SORT_PRICE_LOW_HIGH);
        checkSorting(categoryPage, AppConstants.SORT_PRICE_HIGH_LOW);
    }

    private CategoryPage findCategoryWithMinProducts(MainPage mainPage, List<String> categories, int minProducts) {
        Collections.shuffle(categories);
        for (String categoryName : categories) {
            CategoryPage categoryPage = mainPage.navigateToCategory(categoryName);
            int count = categoryPage.getProductNames().size();
            if (count >= minProducts) {
                return categoryPage;
            } else {
                driver.navigate().to(AppConstants.BASE_URL);
                mainPage = new MainPage(driver);
            }
        }
        Assertions.fail("Не найдено ни одной категории с количеством товаров >= " + minProducts);
        return null;
    }

    private void checkSorting(CategoryPage categoryPage, String sortType) {
        boolean isAscending = false;
        if (sortType.equals(AppConstants.SORT_NAME_A_Z) || sortType.equals(AppConstants.SORT_PRICE_LOW_HIGH)) {
            isAscending = true;
        } else if (sortType.equals(AppConstants.SORT_NAME_Z_A) || sortType.equals(AppConstants.SORT_PRICE_HIGH_LOW)) {
            isAscending = false;
        }
        categoryPage.selectSortBy(sortType);

        if (sortType.startsWith("Name")) {
            List<String> items = categoryPage.getProductNames();
            verifyListIsSorted(items, isAscending);
        } else {
            List<Double> prices = categoryPage.getProductPrices();
            verifyListIsSorted(prices, isAscending);
        }

    }

    private <T extends Comparable> void verifyListIsSorted(List list, boolean isAscending) {
        List sortedList;
        if (isAscending) {
            sortedList = list.stream().sorted().toList();
            Assertions.assertEquals(sortedList, list, "Список не отсортирован по возрастанию");
        } else {
            sortedList = list.stream().sorted(Comparator.reverseOrder()).toList();
            Assertions.assertEquals(sortedList, list, "Список не отсортирован по убыванию");
        }
    }
}