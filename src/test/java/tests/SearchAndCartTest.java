package tests;

import helpers.ParameterProvider;
import io.qameta.allure.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pages.CartPage;
import pages.MainPage;
import pages.ProductListingPage;
import pages.ProductPage;
import utils.TestDataUtils;

/**
 * Тест проверки сценария:
 * 1. Поиск товара по названию
 * 2. Сортировка результатов поиска
 * 3. Добавление товаров в корзину
 * 4. Изменение количества самого дешевого товара
 * 5. Проверка корректности пересчета итоговой суммы
 */
@Epic("Shopping Cart")
@Feature("Checkout & Totals")
public class SearchAndCartTest extends BaseTest {

    /**
     * Тест проверки добавления товаров в корзину и изменения количества самого дешевого:
     */
    @Test
    @Story("Update cart item quantity and verify calculation")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Проверка поисковой выдачи, добавления товара и изменения количество в корзине")
    public void testSearchAndCartOperations() {
        MainPage mainPage = new MainPage(driver, waiter);
        ProductListingPage listingPage = mainPage.searchFor(ParameterProvider.get("name.product.search"));
        listingPage.selectSortBy(ParameterProvider.get("sort.products.name.a.z"));

        int qty1 = TestDataUtils.randomQuantityProduct();
        int qty2 = TestDataUtils.randomQuantityProduct();
        int indexFirstProduct = Integer.parseInt(ParameterProvider.get("index.product.to.add.first"));
        int indexSecondProduct = Integer.parseInt(ParameterProvider.get("index.product.to.add.second"));

        ProductPage.addProduct(listingPage, indexFirstProduct, qty1);
        listingPage = mainPage.searchFor(ParameterProvider.get("name.product.search"));
        listingPage.selectSortBy(ParameterProvider.get("sort.products.name.a.z"));
        ProductPage.addProductToCart(listingPage, indexSecondProduct, qty2);

        CartPage cartPage = new CartPage(driver, waiter);
        CartPage.CartItem cheapestItem = cartPage.findCheapestItem();
        Assertions.assertNotNull(cheapestItem, "В корзине нет товаров");

        String cheapestItemName = cheapestItem.getName();
        int newQty = cheapestItem.getQuantity() * 2;
        CartPage.updateCartItemQuantity(cheapestItem, driver, waiter);
        Assertions.assertTrue(CartPage.verifyCartTotal(cartPage, cheapestItemName, newQty), "Сумма не совпадает после изменения количества");
    }
}