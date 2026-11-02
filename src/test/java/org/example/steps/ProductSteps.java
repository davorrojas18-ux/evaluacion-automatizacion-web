package org.example.steps;

import org.example.page.ProductPage;
import org.openqa.selenium.WebDriver;

public class ProductSteps {

    private final ProductPage productPage;

    public ProductSteps(WebDriver driver) {
        this.productPage = new ProductPage(driver);
    }

    public void openStore() {
        productPage.openStore();
    }

    public void login(String username, String password) {
        productPage.login(username, password);
    }

    public void navigateToCategory(String category, String subcategory) {
        productPage.navigateToCategory(category, subcategory);
    }

    public void addFirstProductToCart(int quantity) {
        productPage.addFirstProductToCart(quantity);
    }

    public void validateAddedProductInPopup() {
        productPage.validateAddedProductInPopup();
    }

    public void validatePopupTotal() {
        productPage.validatePopupTotal();
    }

    public void finalizePurchase() {
        productPage.finalizePurchase();
    }

    public void validateCartPageTitle() {
        productPage.validateCartPageTitle();
    }

    public void validateCartPriceCalculations() {
        productPage.validateCartPriceCalculations();
    }
}
