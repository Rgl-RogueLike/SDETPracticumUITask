package tests;

import helpers.ParameterProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pages.CartPage;
import pages.MainPage;
import pages.ProductListingPage;
import pages.ProductPage;
import utils.TestDataUtils;

public class SearchAndCartTest extends BaseTest {

    @Test
    @DisplayName("Проверка поисковой выдачи, добавления товара и изменения количество в корзине")
    public void testSearchAndCartOperations() {

        MainPage mainPage = new MainPage(driver, waiter);
        ProductListingPage listingPage = mainPage.searchFor(ParameterProvider.get("name.product.search"));
        listingPage.selectSortBy(ParameterProvider.get("sort.products.name.a.z"));
        int qty1 = TestDataUtils.randomQuantityProduct();
        int qty2 = TestDataUtils.randomQuantityProduct();
        ProductPage productPage = listingPage.navigateToProductByIndex(1);
        CartPage cartPage = productPage.setQuantity(qty1).addToCart();
        mainPage = cartPage.goToHomePage();
        listingPage = mainPage.searchFor(ParameterProvider.get("name.product.search"));
        listingPage.selectSortBy(ParameterProvider.get("sort.products.name.a.z"));
        productPage = listingPage.navigateToProductByIndex(2);
        productPage.setQuantity(qty2).addToCart();

        cartPage = new CartPage(driver, waiter);
        CartPage.CartItem cheapestItem = cartPage.findCheapestItem();
        Assertions.assertNotNull(cheapestItem, "В корзине нет товаров");
        String cheapestItemName = cheapestItem.getName();
        int currentQty = cheapestItem.getQuantity();
        int newQty = currentQty * 2;

        cheapestItem.getQuantityInput().clear();
        cheapestItem.getQuantityInput().sendKeys(String.valueOf(newQty));

        cartPage.updateCart();

        double expectedTotal = 0;
        for (CartPage.CartItem item : cartPage.getItems()) {
            if (item.getName().equals(cheapestItemName)) {
                expectedTotal += (item.getPrice() * newQty);
            } else {
                expectedTotal += (item.getPrice() * item.getQuantity());
            }
        }

        double actualTotal = cartPage.getTotal();
        Assertions.assertEquals(expectedTotal, actualTotal, 0.01, "Сумма не совпадает после изменения");
    }
}