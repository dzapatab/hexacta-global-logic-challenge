package com.hexacta.stepdefinitions;

import com.hexacta.drivers.Driver;
import com.hexacta.exceptions.GeneralMessage;
import com.hexacta.models.web.CreateUserRequest;
import com.hexacta.pages.AboutUsPage;
import com.hexacta.pages.HomePage;
import com.hexacta.questions.web.GetElement;
import com.hexacta.tasks.web.InAboutUs;
import com.hexacta.tasks.web.SendDataLetsWorkTogether;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.screenplay.abilities.BrowsingTheWeb;
import org.openqa.selenium.support.PageFactory;
import static com.hexacta.drivers.Driver.getDriver;
import static com.hexacta.exceptions.GeneralMessage.ERROR_NO_MESSAGE_FOUND;
import static com.hexacta.factory.CreateUserDataFactory.createUserRequest;
import static com.hexacta.pages.BasePage.getTextFromElement;
import static com.hexacta.questions.web.ValidateInTheForm.*;
import static com.hexacta.tests.ValidateInTheFormPage.*;
import static com.hexacta.userinterfaces.AboutUs.*;
import static com.hexacta.utils.ConsultProperties.CONSULT;
import static com.hexacta.utils.enums.GenericEnums.*;
import static com.hexacta.utils.enums.OptionsHeader.COMPANY;
import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class WebStepDefinition {

    @Given("Navigate in the page Hexacta")
    public void navigateInThePageHexacta() {
          theActorInTheSpotlight().whoCan(BrowsingTheWeb.with(Driver.web().inTheWebPage(CONSULT.getString(HEXACTA_URL.getValue()))));
    }
    @When("Send {string} data in let´s work together")
    public void sendValidDataInLetsWorkTogether(String dataType) {
        theActorInTheSpotlight().attemptsTo(InAboutUs.selectAnOption());
        CreateUserRequest userValid = createUserRequest(dataType);
        theActorInTheSpotlight().attemptsTo(SendDataLetsWorkTogether.inTheForm(userValid).with(dataType));
    }

    @Then("Checks if the message for the {string} is valid")
    public void checksIfTheMessageMorTheDataTypeIsValid(String dataType) {
        switch (by(dataType)) {
            case MISSING_ALL_INFORMATION -> {
                theActorInTheSpotlight().should(seeThat(ifErrorAllIconsArePresent(), equalTo(true))
                        .orComplainWith(GeneralMessage.class, ERROR_NO_MESSAGE_FOUND));
                theActorInTheSpotlight().should(seeThat(GetElement.OfFront(MESSAGE_MANDATORY_FIRST_NAME), equalTo(INVALID_VALUE.getValue()))
                        .orComplainWith(GeneralMessage.class, ERROR_NO_MESSAGE_FOUND));
                theActorInTheSpotlight().should(seeThat(GetElement.OfFront(MESSAGE_MANDATORY_LAST_NAME), equalTo(INVALID_VALUE.getValue()))
                        .orComplainWith(GeneralMessage.class, ERROR_NO_MESSAGE_FOUND));
                theActorInTheSpotlight().should(seeThat(GetElement.OfFront(MANDATORY_MESSAGE_EMPTY_OR_NULL_EMAIL), equalTo(INVALID_VALUE.getValue()))
                        .orComplainWith(GeneralMessage.class, ERROR_NO_MESSAGE_FOUND));
            }
            case VALID_USER, USER_WITHOUT_COMPANY ->
                    theActorInTheSpotlight().should(seeThat(ifErrorAllIconsArePresent(), equalTo(false))
                            .orComplainWith(GeneralMessage.class, ERROR_NO_MESSAGE_FOUND));
            case USER_WITH_EMAIL_INVALID_AND_WITHOUT_COMPANY -> {
                theActorInTheSpotlight().should(seeThat(ifErrorIconsFirstNameIsPresent(), equalTo(false))
                        .orComplainWith(GeneralMessage.class, ERROR_NO_MESSAGE_FOUND));
                theActorInTheSpotlight().should(seeThat(ifErrorIconsLastNameIsPresent(), equalTo(false))
                        .orComplainWith(GeneralMessage.class, ERROR_NO_MESSAGE_FOUND));
                theActorInTheSpotlight().should(seeThat(ifErrorIconsEmailIsPresent(), equalTo(true))
                        .orComplainWith(GeneralMessage.class, ERROR_NO_MESSAGE_FOUND));
                theActorInTheSpotlight().should(seeThat(GetElement.OfFront(MESSAGE_MANDATORY_EMAIL), equalTo(INVALID_EMAIL_ADDRESS_MESSAGE.getValue()))
                        .orComplainWith(GeneralMessage.class, ERROR_NO_MESSAGE_FOUND));
            }
            default -> throw new IllegalArgumentException("Invalid dataType: " + dataType);
        }
    }


    @When("Send {string} data in let´s work together with page object model")
    public void sendValidDataInLetsWorkTogetherWithPageObjectModel(String dataType) {
        HomePage homePage = PageFactory.initElements(getDriver(), HomePage.class);
        homePage.clickCloseImage();
        homePage.selectHeaderOption(COMPANY.getValue());
        AboutUsPage aboutUsPage = PageFactory.initElements(getDriver(), AboutUsPage.class);
        aboutUsPage.sendDataLetsWorkTogether(createUserRequest(dataType), dataType);
    }
    
    @Then("Checks if the message for the {string} is valid with page object model")
    public void checksIfTheMessageMorTheDataTypeIsValidWithPageObjectModel(String dataType) {
        switch (by(dataType)) {
            case MISSING_ALL_INFORMATION -> {
                assertThat(ERROR_NO_MESSAGE_FOUND,
                        ifErrorAllIconsArePresentPage(), equalTo(true));
                assertThat(ERROR_NO_MESSAGE_FOUND,
                        getTextFromElement(AboutUsPage.MESSAGE_MANDATORY_FIRST_NAME), equalTo(INVALID_VALUE.getValue()));
                assertThat(ERROR_NO_MESSAGE_FOUND,
                        getTextFromElement(AboutUsPage.MESSAGE_MANDATORY_LAST_NAME), equalTo(INVALID_VALUE.getValue()));
                assertThat(ERROR_NO_MESSAGE_FOUND,
                        getTextFromElement(AboutUsPage.MANDATORY_MESSAGE_EMPTY_OR_NULL_EMAIL), equalTo(INVALID_VALUE.getValue()));
            }
            case VALID_USER, USER_WITHOUT_COMPANY ->
                 assertThat(ERROR_NO_MESSAGE_FOUND,
                    ifErrorAllIconsArePresentPage(), equalTo(false));
            case USER_WITH_EMAIL_INVALID_AND_WITHOUT_COMPANY -> {
                assertThat(ERROR_NO_MESSAGE_FOUND,
                        ifErrorIconsFirstNameIsPresentPage(), equalTo(false));
                assertThat(ERROR_NO_MESSAGE_FOUND,
                        ifErrorIconsLastNameIsPresentPage(), equalTo(false));
                assertThat(ERROR_NO_MESSAGE_FOUND,
                        ifErrorIconsEmailIsPresentPage(), equalTo(true));
                assertThat(ERROR_NO_MESSAGE_FOUND,
                        getTextFromElement(AboutUsPage.MESSAGE_MANDATORY_EMAIL), equalTo(INVALID_EMAIL_ADDRESS_MESSAGE.getValue()));
            }
            default -> throw new IllegalArgumentException("Invalid dataType: " + dataType);
        }
    }
}
