package nz.ac.canterbury.seng302.portfolio.cucumber;

import com.google.protobuf.ByteString;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class checkFileTypeTest {

    private UploadUserProfilePhotoRequest.Builder userRequest;
    private ProfilePhotoUploadMetadata.Builder msg;

//    private validateImageType v

    @Given("User selects browse under profile photo")
    public void givenUserSelectsBrowsePhoto(){
        userRequest = UploadUserProfilePhotoRequest.newBuilder();
        msg = ProfilePhotoUploadMetadata.newBuilder();
    }

    @When("User selects an {string}")
    public void whenUserSelectsImage(String image) {
        msg.setFileType(image);
        msg.setUserId(1);
        userRequest.setMetaData(msg);
        userRequest.setFileContent(ByteString.copyFromUtf8("Test"));
    }

    @Then("Error message should be {string}")
    public void thenDoNotShowErrorMsg(String error) {
//        validateImageType.
        assertEquals(error, null);
    }

    @Then("Account page should display {string}")
    public void thenShowErrorMsg(String error) {
//        validateImageType.
        assertEquals(error, msg.getFileType());
//        assertEquals(imag, validateImageType(userRequest));
    }



}
