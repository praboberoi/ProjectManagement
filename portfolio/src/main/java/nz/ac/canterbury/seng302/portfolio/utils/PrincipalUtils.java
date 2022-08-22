package nz.ac.canterbury.seng302.portfolio.utils;

import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;

import java.util.Arrays;
import java.util.List;

/**
 * A collection of methods that retrieve data from a users principal
 */
public class PrincipalUtils {

    /**
     * Gets the user's id from the provided AuthState principal
     * @param principal - current user detail.
     * @return
     */
    public static int getUserId(AuthState principal) {
        int id = Integer.parseInt(principal.getClaimsList().stream()
            .filter(claim -> claim.getType().equals("nameid"))
            .findFirst()
            .map(ClaimDTO::getValue)
            .orElse("-100"));
        return id;
    }
 
    /**
     * Get the current user (principal) roles and returns them as a list.
     * @param principal - current user detail.
     * @return List of current user roles.
     */
    public static List<String> getUserRole(AuthState principal) {
        List<String> userRoles = Arrays.asList(principal.getClaimsList().stream()
        .filter(claim -> claim.getType().equals("role")).findFirst().map(ClaimDTO::getValue).orElse("NOT FOUND").split(","));
        return userRoles;
    }

    /**
     * Check whether the current user has a teacher or course administrator role.
     * @param principal - current user
     * @return true if the user has role teacher or course administrator
     */
    public static boolean checkUserIsTeacherOrAdmin(AuthState principal) {
        List<String> userRoles = Arrays.asList(principal.getClaimsList().stream().filter(claim -> claim.getType().equals("role")).findFirst().map(ClaimDTO::getValue).orElse("NOT FOUND").split(","));
        return (userRoles.contains(UserRole.TEACHER.name()) || userRoles.contains(UserRole.COURSE_ADMINISTRATOR.name()));
    }

    /**
     * Get the current user's username.
     * @param principal - current user detail.
     * @return Current user's username.
     */
    public static String getUserName(AuthState principal) {
        return principal.getClaimsList().stream()
            .filter(claim -> claim.getType().equals("unique_name"))
            .findFirst()
            .map(ClaimDTO::getValue)
            .orElse("guest");
    }
   
}
