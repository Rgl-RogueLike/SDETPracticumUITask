package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CartPage extends BasePage {

    @FindBy(css = "#totals_table tbody tr td:nth-child(2) span")
    private WebElement totalElement;

    @FindBy(css = ".table.table-striped.table-bordered")
    private WebElement cartTable;

    @FindBy(id = "cart_update")
    private WebElement updateBtn;

    public CartPage(WebDriver driver, WebDriverWait waiter) {
        super(driver, waiter);
        PageFactory.initElements(driver, this);
    }

    public List<CartItem> getItems() {
        waiter.until(ExpectedConditions.visibilityOf(cartTable));

        List<WebElement> rows = cartTable.findElements(By.cssSelector("tbody tr"));
        List<CartItem> items = new ArrayList<>();

        for (int i = 1; i < rows.size(); i++) {
            WebElement row = rows.get(i);
            List<WebElement> cells = row.findElements(By.tagName("td"));
            if (cells.size() < 6) {
                continue;
            }
            String name = returnShortName(cells.get(1).getText().trim());
            String priceString = cells.get(3).getText().replace("$", "")
                    .replace(",", "").trim();
            double price = Double.parseDouble(priceString);
            WebElement qtyInput = row.findElement(By.cssSelector("input[type='text']"));
            WebElement deleteBtn = row.findElement(By.cssSelector("a.btn.btn-sm.btn-default"));
            items.add(new CartItem(name, price, qtyInput, deleteBtn));
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
        waiter.until(ExpectedConditions.visibilityOf(totalElement));
        return parseCurrency(totalElement.getText());
    }

    private double parseCurrency(String text) {
        return Double.parseDouble(text.replace("$", "").replace(",", "").trim());
    }

    private String returnShortName(String name) {
        int dashIndex = name.indexOf("- ");
        return dashIndex != -1 ? name.substring(0, dashIndex).trim() : name.trim();
    }

    public void updateCart() {
        waiter.until(ExpectedConditions.elementToBeClickable(updateBtn)).click();
    }

    public static class CartItem {
        private String name;
        private double price;
        private WebElement quantityInput;
        private WebElement deleteBtn;

        public CartItem(String name, double price, WebElement quantityInput, WebElement deleteBtn) {
            this.name = name;
            this.price = price;
            this.quantityInput = quantityInput;
            this.deleteBtn = deleteBtn;
        }

        public void clickDelete() {
            deleteBtn.click();
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
