package tests;

import helpers.ParameterProvider;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pages.CartPage;
import pages.MainPage;
import pages.ProductListingPage;
import pages.ProductPage;
import utils.TestDataUtils;

import java.util.List;

public class SearchAndCartTest extends BaseTest {

    @Test
    @DisplayName("Проверка поисковой выдачи, добавления товара и изменения количество в корзине")
    public void testSearchAndCartOperations() {
        MainPage mainPage = new MainPage(driver, waiter);
        ProductListingPage listingPage = mainPage.searchFor(ParameterProvider.get("name.product.search"));
        listingPage.selectSortBy(ParameterProvider.get("sort.products.name.a.z"));
        int qty1 = TestDataUtils.randomQuantityProduct();
        int qty2 = TestDataUtils.randomQuantityProduct();
        int indexFirstProduct = Integer.parseInt(ParameterProvider.get("index.product.to.add.first"));
        int indexSecondProduct = Integer.parseInt(ParameterProvider.get("index.product.to.add.second"));
        addProduct(listingPage, indexFirstProduct, qty1);
        listingPage = mainPage.searchFor(ParameterProvider.get("name.product.search"));
        listingPage.selectSortBy(ParameterProvider.get("sort.products.name.a.z"));
        addProductToCart(listingPage, indexSecondProduct, qty2);
        CartPage cartPage = new CartPage(driver, waiter);
        CartPage.CartItem cheapestItem = cartPage.findCheapestItem();
        Assertions.assertNotNull(cheapestItem, "В корзине нет товаров");
        String cheapestItemName = cheapestItem.getName();
        int newQty = cheapestItem.getQuantity() * 2;
        updateCartItemQuantity(cheapestItem);
        verifyCartTotal(cartPage, cheapestItemName, newQty);
    }

    @Step("Add product at index {index} with quantity {qty} and return to main page")
    private void addProduct(ProductListingPage listingPage, int index, int qty) {
        ProductPage productPage = listingPage.navigateToProductByIndex(index);
        CartPage cartPage = productPage.setQuantity(qty).addToCart();
        MainPage mainPage = cartPage.goToHomePage();
    }

    @Step("Add product at index {index} with quantity {qty} and stay in cart")
    private void addProductToCart(ProductListingPage listingPage, int index, int qty) {
        ProductPage productPage = listingPage.navigateToProductByIndex(index);
        productPage.setQuantity(qty).addToCart();
    }

    @Step("Update cheapest item quantity (double id)")
    private void updateCartItemQuantity(CartPage.CartItem cheapestItem) {
        int currentQty = cheapestItem.getQuantity();
        int newQty = currentQty * 2;
        cheapestItem.getQuantityInput().clear();
        cheapestItem.getQuantityInput().sendKeys(String.valueOf(newQty));
        CartPage tempCart = new CartPage(driver, waiter);
        tempCart.updateCart();
    }

    @Step("Verify cart total is correct for item {originProductName} with quantity {newQty}")
    private void verifyCartTotal(CartPage cartPage, String originProductName, int newQty) {
        List<CartPage.CartItem> items = cartPage.getItems();
        double expectedTotal = 0;
        for (CartPage.CartItem item : cartPage.getItems()) {
            if (item.getName().equals(originProductName)) {
                expectedTotal += (item.getPrice() * newQty);
            } else {
                expectedTotal += (item.getPrice() * item.getQuantity());
            }
        }
        double actualTotal = cartPage.getTotal();
        Assertions.assertEquals(expectedTotal, actualTotal, 0.01, "Сумма не совпадает после изменения количества");
    }
}