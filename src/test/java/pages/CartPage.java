package pages;

import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Страница корзины.
 * Содержит методы для добавления, удаления и проверки суммы.
 */
public class CartPage extends BasePage {

    @FindBy(css = "#totals_table tbody tr td:nth-child(2) span")
    private WebElement totalElement;

    @FindBy(css = ".table.table-striped.table-bordered")
    private WebElement cartTable;

    @FindBy(id = "cart_update")
    private WebElement updateBtn;

    @FindBy(css = "a.logo")
    private WebElement homeLogoLink;

    /**
     * Конструктор страницы корзины.
     *
     * @param driver драйвер.
     * @param waiter экземпляр WebDriver.
     */
    public CartPage(WebDriver driver, WebDriverWait waiter) {
        super(driver, waiter);
        PageFactory.initElements(driver, this);
    }

    /**
     * Список товаров в корзине.
     *
     * @return список объектов товаров.
     */
    @Step("Get list of items from cart")
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

    /**
     * Находит самый дешевый товар.
     *
     * @return дешевый товар или null, если корзина пуста.
     */
    @Step("Find cheapest item in cart")
    public CartItem findCheapestItem() {
        List<CartItem> items = getItems();
        return items.stream()
                .min(Comparator.comparingDouble(item -> item.price))
                .orElse(null);
    }

    /**
     * Получает итоговую сумму корзины.
     *
     * @return число (double) итоговая сумма.
     */
    @Step("Get total cart price")
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

    /**
     * Перейти на главную страницу.
     *
     * @return главная страница магазина.
     */
    @Step("Click homo logo link")
    public MainPage goToHomePage() {
        waiter.until(ExpectedConditions.elementToBeClickable(homeLogoLink)).click();
        return new MainPage(driver, waiter);
    }

    /**
     * Обновляет корзину.
     */
    @Step("Click update button to refresh cart")
    public void updateCart() {
        waiter.until(ExpectedConditions.elementToBeClickable(updateBtn)).click();
    }

    /**
     * Удаляет товар по индексу.
     *
     * @param index индекс товара для удаления.
     */
    @Step("Remove item at index {index} from cart")
    public void removeItemByIndex(int index) {
        List<CartItem> items = getItems();
        if (index < 0 || index >= items.size()) {
            throw new RuntimeException("Некорректный индекс для указателя: " + index);
        }
        CartItem itemToDelete = items.get(index);
        itemToDelete.clickDelete();

        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".table.table-striped.table-bordered tbody tr td")));
    }

    /**
     * Увеличивает количество самого дешевого товара в корзине в 2 раза.
     *
     * @param cheapestItem самый дешевый товар в корзине
     */
    @Step("Update cheapest item quantity (double id)")
    public static void updateCartItemQuantity(CartPage.CartItem cheapestItem, WebDriver driver, WebDriverWait waiter) {
        int currentQty = cheapestItem.getQuantity();
        int newQty = currentQty * 2;
        cheapestItem.getQuantityInput().clear();
        cheapestItem.getQuantityInput().sendKeys(String.valueOf(newQty));
        CartPage tempCart = new CartPage(driver, waiter);
        tempCart.updateCart();
    }

    /**
     * Проверяет корректность итоговой суммы корзины после изменения количества товара.
     *
     * @param cartPage страница корзины
     * @param originProductName название товара, количество которого было изменено
     * @param newQty новое количество товара (должно быть положительным)
     */
    @Step("Verify cart total is correct for item {originProductName} with quantity {newQty}")
    public static double verifyCartTotal(CartPage cartPage, String originProductName, int newQty) {
        List<CartPage.CartItem> items = cartPage.getItems();
        double expectedTotal = 0;
        for (CartPage.CartItem item : cartPage.getItems()) {
            if (item.getName().equals(originProductName)) {
                expectedTotal += (item.getPrice() * newQty);
            } else {
                expectedTotal += (item.getPrice() * item.getQuantity());
            }
        }
        return expectedTotal;
    }

    /**
     * Находит индексы товаров с нечетными позициями (1, 3, 5...), для удаления четных товаров.
     * Так как в пользовательском интерфейсе индексация начинается с 1.
     * Сортирует в обратном порядке для корректного удаления.
     *
     * @param items список товаров в корзине
     * @return список индексов для удаления (от большего к меньшему)
     */
    @Step("Find indices of even items")
    public static List<Integer> findEvenIndices(List<CartPage.CartItem> items) {
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
    public static double calculateExpectedTotal(List<CartPage.CartItem> items) {
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
    public static void deleteItemsByIndices(CartPage cartPage, List<Integer> indices) {
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
    public static boolean verifyCartTotal(CartPage cartPage, double expected) {
        double actual = cartPage.getTotal();
        return expected == actual;
    }


    /**
     * Внутренний класс для хранения элементов товара.
     */
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

    }
}
