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
 * Controller for the account page
 */
@Controller
public class AccountController {
    private String FILE_PATH_ROOT = "./profilePhotos/";
    @Autowired private UserRepository userRepository;
    Logger logger = LoggerFactory.getLogger(AccountController.class);

    public AccountController () {
    }
        
    @GetMapping(path="/profile/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable("id") int userId) {
        byte[] image = new byte[0];
        MediaType imageType = MediaType.parseMediaType("image/svg+xml");
        Path imagePath = Paths.get(FILE_PATH_ROOT + userRepository.getUserByUserId(userId).getProfileImagePath());
        try {
            if (Files.exists(imagePath)) {
                image = Files.readAllBytes(imagePath);
                imageType = MediaType.parseMediaType(Files.probeContentType(imagePath));
            } else {
                logger.info("Could not load user " + userId + "'s profile image. Falling back to default.");
                image = Files.readAllBytes(Paths.get(FILE_PATH_ROOT + "default-image.svg"));
            }
        } catch (IOException e) {
            logger.error("Could not load default profile picture fallback.", e);
        }
        return ResponseEntity.ok().contentType(imageType).body(image);
    }
}
