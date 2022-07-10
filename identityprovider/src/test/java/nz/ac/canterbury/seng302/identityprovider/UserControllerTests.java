package nz.ac.canterbury.seng302.identityprovider;

import nz.ac.canterbury.seng302.identityprovider.controller.UserController;
import nz.ac.canterbury.seng302.identityprovider.model.User;
import nz.ac.canterbury.seng302.identityprovider.model.UserRepository;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;


import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    UserRepository userRepository;

    @Mock
    private User user;

    /**
     * Tests that the user gets the correct image type when requesting it from the idp
     * @throws Exception Expection thrown during mockMvc run
     */
    @Test
    void givenUserHasImage_whenGetImageCalled_thenJPGImageReturned() throws Exception{
        when(userRepository.getUserByUserId(anyInt())).thenReturn(user);
        when(user.getProfileImagePath()).thenReturn("testUserImage.jpg");

        this.mockMvc
		.perform(get("/profile/1"))
		.andExpect(status().isOk())
		.andExpect(result -> {
			assertEquals("image/jpeg", result.getResponse().getContentType());
		});
	}

	@Test
	void givenUserHasNoImage_whenGetImageCalled_thenDefaultSVGImageReturned() throws Exception{

        when(userRepository.getUserByUserId(anyInt())).thenReturn(user);
        when(user.getProfileImagePath()).thenReturn(null);

        this.mockMvc
		.perform(get("/profile/1"))
		.andExpect(status().isOk())
		.andExpect(result -> {
			assertEquals("image/svg+xml", result.getResponse().getContentType());
        });
    }
}
