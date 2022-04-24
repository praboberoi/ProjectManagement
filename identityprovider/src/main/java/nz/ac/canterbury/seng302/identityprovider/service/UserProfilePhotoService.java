package nz.ac.canterbury.seng302.identityprovider.service;

import nz.ac.canterbury.seng302.shared.identityprovider.DeleteUserProfilePhotoRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.DeleteUserProfilePhotoResponse;

public class UserProfilePhotoService {

    public UserProfilePhotoService() {
        
    }

    public DeleteUserProfilePhotoResponse deleteUserProfilePhoto(DeleteUserProfilePhotoRequest request) {
        return DeleteUserProfilePhotoResponse.newBuilder().build();
    }

}
