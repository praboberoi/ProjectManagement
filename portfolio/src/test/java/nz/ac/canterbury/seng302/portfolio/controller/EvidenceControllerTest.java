package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Evidence;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.dto.EvidenceDTO;
import nz.ac.canterbury.seng302.portfolio.service.EvidenceService;
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

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = EvidenceController.class)
@AutoConfigureMockMvc(addFilters = false)
public class EvidenceControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EvidenceService evidenceService;

    @MockBean
    private UserAccountClientService userAccountClientService;

    private EvidenceDTO evidenceDTO;
    private EvidenceDTO evidenceDTO1;
    private Project project;

    private static MockedStatic<PrincipalUtils> utilities;

    @BeforeAll
    private static void beforeAllInit() {
        utilities = Mockito.mockStatic(PrincipalUtils.class);
        utilities.when(() -> PrincipalUtils.checkUserIsTeacherOrAdmin(any())).thenReturn(true);
    }

    @BeforeEach
    public void init() {
        LocalDate now = LocalDate.now();
        project = new Project(1, "Test Project", "test", java.sql.Date.valueOf(now), java.sql.Date.valueOf(now.plusDays(50)));

        evidenceDTO = new EvidenceDTO.Builder()
            .title("New Evidence")
            .description("New piece of evidence")
            .dateOccurred(java.sql.Date.valueOf(now))
            .ownerId(1)
            .project(project)
            .build();

        evidenceDTO1 = new EvidenceDTO.Builder()
            .title("Another Evidence")
            .description("Additional piece of evidence")
            .dateOccurred(java.sql.Date.valueOf(now))
            .ownerId(1)
            .project(project)
            .build();

    }

    /**
     * Test verification of evidence object when a valid evidence is saved and checks it redirects the user
     */
    @Test
    void givenServer_WhenSaveValidEvidence_ThenEvidenceVerifiedSuccessfully() throws Exception {
        Evidence evidence = new Evidence(evidenceDTO);
        when(evidenceService.saveEvidence(evidence)).thenReturn("Successfully Created " + evidenceDTO.getTitle());

        this.mockMvc
                .perform(post("/evidence/save").flashAttr("evidenceDTO", evidenceDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("messageDanger", nullValue()))
                .andExpect(flash().attribute("messageSuccess", "Successfully Created " + evidenceDTO.getTitle()));


    }

    /**
     * Tests verification of evidence object when an invalid evidence is saved and checks it redirects the user
     */
    @Test
    void givenServer_WhenSaveInvalidEvidence_ThenEvidenceVerifiedSuccessfully() throws Exception {
        Evidence evidence1 = new Evidence(evidenceDTO1);
        when(evidenceService.saveEvidence(evidence1)).thenThrow(new IncorrectDetailsException("Failure Saving Evidence"));

        this.mockMvc
                .perform(post("/evidence/save").flashAttr("evidenceDTO", evidenceDTO1))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("messageDanger", "Failure Saving Evidence"))
                .andExpect(flash().attribute("messageSuccess", nullValue()));

    }

//    @Test
//    void givenServer_whenEvidenceEditForm_thenEvidenceCorrectlyRetrieved() throws IncorrectDetailsException {
//        Evidence evidence1 = new Evidence(evidenceDTO);
//        when(evidenceService.getEvidence(evidence1.getEvidenceId())).thenReturn(evidence1);
//
//        this.mockMvc
//                .perform(get("/editEvidence/" + evidence1.getEvidenceId()).flashAttr())
//    }

    @AfterAll
    public static void afterAll() {
        utilities.close();
    }

}
