package nz.ac.canterbury.seng302.identityprovider.service;
import nz.ac.canterbury.seng302.shared.identityprovider.ProfilePhotoUploadMetadata;
import nz.ac.canterbury.seng302.shared.identityprovider.UploadUserProfilePhotoRequest;
import nz.ac.canterbury.seng302.shared.util.ValidationError;

import java.util.ArrayList;
import java.util.List;

public class UserProfilePhotoService {


    public List<ValidationError> validateImageType(UploadUserProfilePhotoRequest req) {
        ProfilePhotoUploadMetadata data = req.getMetaData();
        String fileType = data.getFileType();
        ValidationError.Builder errorBuilder = ValidationError.newBuilder();
        List<ValidationError> result = new ArrayList<>();

        if (fileType != ".jpg"){
            errorBuilder.setErrorText("File type must be jpg");
            result.add(errorBuilder.build());
        } else {
            errorBuilder.setErrorText("null");
            result.add(errorBuilder.build());
        }
        return result;
    }

}
