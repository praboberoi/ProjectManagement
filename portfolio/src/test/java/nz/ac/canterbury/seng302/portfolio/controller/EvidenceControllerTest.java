package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Evidence;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.dto.EvidenceDTO;
import nz.ac.canterbury.seng302.portfolio.model.notifications.EvidenceNotification;
import nz.ac.canterbury.seng302.portfolio.service.EvidenceService;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.portfolio.utils.IncorrectDetailsException;
import nz.ac.canterbury.seng302.portfolio.utils.PrincipalUtils;
import nz.ac.canterbury.seng302.portfolio.utils.WebSocketPrincipal;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.StompSubProtocolHandler;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = EvidenceController.class)
@AutoConfigureMockMvc(addFilters = false)
public class EvidenceControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EvidenceService evidenceService;
    @MockBean
    private ProjectService projectService;

    @MockBean private SimpMessagingTemplate template;

    @Autowired
    private EvidenceController evidenceController;

    @MockBean
    private UserAccountClientService userAccountClientService;

    private Evidence evidence;
    private Evidence evidence1;
    private Project project;

    private static MockedStatic<PrincipalUtils> utilities;

    private static WebSocketPrincipal mockedWebSocketPrincipal;

    /**
     * Helper function which creates a new user for testing with
     * @param userId The user id to set the user, this affects nearly all of the user's attributes
     * @return A new User object
     */
    UserResponse.Builder createTestUserResponse(int userId) {
        UserResponse.Builder userResponse = UserResponse.newBuilder()
                .setId(userId)
                .setFirstName("First" + userId)
                .setLastName("Last" + userId)
                .setNickname("Nick" + userId)
                .setUsername("User" + userId)
                .setBio("Bio " + userId)
                .setPersonalPronouns("Pronoun " + userId)
                .setEmail("test" + userId + "@gmail.com")
                .addAllRoles(Arrays.asList(UserRole.STUDENT));
        return userResponse;
    }

    @BeforeAll
    private static void beforeAllInit() {
        utilities = Mockito.mockStatic(PrincipalUtils.class);
        utilities.when(() -> PrincipalUtils.checkUserIsTeacherOrAdmin(any())).thenReturn(true);
        mockedWebSocketPrincipal = mock(WebSocketPrincipal.class);
    }

    @BeforeEach
    public void setup() {
        when(PrincipalUtils.checkUserIsTeacherOrAdmin(any())).thenReturn(true);

        LocalDate now = LocalDate.now();
        project = new Project(1, "Test Project", "test", java.sql.Date.valueOf(now), java.sql.Date.valueOf(now.plusDays(50)));

        evidence = new Evidence.Builder()
            .title("New Evidence")
            .description("New piece of evidence")
            .dateOccurred(java.sql.Date.valueOf(now))
            .ownerId(1)
            .project(project)
            .build();

        evidence1 = new Evidence.Builder()
            .title("Another Evidence")
            .description("Additional piece of evidence")
            .dateOccurred(java.sql.Date.valueOf(now))
            .ownerId(1)
            .project(project)
            .build();

    }

    public EvidenceDTO toDTO(Evidence evidence)  {
        return new EvidenceDTO(
        evidence.getEvidenceId(),
        evidence.getProject(),
        evidence.getDateOccurred(),
        evidence.getTitle(),
        evidence.getDescription(),
        evidence.getOwnerId());
    }

    /**
     * Test verification of evidence object when a valid evidence is saved and checks it redirects the user
     */
    @Test
    void givenServer_whenSaveValidEvidence_thenEvidenceVerifiedSuccessfully() throws Exception {
        when(evidenceService.saveEvidence(any())).thenReturn("Successfully Created " + evidence.getTitle());
        when(PrincipalUtils.getUserId(any())).thenReturn(99);

        this.mockMvc
                .perform(post("/evidence").flashAttr("evidenceDTO", toDTO(evidence)))
                .andExpect(status().isOk())
                .andExpect(content().string("Successfully Created " + evidence.getTitle()));
    }

    /**
     * Tests verification of evidence object when an invalid evidence is saved and checks it redirects the user
     */
    @Test
    void givenServer_whenSaveInvalidEvidence_thenEvidenceVerifiedSuccessfully() throws Exception {
        when(evidenceService.saveEvidence(any())).thenThrow(new IncorrectDetailsException("Failure Saving Evidence"));
        when(PrincipalUtils.getUserId(any())).thenReturn(99);
        this.mockMvc
                .perform(post("/evidence").flashAttr("evidenceDTO", toDTO(evidence1)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Failure Saving Evidence"));

    }

    /**
     * Asserts that when a user is attempting to create an evidence object under another users page, the correct error message is displayed
     * @throws Exception when userId doesn't exist
     */
    @Test
    void givenEvidenceObjectAndIncorrectUser_whenSaveEvidenceCalled_thenEvidenceSavedCorrectly() throws Exception {
        when(PrincipalUtils.getUserId(any())).thenReturn(53);
        when(PrincipalUtils.checkUserIsTeacherOrAdmin(any())).thenReturn(false);
        this.mockMvc
                .perform(post("/evidence").flashAttr("evidenceDTO", toDTO(evidence)))
                .andExpect(status().isForbidden())
                .andExpect(content().string("You may only create evidence on your own evidence page" ));

    }

    /**
     * Tests that when an evidence is called then the list of evidence is correctly added to the ModelAndView return object
     * @throws Exception when userId doesn't exist
     */
    @Test
    void givenEvidenceObject_whenEvidenceCalled_thenCorrectModelViewObjectReturned() throws Exception {
        UserResponse user = createTestUserResponse(99).addRoles(UserRole.COURSE_ADMINISTRATOR).build();
        
        ArrayList<Project> projectList = new ArrayList<>();
        ArrayList<Evidence> evidenceList = new ArrayList<>();
        evidenceList.add(evidence);
        evidenceList.add(evidence1);
        
        when(userAccountClientService.getUser(any())).thenReturn(user);
        when(projectService.getAllProjects()).thenReturn(projectList);
        when(evidenceService.getEvidenceByUserId(99)).thenReturn(List.of(evidence, evidence1));
        when(evidenceService.getNewEvidence(99)).thenReturn(evidence);
        when(userAccountClientService.getUser(99)).thenReturn(user);
        when(PrincipalUtils.getUserId(any())).thenReturn(99);

        this.mockMvc
                .perform(get("/user/99/evidence/"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("listEvidence", evidenceList));

    }

    /**
     * Tests that when EvidenceList is called then the list of evidence is correctly added to the ModelAndView return object
     * @throws Exception when userId doesn't exist
     */
    @Test
    void givenEvidenceObject_whenEvidenceListCalled_thenCorrectModelViewObjectReturned() throws Exception {
        UserResponse user = createTestUserResponse(99).addRoles(UserRole.COURSE_ADMINISTRATOR).build();
        when(userAccountClientService.getUser(any())).thenReturn(user);
        ArrayList<Evidence> evidenceList = new ArrayList<>();
        evidenceList.add(evidence);
        evidenceList.add(evidence1);
        when(evidenceService.getEvidenceByUserId(99)).thenReturn(List.of(evidence, evidence1));
        when(userAccountClientService.getUser(99)).thenReturn(user);
        when(PrincipalUtils.getUserId(any())).thenReturn(99);

        this.mockMvc
                .perform(get("/user/99/evidence/getEvidenceList"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("listEvidence", evidenceList));

    }

    /**
     * Asserts that given an evidence ID and a user ID when selectedEvidence called then returns a list of all evidence and the selected evidence
     * @throws Exception when userId doesn't exist
     */
    @Test
    void givenCorrectEvidenceAndUserIds_whenSelectedEvidenceCalled_thenReturnSelectedEvidence() throws Exception {
        UserResponse user = createTestUserResponse(1).addRoles(UserRole.COURSE_ADMINISTRATOR).build();
        when(userAccountClientService.getUser(any())).thenReturn(user);
        when(evidenceService.getEvidenceByUserId(1)).thenReturn(List.of(evidence, evidence1));
        when(PrincipalUtils.getUserId(any())).thenReturn(1);
        when(evidenceService.getEvidence(33)).thenReturn(evidence);

        this.mockMvc
                .perform(get("/evidence/33"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("selectedEvidence", evidence))
                .andExpect(model().attribute("isCurrentUserEvidence", user.getId()==1));
    }

    /**
     * Asserts that no evidence is selected when a given evidence ID does not exist
     * @throws Exception when userId doesn't exist
     */
    @Test
    void givenIncorrectEvidence_whenSelectedEvidenceCalled_thenNoEvidenceSelected() throws Exception {
        UserResponse user = createTestUserResponse(99).addRoles(UserRole.COURSE_ADMINISTRATOR).build();
        when(userAccountClientService.getUser(any())).thenReturn(user);
        when(evidenceService.getEvidenceByUserId(99)).thenReturn(List.of(evidence, evidence1));
        when(evidenceService.getEvidence(33)).thenThrow(new IncorrectDetailsException("Failed to locate the piece of evidence with ID: 33"));

        this.mockMvc
                .perform(get("/evidence/33"))
                .andExpect(status().isNotFound())
                .andExpect(model().attribute("messageDanger", "Failed to locate the piece of evidence with ID: 33"));
    }

    /**
     * Asserts that given a user ID when createEvidence called then returns a list of all projects,
     * correct submission image, and submission name
     */
    @Test
    void givenCurrentUserId_whenCreateEvidenceCalled_thenCorrectModelViewObjectReturned() throws Exception{
        when(projectService.getAllProjects()).thenReturn(List.of(project));
        when(evidenceService.getNewEvidence(99)).thenReturn(evidence);
        when(PrincipalUtils.getUserId(any())).thenReturn(99);

        this.mockMvc
                .perform(get("/evidence/getNewEvidence"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("evidence", evidence))
                .andExpect(model().attribute("submissionImg", "/icons/create-icon.svg"))
                .andExpect(model().attribute("submissionName", "Create"));

    }

    /**
     * Asserts that given an evidence ID when editEvidence called then returns a list of all projects, submission image,
     * and submission name
     */
    @Test
    void givenCurrentEvidenceId_whenEditEvidenceCalled_thenCorrectModelViewObjectReturned() throws Exception {
        when(projectService.getAllProjects()).thenReturn(List.of(project));
        when(evidenceService.getEvidence(99)).thenReturn(evidence);

        this.mockMvc
                .perform(get("/evidence/99/editEvidence"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("evidence", evidence))
                .andExpect(model().attribute("submissionImg", "/icons/save-icon.svg"))
                .andExpect(model().attribute("submissionName", "Save"));
    }

    /**
     * Asserts that given an incorrect evidence ID when editEvidence called then returns the correct message
     */
    @Test
    void givenIncorrectEvidenceID_whenEditEvidenceCalled_thenModalViewObjectWithDangerMessageReturned() throws Exception {
        when(projectService.getAllProjects()).thenReturn(List.of(project));
        when(evidenceService.getEvidence(99)).thenThrow(new IncorrectDetailsException("Failed to locate the piece of evidence with ID: 99"));

        this.mockMvc
                .perform(get("/evidence/99/editEvidence"))
                .andExpect(status().isNotFound())
                .andExpect(model().attribute("messageDanger", "Failed to locate the piece of evidence with ID: 99"))
                .andExpect(view().name("evidence::serverMessages"));
    }

    /**
     * Asserts that students are unable to delete evidence
     */
    @Test
    void givenStudent_whenDeleteEvidenceCalled_thenEvidenceDeletedSuccessfully() throws Exception {
        when(PrincipalUtils.checkUserIsTeacherOrAdmin(any())).thenReturn(false);
        when(evidenceService.deleteEvidence(99)).thenReturn("Successfully Deleted Test Evidence");
        when(evidenceService.getEvidence(99)).thenReturn(evidence);

        this.mockMvc
                .perform(delete("/evidence/99"))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Insufficient Permissions"));
    }

    /**
     * Asserts that the evidence owner is able to delete a piece of evidence
     */
    @Test
    void givenEvidenceOwner_whenDeleteEvidenceCalled_thenEvidenceDeletedSuccessfully() throws Exception {
        when(PrincipalUtils.checkUserIsTeacherOrAdmin(any())).thenReturn(false);
        when(evidenceService.deleteEvidence(1)).thenReturn("Successfully Deleted Test Evidence");
        when(evidenceService.getEvidence(1)).thenReturn(evidence);
        when(PrincipalUtils.getUserId(any())).thenReturn(1);

        this.mockMvc
                .perform(delete("/evidence/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Successfully Deleted Test Evidence"));
    }

    /**
     * Asserts that the you can't delete a non-existent piece of evidence
     */
    @Test
    void givenNoEvidence_whenDeleteEvidenceCalled_thenEvidenceDeletedSuccessfully() throws Exception {
        when(evidenceService.deleteEvidence(1)).thenReturn("Successfully Deleted Test Evidence");
        when(evidenceService.getEvidence(1)).thenThrow(new IncorrectDetailsException("Evidence doesn't exist"));
        when(PrincipalUtils.getUserId(any())).thenReturn(1);

        this.mockMvc
                .perform(delete("/evidence/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Evidence doesn't exist"));
    }

    /**
     * Asserts that given an evidence ID when deleteEvidence called then user is redirected to the evidence page with
     * correct message
     */
    @Test
    void givenCorrectEvidenceID_whenDeleteEvidenceCalled_thenEvidenceDeletedSuccessfully() throws Exception {
        when(evidenceService.deleteEvidence(99)).thenReturn("Successfully Deleted Test Evidence");
        when(evidenceService.getEvidence(99)).thenReturn(evidence);

        this.mockMvc
                .perform(delete("/evidence/99"))
                .andExpect(status().isOk())
                .andExpect(content().string("Successfully Deleted Test Evidence"));
    }

    /**
     * Asserts that given a user ID when evidence list is requested then a correct list of evidence is returned
     */
    @Test
    void givenAUserExists_whenEvidenceListIsRequested_thenCorrectModelViewObjectReturned() throws Exception {
        UserResponse user = createTestUserResponse(1).addRoles(UserRole.COURSE_ADMINISTRATOR).build();
        when(userAccountClientService.getUser(any())).thenReturn(user);
        when(evidenceService.getEvidenceByUserId(1)).thenReturn(List.of(evidence, evidence1));
        when(PrincipalUtils.getUserId(any())).thenReturn(1);

        this.mockMvc
                .perform(get("/user/1/evidence/getEvidenceList"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("listEvidence", List.of(evidence, evidence1)))
                .andExpect(model().attribute("isCurrentUserEvidence", user.getId()==1));
    }

    /**
     * Tests that the user is added to the list of editing users when they start editing
     */
    @Test
    void givenUserDecidesToEditAnEvidence_whenEvidenceListIsRequested_thenNotificationIsPresent() throws Exception {
        evidence.setEvidenceId(1);
        UserResponse user = createTestUserResponse(99).addRoles(UserRole.COURSE_ADMINISTRATOR).build();
        when(userAccountClientService.getUser(any())).thenReturn(user);
        when(evidenceService.getEvidenceByUserId(99)).thenReturn(List.of(evidence, evidence1));

        EvidenceNotification evidenceNotification = new EvidenceNotification(evidence.getEvidenceId(), 99, user.getUsername(), true, "testing");

        HashMap<Integer, EvidenceNotification> expectedNotifications = new HashMap<>();

        expectedNotifications.put(evidence.getEvidenceId(),evidenceNotification );

        when(mockedWebSocketPrincipal.getName()).thenReturn("tes2");

        evidenceController.editing(evidenceNotification, mockedWebSocketPrincipal, "testing");

        this.mockMvc
                .perform(get("/user/99/evidence/getEvidenceList"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("notifications", expectedNotifications));
    }

    /**
     * Tests that the user is removed to the list of editing users when they finish editing
     */
    @Test
    void givenUserDecidesToStopEditAnEvidence_whenEvidenceListIsRequested_thenNotificationIsNotPresent() throws Exception {
        evidence.setEvidenceId(1);
        UserResponse user = createTestUserResponse(99).addRoles(UserRole.COURSE_ADMINISTRATOR).build();
        when(userAccountClientService.getUser(any())).thenReturn(user);
        when(evidenceService.getEvidenceByUserId(99)).thenReturn(List.of(evidence, evidence1));

        EvidenceNotification evidenceNotificationStart = new EvidenceNotification(evidence.getEvidenceId(), 99, user.getUsername(), true, "testing");
        EvidenceNotification evidenceNotificationEnd = new EvidenceNotification(evidence.getEvidenceId(), 99, user.getUsername(), false, "testing");

        HashMap<Integer, EvidenceNotification> expectedNotifications = new HashMap<>();

        when(mockedWebSocketPrincipal.getName()).thenReturn("tes2");

        evidenceController.editing(evidenceNotificationStart, mockedWebSocketPrincipal, "testing");
        evidenceController.editing(evidenceNotificationEnd, mockedWebSocketPrincipal, "testing");

        this.mockMvc
                .perform(get("/user/99/evidence/getEvidenceList"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("notifications", expectedNotifications));
    }

    /**
     * Check that the user is removed from the list of editing users when they are disconnected
     */
    @Test
    void givenAUserIsEditing_whenDisconnectEvent_thenUserIsNotEditing() throws Exception {
        evidence.setEvidenceId(1);
        UserResponse user = createTestUserResponse(99).addRoles(UserRole.COURSE_ADMINISTRATOR).build();
        when(userAccountClientService.getUser(any())).thenReturn(user);

        EvidenceNotification evidenceNotification = new EvidenceNotification(evidence.getEvidenceId(), 99, user.getUsername(), true, "testing");

        HashMap<Integer, EvidenceNotification> expectedNotifications = new HashMap<>();

        StompSubProtocolHandler testSource = new StompSubProtocolHandler();
        GenericMessage<byte[]> testMessage = new GenericMessage<byte[]>(HexFormat.of().parseHex("FF"));

        SessionDisconnectEvent disconnectEvent = new SessionDisconnectEvent(testSource, testMessage, "0", CloseStatus.TLS_HANDSHAKE_FAILURE);

        when(mockedWebSocketPrincipal.getName()).thenReturn("tes2");

        evidenceController.editing(evidenceNotification, mockedWebSocketPrincipal, "0");

        evidenceController.onApplicationEvent(disconnectEvent);

        this.mockMvc
                .perform(get("/user/99/evidence/getEvidenceList"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("notifications", expectedNotifications));
    }

    @AfterAll
    public static void afterAll() {
        utilities.close();
    }

}
