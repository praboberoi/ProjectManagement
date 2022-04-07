package nz.ac.canterbury.seng302.portfolio.cucumber;

import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.*;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.HashMap;
import java.util.Map;

public class ProfilePhotoStepDefinitions {
    AuthState mockPrincipal;

    @Before
    public void setup() {
        mockPrincipal = Mockito.mock(AuthState.class);
    }

    @Given("User exists with profile photo")
    public void user_exists_with_profile_photo() {
        Mockito.when(mockPrincipal.getClaimsList().stream()
                .filter(claim -> claim.getType().equals("nameid"))
                .findFirst()
                .map(ClaimDTO::getValue)
                .orElse("-100")).thenReturn(String.valueOf(1));

    }

    @When("User deletes their photo")
    public void user_deletes_their_photo() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("Photo is deleted")
    public void photo_is_deleted() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("User has no photo")
    public void user_has_no_photo() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Given("User exists with no profile photo")
    public void user_exists_with_no_profile_photo() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("Photo not found error is thrown")
    public void photo_not_found_error_is_thrown() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

}
