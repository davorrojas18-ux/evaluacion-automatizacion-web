package org.example.stepsdefinitions;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.example.core.DriverManager;
import org.example.steps.ProductSteps;
import org.openqa.selenium.WebDriver;

public class ProductStepsDef {

    private ProductSteps productSteps;

    @Before()
    public void initializeProductSteps() {
        DriverManager.startDriver();
        WebDriver driver = DriverManager.getDriver();
        productSteps = new ProductSteps(driver);
    }

    @After
    public void closeBrowser() {
        DriverManager.closeDriver();
    }

    @Given("estoy en la página de la tienda")
    public void openStore() {
        productSteps.openStore();
    }

    @Given("me logueo con mi usuario {string} y clave {string}")
    public void login(String username, String password) {
        productSteps.login(username, password);
    }

    @When("navego a la categoria {string} y subcategoria {string}")
    public void navigateToCategory(String category, String subcategory) {
        productSteps.navigateToCategory(category, subcategory);
    }

    @When("agrego {int} unidades del primer producto al carrito")
    public void addFirstProductToCart(int quantity) {
        productSteps.addFirstProductToCart(quantity);
    }

    @Then("valido en el popup la confirmación del producto agregado")
    public void validateAddedProductInPopup() {
        productSteps.validateAddedProductInPopup();
    }

    @Then("valido en el popup que el monto total sea calculado correctamente")
    public void validatePopupTotal() {
        productSteps.validatePopupTotal();
    }

    @When("finalizo la compra")
    public void finalizePurchase() {
        productSteps.finalizePurchase();
    }

    @Then("valido el titulo de la pagina del carrito")
    public void validateCartPageTitle() {
        productSteps.validateCartPageTitle();
    }

    @Then("vuelvo a validar el calculo de precios en el carrito")
    public void validateCartPriceCalculations() {
        productSteps.validateCartPriceCalculations();
    }
}
