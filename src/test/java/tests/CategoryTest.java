package tests;

import helpers.ParameterProvider;
import io.qameta.allure.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pages.CategoryPage;
import pages.MainPage;

import java.util.List;

/**
 * Тест функции сортировки товаров в категориях каталога.
 * Проверяет корректность сортировки по имени и цене (по возрастанию/убыванию).
 */
@Epic("Store navigation")
@Feature("Product Catalog")
public class CategoryTest extends BaseTest {

    /**
     * Тест проверки работоспособности фильтрации товаров в категории.
     */
    @Test
    @Story("Verify catalog sorting functionality")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Проверка сортировки товаров в категории")
    public void testCategorySorting() {
        MainPage mainPage = new MainPage(driver, waiter);
        List<String> categories = mainPage.getAvailableCategories();
        categories.removeIf(name -> name.equalsIgnoreCase("HOME"));

        int minProducts = Integer.parseInt(ParameterProvider.get("min.products.count"));

        CategoryPage categoryPage = CategoryPage.findCategoryWithMinProducts(mainPage, categories, minProducts, driver, waiter);

        Assertions.assertTrue(CategoryPage.checkSorting(categoryPage, ParameterProvider.get("sort.products.name.a.z"), true),
                "Имена товаров не отсортированы по возрастанию");

        Assertions.assertTrue(CategoryPage.checkSorting(categoryPage, ParameterProvider.get("sort.products.name.z.a"), false),
                "Имена товаров не отсортированы по убыванию");

        Assertions.assertTrue(CategoryPage.checkSorting(categoryPage, ParameterProvider.get("sort.products.price.low.high"), true),
                "Цены товаров не отсортированы по возрастанию");

        Assertions.assertTrue(CategoryPage.checkSorting(categoryPage, ParameterProvider.get("sort.products.price.high.low"), false),
                "Цены товаров не отсортированы по убыванию");

    }
}