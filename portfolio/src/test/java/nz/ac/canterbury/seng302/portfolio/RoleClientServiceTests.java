package nz.ac.canterbury.seng302.portfolio;

import nz.ac.canterbury.seng302.portfolio.model.User;
import nz.ac.canterbury.seng302.portfolio.service.AuthenticateClientService;
import nz.ac.canterbury.seng302.portfolio.service.RoleClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
@SpringBootTest
public class RoleClientServiceTests {

   private final User user = Mockito.mock(User.class);
   private RoleClientService roleClientService;
   private AuthState authState;
   private AuthenticateClientService authenticateClientService;

   @BeforeEach
   public void setUp() {
      authState = authenticateClientService.checkAuthState();
   }

   @Test
   public void givenAUserWithRoleStudent_whenLoggedIn_thenDisplayTheStudentDashboard() {
      roleClientService.getUserRole(authState);

      //when(user.getRoles()).thenReturn(Collections.singletonList(UserRole.STUDENT));


   }
}
