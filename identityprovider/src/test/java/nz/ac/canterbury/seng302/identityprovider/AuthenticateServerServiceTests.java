package nz.ac.canterbury.seng302.identityprovider;

import io.grpc.internal.testing.StreamRecorder;
import nz.ac.canterbury.seng302.identityprovider.model.User;
import nz.ac.canterbury.seng302.identityprovider.model.UserRepository;
import nz.ac.canterbury.seng302.identityprovider.service.AuthenticateServerService;
import nz.ac.canterbury.seng302.identityprovider.util.EncryptionUtilities;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthenticateRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthenticateResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

@SpringBootTest
public class AuthenticateServerServiceTests {
    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final User user = Mockito.mock(User.class);
    private static AuthenticateRequest.Builder authenticateRequest;

    @InjectMocks
    private AuthenticateServerService authenticateServerService;

    @BeforeAll
    static void initAuthenticateRequest() {
        authenticateRequest = AuthenticateRequest.newBuilder();
        authenticateRequest.setUsername("abc123");
        authenticateRequest.setPassword("test_password1");
    }

    @BeforeEach
    void initAuthenticateServerService() {
        authenticateServerService = new AuthenticateServerService(userRepository);

    }

    /**
     *
     */
    @Test
    void givenCorrectUserCredentials_whenAuthenticate_thenReturnResponseSuccessTrue () throws Exception {
        when(user.getUsername()).thenReturn("abc123");
        when(user.getPassword()).thenReturn(EncryptionUtilities.encryptPassword("", "test_password1"));
        when(user.getSalt()).thenReturn("");
        when(user.getFirstName()).thenReturn("");
        when(user.getLastName()).thenReturn("");
        when(user.getEmail()).thenReturn("");

        when(userRepository.getUserByUsername(user.getUsername())).thenReturn(user);

        StreamRecorder<AuthenticateResponse> responseObserver = StreamRecorder.create();
        authenticateServerService.authenticate(authenticateRequest.build(), responseObserver);
        if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
            fail("The call did not terminate in time");
        }
        List<AuthenticateResponse> results = responseObserver.getValues();
        System.out.println(results);
        AuthenticateResponse response = results.get(0);
        assertTrue(response.getSuccess());
    }





}
