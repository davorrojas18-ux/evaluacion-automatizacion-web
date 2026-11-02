package org.example.page;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProductPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    public ProductPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void openStore() {
        driver.get("https://qalab.bensg.com/store/pe/iniciar-sesion");
    }

    public void login(String username, String password) {
        driver.findElement(By.cssSelector("input#field-email")).sendKeys(username);
        driver.findElement(By.cssSelector("input#field-password")).sendKeys(password);
        driver.findElement(By.cssSelector("button#submit-login")).click();

        By authenticationError = By.cssSelector(
                ".login-form .help-block li.alert-danger"
        );
        wait.until(webDriver ->
                webDriver.findElements(authenticationError).stream()
                        .anyMatch(WebElement::isDisplayed)
                        || webDriver.findElements(By.cssSelector("form#login-form")).isEmpty()
        );

        for (WebElement error : driver.findElements(authenticationError)) {
            if (error.isDisplayed()) {
                throw new AssertionError(
                        "No se pudo iniciar sesión: " + error.getText().trim()
                );
            }
        }
    }

    public void navigateToCategory(String category, String subcategory) {
        driver.get("https://qalab.bensg.com/store/pe/");
        clickLinkByText(
                By.cssSelector("li.category > a.dropdown-item"),
                category
        );
        clickLinkByText(
                By.cssSelector(".category-sub-menu a"),
                subcategory
        );
    }

    private String productName;
    private int productQuantity;
    private BigDecimal productPrice;

    public void addFirstProductToCart(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("La cantidad del producto debe ser mayor que cero.");
        }

        findVisible(By.cssSelector("div#js-product-list article a")).click();
        productName = findVisible(By.cssSelector(".product-container h1")).getText().trim();
        WebElement priceElement = findVisible(By.cssSelector(".current-price-value"));
        String priceContent = priceElement.getAttribute("content");
        productPrice = parseMoney(priceContent == null || priceContent.isBlank()
                ? priceElement.getText()
                : priceContent);

        WebElement quantityInput = findVisible(By.cssSelector("input#quantity_wanted"));
        quantityInput.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE);
        quantityInput.sendKeys(String.valueOf(quantity));
        productQuantity = quantity;
        findVisible(By.cssSelector("button.add-to-cart")).click();
    }

    public void validateAddedProductInPopup() {
        String popupProductName = findVisible(
                By.cssSelector(".modal-dialog .product-name")
        ).getText().trim();
        String popupProductQuantity = findVisible(
                By.cssSelector(".modal-dialog .product-quantity")
        ).getText();

        if (!popupProductName.equalsIgnoreCase(productName)) {
            throw new AssertionError("El nombre del producto en el popup no coincide con el producto agregado al carrito. ");
        }

        Matcher quantityMatcher = Pattern.compile("\\d+").matcher(popupProductQuantity);
        if (!quantityMatcher.find()) {
            throw new AssertionError("No se pudo obtener la cantidad del producto desde el popup.");
        }
        if (Integer.parseInt(quantityMatcher.group()) != productQuantity) {
            throw new AssertionError("La cantidad del producto en el popup no coincide con la cantidad agregada al carrito.");
        }
    }

    public void validatePopupTotal() {
        String totalPriceText = findVisible(
                By.cssSelector(".modal-dialog .subtotal")
        ).getText();
        BigDecimal totalPrice = parseMoney(totalPriceText);
        BigDecimal expectedTotal = productPrice.multiply(BigDecimal.valueOf(productQuantity));

        if (toTwoDecimals(totalPrice).compareTo(toTwoDecimals(expectedTotal)) != 0) {
            throw new AssertionError("El precio total en el popup no coincide con el precio calculado.");
        }
    }

    public void finalizePurchase() {
        findVisible(By.cssSelector(".modal-dialog .cart-content-btn a")).click();
    }

    public void validateCartPageTitle() {
        String pageTitle = driver.getTitle();
        if (pageTitle == null || !pageTitle.contains("Carrito")) {
            throw new AssertionError("El título de la página del carrito no es el esperado.");
        }
    }

    public void validateCartPriceCalculations() {
        String cartTotalPrice = findVisible(
                By.cssSelector(".cart-summary-line .value")
        ).getText();
        BigDecimal totalPrice = parseMoney(cartTotalPrice);
        BigDecimal expectedTotal = productPrice.multiply(BigDecimal.valueOf(productQuantity));

        if (toTwoDecimals(totalPrice).compareTo(toTwoDecimals(expectedTotal)) != 0) {
            throw new AssertionError("El precio total en el carrito no coincide con el precio calculado.");
        }
    }

    private WebElement findVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    private void clickLinkByText(By linkLocator, String text) {
        WebElement link = wait.until(webDriver -> {
            for (WebElement candidate : webDriver.findElements(linkLocator)) {
                if (candidate.isDisplayed()
                        && candidate.getText().trim().equalsIgnoreCase(text.trim())) {
                    return candidate;
                }
            }
            return null;
        });
        wait.until(ExpectedConditions.elementToBeClickable(link));
        link.click();
    }

    private BigDecimal parseMoney(String value) {
        Matcher priceMatcher = Pattern.compile("\\d+(?:\\.\\d+)?").matcher(value);
        if (!priceMatcher.find()) {
            throw new AssertionError("No se pudo interpretar el precio: " + value);
        }

        try {
            return new BigDecimal(priceMatcher.group());
        } catch (NumberFormatException exception) {
            throw new AssertionError("No se pudo interpretar el precio: " + value, exception);
        }
    }

    private BigDecimal toTwoDecimals(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}
