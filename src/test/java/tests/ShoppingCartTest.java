package tests;

import base.BaseTest;
import constants.AppConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import pages.CartPage;
import pages.MainPage;
import pages.ProductListingPage;
import pages.ProductPage;

import java.util.Random;

public class ShoppingCartTest extends BaseTest {

    @Test
    public void testSearchAndCart() {
        driver.get(AppConstants.BASE_URL);

        MainPage mainPage = new MainPage(driver);
        ProductListingPage searchPage = mainPage.searchFor(AppConstants.SEARCH_QUERY_SHIRT);
        searchPage.selectSortBy(AppConstants.SORT_NAME_A_Z);
        int qty1 = randomQuantityProduct();
        int qty2 = randomQuantityProduct();
        String listingUrl = driver.getCurrentUrl();

        ProductPage productPage = searchPage.navigateToProductByIndex(AppConstants.INDEX_SECOND_PRODUCT);
        searchPage = productPage.setQuantity(qty1).addToCartAndReturnToListing(listingUrl);
        productPage = searchPage.navigateToProductByIndex(AppConstants.INDEX_THRID_PRODUCT);
        productPage.setQuantity(qty2).addToCart();

        CartPage cartPage = new CartPage(driver);
        CartPage.CartItem cheapestItem = cartPage.findCheapestItem();
        Assertions.assertNotNull(cheapestItem, "В корзине нет товаров");

        String cheapestItemName = cheapestItem.getName();
        int currentQty = cheapestItem.getQuantity();
        int newQty = currentQty * 2;

        WebElement input = cheapestItem.getQuantityInput();
        input.clear();
        input.sendKeys(String.valueOf(newQty));
        input.sendKeys(org.openqa.selenium.Keys.ENTER);

        cartPage = new CartPage(driver);

        double expectedTotal = 0;
        for (CartPage.CartItem item : cartPage.getItems()) {
            if (item.getName().equals(cheapestItemName)) {
                expectedTotal += (item.getPrice() * newQty);
            } else {
                expectedTotal += (item.getPrice() * item.getQuantity());
            }
        }

        double actualTotal = cartPage.getTotal();
        Assertions.assertEquals(expectedTotal, actualTotal, 0.01, "Сумма не совпадает");
    }

    private int randomQuantityProduct() {
        Random random = new Random();
        return random.nextInt(AppConstants.MAX_QUANTITY_PRODUCTS) + AppConstants.MIN_QUANTITY_PRODUCTS;
    }
}