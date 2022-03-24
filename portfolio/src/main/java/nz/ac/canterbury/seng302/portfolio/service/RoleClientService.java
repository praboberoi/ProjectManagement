package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.ProjectRepository;
import nz.ac.canterbury.seng302.portfolio.model.User;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class RoleClientService {
    @Autowired
    private ProjectRepository projectRepo;
    private User currentUser;

    public List<UserRole> getUserRole() {
        return currentUser.getRoles();
    }


}
