package nz.ac.canterbury.seng302.identityprovider.service;

import nz.ac.canterbury.seng302.identityprovider.model.User;
import nz.ac.canterbury.seng302.identityprovider.model.UserRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.DeleteUserProfilePhotoResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.ProfilePhotoUploadMetadata;
import nz.ac.canterbury.seng302.shared.identityprovider.UploadUserProfilePhotoRequest;
import nz.ac.canterbury.seng302.shared.util.ValidationError;

import java.util.ArrayList;
import java.util.List;
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
                response.setIsSuccess(false).setMessage(("Could not find user"));
            } else if (user.getProfileImagePath() == null) {
                response.setIsSuccess(false).setMessage(("Could not find a profile photo to delete"));
            } else {
                Path imagePath = imageBasePath.resolve(user.getProfileImagePath());
                Files.deleteIfExists(imagePath);
                response.setIsSuccess(true).setMessage("File deleted");

            }

            user.setProfileImagePath(null);
            userRepository.save(user);
        } catch (Exception e) {
            response.setIsSuccess(false).setMessage(("An error occurred while deleting the profile photo" + e.getMessage()));
        }
        return response.build();
    }

    public List<ValidationError> validateImageType(UploadUserProfilePhotoRequest req) {
        ProfilePhotoUploadMetadata data = req.getMetaData();
        String fileType = data.getFileType();
        ValidationError.Builder errorBuilder = ValidationError.newBuilder();
        List<ValidationError> result = new ArrayList<>();

        if (fileType != ".jpg") {
            errorBuilder.setErrorText("File type must be jpg");
            result.add(errorBuilder.build());
        } else {
            errorBuilder.setErrorText("null");
            result.add(errorBuilder.build());
        }
        return result;
    }
}

