package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Evidence;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.dto.EvidenceDTO;
import nz.ac.canterbury.seng302.portfolio.service.EvidenceService;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.portfolio.utils.IncorrectDetailsException;
import nz.ac.canterbury.seng302.portfolio.utils.PrincipalUtils;
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
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.mockito.Mockito.when;
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

    @MockBean
    private UserAccountClientService userAccountClientService;

    private Evidence evidence;
    private Evidence evidence1;

    private static MockedStatic<PrincipalUtils> utilities;

    @BeforeAll
    private static void beforeAllInit() {
        utilities = Mockito.mockStatic(PrincipalUtils.class);
        utilities.when(() -> PrincipalUtils.checkUserIsTeacherOrAdmin(any())).thenReturn(true);
    }

    @BeforeEach
    public void setup() {
        LocalDate now = LocalDate.now();
        Project project = new Project(1, "Test Project", "test", java.sql.Date.valueOf(now), java.sql.Date.valueOf(now.plusDays(50)));

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
                .perform(post("/evidence/99/saveEvidence").flashAttr("evidenceDTO", toDTO(evidence)))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("messageDanger", nullValue()))
                .andExpect(flash().attribute("messageSuccess", "Successfully Created " + evidence.getTitle()));
    }

    /**
     * Tests verification of evidence object when an invalid evidence is saved and checks it redirects the user
     */
    @Test
    void givenServer_whenSaveInvalidEvidence_thenEvidenceVerifiedSuccessfully() throws Exception {
        when(evidenceService.saveEvidence(any())).thenThrow(new IncorrectDetailsException("Failure Saving Evidence"));
        when(PrincipalUtils.getUserId(any())).thenReturn(99);
        this.mockMvc
                .perform(post("/evidence/99/saveEvidence").flashAttr("evidenceDTO", toDTO(evidence1)))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("messageDanger", "Failure Saving Evidence"))
                .andExpect(flash().attribute("messageSuccess", nullValue()));

    }

    /**
     * Asserts that when a user is attempting to create an evidence object under another users page, the correct error message is displayed
     * @throws Exception when userId doesn't exist
     */
    @Test
    void givenEvidenceObjectAndIncorrectUser_whenSaveEvidenceCalled_thenEvidenceSavedCorrectly() throws Exception {
        when(PrincipalUtils.getUserId(any())).thenReturn(53);
        this.mockMvc
                .perform(post("/evidence/99/saveEvidence").flashAttr("evidenceDTO", toDTO(evidence)))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("messageDanger","You may only create evidence on your own evidence page" ));

    }


    /**
     * Tests that when EvidenceList is called then the list of evidence is correctly added to the ModelAndView return object
     * @throws Exception when userId doesn't exist
     */
    @Test
    void givenEvidenceObject_whenEvidenceListCalled_thenCorrectModelViewObjectReturned() throws Exception {
        ArrayList<Project> projectList = new ArrayList<>();
        when(projectService.getAllProjects()).thenReturn(projectList);
        when(evidenceService.getEvidenceByUserId(99)).thenReturn(List.of(evidence, evidence1));
        when(evidenceService.getNewEvidence(99)).thenReturn(evidence);

        this.mockMvc
                .perform(get("/evidence/99"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attribute("evidence", evidence))
                .andExpect(model().attribute("listEvidence", List.of(evidence, evidence1)))
                .andExpect(model().attribute("listProjects", projectList))
                .andExpect(model().attribute("userId", 99));

    }

    /**
     * Asserts that given an evidence ID and a user ID when selectedEvidence called then returns a list of all evidence and the selected evidence
     * @throws Exception when userId doesn't exist
     */
    @Test
    void givenCorrectEvidenceAndUserIds_whenSelectedEvidenceCalled_thenReturnSelectedEvidence() throws Exception {
        when(evidenceService.getEvidenceByUserId(99)).thenReturn(List.of(evidence, evidence1));
        when(evidenceService.getEvidence(33)).thenReturn(evidence);

        this.mockMvc
                .perform(get("/evidence/99/33"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attribute("listEvidence", List.of(evidence, evidence1)))
                .andExpect(model().attribute("selectedEvidence", evidence));
    }

    /**
     * Asserts that no evidence is selected when a given evidence ID does not exist
     * @throws Exception when userId doesn't exist
     */
    @Test
    void givenIncorrectEvidence_whenSelectedEvidenceCalled_thenNoEvidenceSelected() throws Exception {
        when(evidenceService.getEvidenceByUserId(99)).thenReturn(List.of(evidence, evidence1));
        when(evidenceService.getEvidence(33)).thenThrow(new IncorrectDetailsException("Failed to locate the piece of evidence with ID: 33"));

        this.mockMvc
                .perform(get("/evidence/99/33"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attribute("listEvidence", List.of(evidence, evidence1)))
                .andExpect(model().attributeDoesNotExist("selectedEvidence"));
    }


    @AfterAll
    public static void afterAll() {
        utilities.close();
    }

}
