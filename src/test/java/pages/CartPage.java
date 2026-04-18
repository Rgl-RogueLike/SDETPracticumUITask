package pages;

import constants.AppConstants;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import utils.WaitHelper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CartPage {

    private WebDriver driver;
    protected WaitHelper waitHelper;

    private final By rowLocator = By.tagName("tr");
    private final By totalLocator = By.cssSelector(".cart_total");

    public CartPage(WebDriver driver) {
        this.driver = driver;
        this.waitHelper = new WaitHelper(driver, AppConstants.DEFAULT_TIMEOUT_SECONDS);
        PageFactory.initElements(driver, this);
    }

    public List<CartItem> getItems() {
        waitHelper.waitForVisibility(By.cssSelector(".table-responsive.cart-info table tbody tr td"));

        WebElement table = driver.findElement(By.cssSelector(".product-list > table:nth-child(1)"));
        List<WebElement> rows = table.findElements(rowLocator);
        List<CartItem> items = new ArrayList<>();

        for (int i = 1; i < rows.size(); i++) {
            WebElement row = rows.get(i);
            List<WebElement> cells = row.findElements(By.tagName("td"));
            String name = returnShortName(cells.get(1).getText().trim());
            String priceString = cells.get(3).getText().replace("$", "")
                    .replace(",", "").trim();
            double price = Double.parseDouble(priceString);
            WebElement qtyInput = row.findElement(By.cssSelector("td.align_center input[type='text']"));
            items.add(new CartItem(name, price, qtyInput));
        }
        return items;
    }

    public CartItem findCheapestItem() {
        List<CartItem> items = getItems();
        return items.stream()
                .min(Comparator.comparingDouble(item -> item.price))
                .orElse(null);
    }

    public double getTotal() {
        String text = waitHelper.waitForVisibility(totalLocator).getText();
        return parseCurrency(text);
    }

    private double parseCurrency(String text) {
        return Double.parseDouble(text.replace("$", "").replace(",", "").trim());
    }

    private String returnShortName(String name) {
        int dashIndex = name.indexOf("- ");
        return dashIndex != -1 ? name.substring(0, dashIndex).trim() : name.trim();
    }

    public static class CartItem {
        private String name;
        private double price;
        private WebElement quantityInput;

        public CartItem(String name, double price, WebElement quantityInput) {
            this.name = name;
            this.price = price;
            this.quantityInput = quantityInput;
        }

        public int getQuantity() {
            return Integer.parseInt(quantityInput.getAttribute("value"));
        }

        public double getTotal() {
            return price * getQuantity();
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public WebElement getQuantityInput() {
            return quantityInput;
        }

        public void setQuantityInput(WebElement quantityInput) {
            this.quantityInput = quantityInput;
        }
    }
}
