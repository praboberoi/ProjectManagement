package nz.ac.canterbury.seng302.identityprovider.service;

import com.google.protobuf.ByteString;
import nz.ac.canterbury.seng302.identityprovider.model.User;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import nz.ac.canterbury.seng302.identityprovider.model.UserRepository;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * Handles logic for saving profile pictures
 */
public class UserProfilePhotoService {
    static final Path SERVER_BASE_PATH = Paths.get("identityprovider/src/main/resources/profilePhotos");


    /**
     * Writes image file to profilePhotos
     * @param writer OutputStream containing pathway
     * @param content Image content
     */
     static void writeFile(OutputStream writer, ByteString content) throws IOException {
        writer.write(content.toByteArray());
        writer.flush();
    }
    /**
     * Creates file name and writer
     * @param request Contains image information
     * @return Writer containing information to save file
     */
    static OutputStream getFilePath(UploadUserProfilePhotoRequest request) throws IOException {
        var fileName = request.getMetaData().getUserId() + "." + request.getMetaData().getFileType();
        return Files.newOutputStream(SERVER_BASE_PATH.resolve(fileName), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    /**
     * Closes OutputStream
     * @param writer The OutputStream
     */
    static void closeFile(OutputStream writer){
        try {
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void savePath(Path path, User user) {
        user.setProfileImagePath(String.valueOf(path));
    }
}
