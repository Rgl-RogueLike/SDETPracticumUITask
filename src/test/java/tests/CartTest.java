package tests;

import helpers.ParameterProvider;
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
        for (int i = 0; i < amountProducts; i++) {
            ProductPage productPage = mainPage.navigateToRandomProduct();
            int qty = TestDataUtils.randomQuantityProduct();
            CartPage cartPage = productPage.setQuantity(qty).addToCart();
            mainPage = cartPage.goToHomePage();
        }
        driver.get(ParameterProvider.get("cart.url"));
        CartPage cartPage = new CartPage(driver, waiter);
        List<CartPage.CartItem> items = cartPage.getItems();
        List<Integer> indicesToDelete = new ArrayList<>();
        double expectedTotal = 0;

        for (int i = 0; i < items.size(); i++) {
            if (i % 2 != 0) {
                indicesToDelete.add(i);
            } else {
                expectedTotal += items.get(i).getTotal();
            }
        }

        indicesToDelete.sort(Collections.reverseOrder());
        for (int index : indicesToDelete) {
            cartPage.removeByIndex(index);
        }

        double actualTotal = cartPage.getTotal();
        Assertions.assertEquals(expectedTotal, actualTotal, 0.01, "Итоговая сумма не совпадает после удаления четных товаров");
    }
}
