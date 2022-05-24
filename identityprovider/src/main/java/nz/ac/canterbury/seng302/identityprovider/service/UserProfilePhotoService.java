package nz.ac.canterbury.seng302.identityprovider.service;

import nz.ac.canterbury.seng302.identityprovider.model.User;
import nz.ac.canterbury.seng302.identityprovider.model.UserRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.DeleteUserProfilePhotoResponse;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Handles logic for User Profile Photos
 */
public class UserProfilePhotoService {
    private final Path imageBasePath = Paths.get("identityprovider/src/main/resources/profilePhotos");
    private UserRepository userRepository;

    public UserProfilePhotoService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Deletes a user's profile photo from the database
     * @param
     * @return
     */
    public DeleteUserProfilePhotoResponse deleteUserProfilePhoto(int userId) {
        DeleteUserProfilePhotoResponse.Builder response = DeleteUserProfilePhotoResponse.newBuilder().setIsSuccess(true);
        try {
            User user = userRepository.getUserByUserId(userId);

            if (user == null) {
                response.setIsSuccess(false).setMessage("Could not find user");
            } else if (user.getProfileImagePath() == null) {
                response.setIsSuccess(false).setMessage("Could not find a profile photo to delete");
            } else {
                Path imagePath = imageBasePath.resolve(user.getProfileImagePath());
                Files.deleteIfExists(imagePath);
                response.setIsSuccess(true).setMessage("File deleted");
            }

            user.setProfileImagePath(null);
            userRepository.save(user);
        } catch (Exception e) {
            response.setIsSuccess(false).setMessage("An error occured while deleting the profile photo" + e.getMessage());
        }
        return response.build();
    }

}
