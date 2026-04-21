package tests;

import helpers.ParameterProvider;
import io.qameta.allure.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pages.CartPage;
import pages.MainPage;
import pages.ProductPage;
import utils.TestDataUtils;

import java.util.ArrayList;
import java.util.Collections;
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
        addRandomProductToCart(mainPage, amountProducts);
        driver.get(ParameterProvider.get("cart.url"));
        CartPage cartPage = new CartPage(driver, waiter);
        List<CartPage.CartItem> items = cartPage.getItems();
        List<Integer> indicesToDelete = findEvenIndices(items);
        double expectedTotal = calculateExpectedTotal(items);
        deleteItemsByIndices(cartPage, indicesToDelete);
        verifyCartTotal(cartPage, expectedTotal);
    }

    /**
     * Добавляет указанное количество случайных товаров в корзину.
     * Для каждого товара генерирует случайное количество.
     *
     * @param mainPage начальная страница
     * @param amount количество товаров для добавления
     */
    @Step("Add {amount} random products to cart")
    private void addRandomProductToCart(MainPage mainPage, int amount) {
        for (int i = 0; i < amount; i++) {
            ProductPage productPage = mainPage.navigateToRandomProduct();
            int qty = TestDataUtils.randomQuantityProduct();
            CartPage cartPage = productPage.setQuantity(qty).addToCart();
            mainPage = cartPage.goToHomePage();
        }
    }

    /**
     * Находит индексы товаров с нечетными позициями (1, 3, 5...).
     * Сортирует в обратном порядке для корректного удаления.
     *
     * @param items список товаров в корзине
     * @return список индексов для удаления (от большего к меньшему)
     */
    @Step("Find indices of even items")
    private List<Integer> findEvenIndices(List<CartPage.CartItem> items) {
        List<Integer> indicesToDelete = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            if (i % 2 != 0) {
                indicesToDelete.add(i);
            }
        }
        indicesToDelete.sort(Collections.reverseOrder());
        return indicesToDelete;
    }


    /**
     * Рассчитывает ожидаемую итоговую сумму для оставшихся товаров
     * (удаляются только нечетные позиции).
     *
     * @param items исходный список товаров
     * @return ожидаемая итоговая сумма четных позиций
     */
    @Step("Calculate expected total sum for remaining items")
    private double calculateExpectedTotal(List<CartPage.CartItem> items) {
        double total = 0;
        for (int i = 0; i < items.size(); i++) {
            if (i % 2 == 0) {
                total += items.get(i).getTotal();
            }
        }
        return total;
    }


    /**
     * Удаляет товары из корзины по заданным индексам.
     *
     * @param cartPage страница корзины
     * @param indices список индексов для удаления
     */
    @Step("Delete items by indices: {indices}")
    private void deleteItemsByIndices(CartPage cartPage, List<Integer> indices) {
        for (int index : indices) {
            cartPage.removeItemByIndex(index);
        }
    }


    /**
     * Проверяет соответствие итоговой суммы корзины ожидаемому значению.
     *
     * @param cartPage страница корзины
     * @param expected ожидаемая итоговая сумма
     */
    @Step("Verify cart total equals {expected}")
    private void verifyCartTotal(CartPage cartPage, double expected) {
        double actual = cartPage.getTotal();
        Assertions.assertEquals(expected, actual, 0.01, "Итоговая сумма не совпадает после удаления четных товаров");
    }


}
