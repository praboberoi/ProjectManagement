package nz.ac.canterbury.seng302.portfolio.util;

import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;

public class PrincipalUtils {

    public static int getUserId(AuthState principal) {
        return Integer.parseInt(principal.getClaimsList().stream()
        .filter(claim -> claim.getType().equals("nameid"))
        .findFirst()
        .map(ClaimDTO::getValue)
        .orElse("-1"));
    }

}
