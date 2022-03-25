package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.ProjectRepository;
import nz.ac.canterbury.seng302.portfolio.model.User;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleClientService {


    public String getUserRole(
        @AuthenticationPrincipal AuthState principal) {

            String role = principal.getClaimsList().stream()
            .filter(claim -> claim.getType().equals("role"))
            .findFirst()
            .map(ClaimDTO::getValue)
            .orElse("NOT FOUND");

            System.out.println(role);
            return role;
    }

}
