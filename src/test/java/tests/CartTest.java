package tests;

import helpers.ParameterProvider;
import io.qameta.allure.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pages.CartPage;
import pages.MainPage;
import pages.ProductPage;

import java.util.List;

/**
 * Тесты функциональности корзины покупок.
 * Наследуется от {@link BaseTest} и тестирует сценарии добавления/удаления товаров.
 */
@Epic("Shopping Cart")
@Feature("Cart Management")
public class CartTest extends BaseTest{

    /**
     * Тест добавления заданного количества случайных товаров в корзину
     * и удаления всех четных по порядку товаров из корзины.
     * Проверяет корректность пересчета итоговой суммы.
     */
    @Test
    @Story("Add random products and delete even items")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Проверка добавления 5 случайных товаров и удаления четных позиций в корзине")
    public void testRandomProductDelection() {
        MainPage mainPage = new MainPage(driver, waiter);
        int amountProducts = Integer.parseInt(ParameterProvider.get("amount.products.add.cart"));
        ProductPage.addRandomProductToCart(mainPage, amountProducts);
        driver.get(ParameterProvider.get("cart.url"));
        CartPage cartPage = new CartPage(driver, waiter);
        List<CartPage.CartItem> items = cartPage.getItems();
        List<Integer> indicesToDelete = CartPage.findEvenIndices(items);
        double expectedTotal = CartPage.calculateExpectedTotal(items);
        CartPage.deleteItemsByIndices(cartPage, indicesToDelete);
        Assertions.assertTrue(CartPage.verifyCartTotal(cartPage, expectedTotal), "Итоговая сумма не совпадает после удаления четных товаров");
    }
}
