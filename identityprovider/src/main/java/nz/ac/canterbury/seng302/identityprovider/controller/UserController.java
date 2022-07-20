package nz.ac.canterbury.seng302.identityprovider.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import nz.ac.canterbury.seng302.identityprovider.model.UserRepository;

/**
 * Controller for retrieving user information from the Identity Provider through html requests
 */
@Controller
public class UserController {
    private final static String FILE_PATH_ROOT = "./profilePhotos/";
    @Autowired private UserRepository userRepository;
    Logger logger = LoggerFactory.getLogger(UserController.class);
    
    /**
     * Converts the user's profile image into a byte array and returns it to the client
     * @param userId The user whos profile image is requested
     * @return The image as a ResponseEntity
     */
    @GetMapping(path="/profile/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable("id") int userId) {
        byte[] image = new byte[0];
        MediaType imageType = MediaType.parseMediaType("image/png");
        Path imagePath = Paths.get(FILE_PATH_ROOT + userRepository.getUserByUserId(userId).getProfileImagePath());
        try {
            if (Files.exists(imagePath)) {
                logger.info("Loading user {}s's profile image.", userId);
                image = Files.readAllBytes(imagePath);
                imageType = MediaType.parseMediaType(Files.probeContentType(imagePath));
            } else {
                logger.info("Could not load user {}s's profile image. Falling back to default.", userId);
                image = Files.readAllBytes(Paths.get(FILE_PATH_ROOT + "default-image.png"));
            }
        } catch (IOException e) {
            logger.error("Could not load default profile picture fallback.", e);
        }
        return ResponseEntity.ok().contentType(imageType).body(image);
    }
}
