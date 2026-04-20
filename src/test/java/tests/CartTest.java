package tests;

import helpers.ParameterProvider;
import io.qameta.allure.Step;
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

public class CartTest extends BaseTest{

    @Test
    @DisplayName("Проверка добавления 5 случайных товаров и удаления четных позиций в корзине")
    public void testRandomProductDelection() {
        MainPage mainPage = new MainPage(driver, waiter);
        int amountProducts = Integer.parseInt(ParameterProvider.get("amount.products.add.cart"));
        addRandomProductToCart(mainPage, amountProducts);
        driver.get(ParameterProvider.get("cart.url"));
        CartPage cartPage = new CartPage(driver, waiter);
        List<CartPage.CartItem> items = cartPage.getItems();
        List<Integer> indicesToDelete = findEvenIndicies(items);
        double expectedTotal = calculateExpectedTotal(items);
        deleteItemsByIndices(cartPage, indicesToDelete);
        verifyCartTotal(cartPage, expectedTotal);
    }

    @Step("Add {amount} random products to cart")
    private void addRandomProductToCart(MainPage mainPage, int amount) {
        for (int i = 0; i < amount; i++) {
            ProductPage productPage = mainPage.navigateToRandomProduct();
            int qty = TestDataUtils.randomQuantityProduct();
            CartPage cartPage = productPage.setQuantity(qty).addToCart();
            mainPage = cartPage.goToHomePage();
        }
    }

    @Step("Find indices of even items")
    private List<Integer> findEvenIndicies(List<CartPage.CartItem> items) {
        List<Integer> indicesToDelete = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            if (i % 2 != 0) {
                indicesToDelete.add(i);
            }
        }
        indicesToDelete.sort(Collections.reverseOrder());
        return indicesToDelete;
    }

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

    @Step("Delete items by indices: {indices}")
    private void deleteItemsByIndices(CartPage cartPage, List<Integer> indices) {
        for (int index : indices) {
            cartPage.removeItemByIndex(index);
        }
    }

    @Step("Verify cart total equals {expected}")
    private void verifyCartTotal(CartPage cartPage, double expected) {
        double actual = cartPage.getTotal();
        Assertions.assertEquals(expected, actual, 0.01, "Итоговая сумма не совпадает после удаления четных товаров");
    }


}
