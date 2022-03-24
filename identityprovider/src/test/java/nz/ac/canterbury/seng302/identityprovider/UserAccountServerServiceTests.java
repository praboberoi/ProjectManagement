package nz.ac.canterbury.seng302.identityprovider;

import io.grpc.internal.testing.StreamRecorder;
import nz.ac.canterbury.seng302.identityprovider.model.User;
import nz.ac.canterbury.seng302.identityprovider.model.UserRepository;
import nz.ac.canterbury.seng302.identityprovider.service.UserAccountServerService;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Unit tests for methods in the UserAccountServerService class
 */
@SpringBootTest
class UserAccountServerServiceTests {

    private final UserRepository userRepository = Mockito.mock(UserRepository.class);

    private UserAccountServerService userAccountServerService;

    @BeforeEach
    void initUAServerService() {
        userAccountServerService = new UserAccountServerService(userRepository);
    }

    /**
     * Tests blue sky data for registering a user
     * @throws Exception thrown during awaitCompletion method
     */
    @Test
    void givenValidUserRequest_whenUserCreation_thenResponseSuccessTrue() throws Exception {
        UserRegisterRequest request = UserRegisterRequest.newBuilder()
                .setUsername("tu123")
                .setFirstName("Test")
                .setLastName("User")
                .setEmail("TestUser@canterbury.ac.nz")
                .setPassword("password123")
                .build();
        StreamRecorder<UserRegisterResponse> responseObserver = StreamRecorder.create();
        when(userRepository.save(any(User.class))).then(returnsFirstArg());
        userAccountServerService.register(request, responseObserver);

        if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
            fail("The call did not terminate in time");
        }

        List<UserRegisterResponse> results = responseObserver.getValues();
        UserRegisterResponse response = results.get(0);
        assertTrue(response.getIsSuccess());
    }
}
