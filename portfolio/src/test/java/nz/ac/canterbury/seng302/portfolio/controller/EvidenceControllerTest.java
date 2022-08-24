package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Event;
import nz.ac.canterbury.seng302.portfolio.model.Evidence;
import nz.ac.canterbury.seng302.portfolio.service.EvidenceService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.portfolio.utils.IncorrectDetailsException;
import nz.ac.canterbury.seng302.portfolio.utils.PrincipalUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.metadata.HsqlTableMetaDataProvider;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = EvidenceController.class)
@AutoConfigureMockMvc(addFilters = false)
public class EvidenceControllerTest {
    @Autowired
    private MockMvc mockMvc;

    private Evidence evidence;
    private Evidence evidence1;

    @MockBean
    private EvidenceService evidenceService;

    @MockBean
    private EvidenceController evidenceController;

    private static MockedStatic<PrincipalUtils> utilities;

    @MockBean
    private UserAccountClientService userAccountClientService;

    @BeforeAll
    private static void beforeAllInit() {

    }

    @BeforeEach
    public void init() {
        LocalDate now = LocalDate.now();
        evidence = new Evidence.Builder()
                .evidenceId(2)
            .title("New Evidence")
            .description("I am adding a new piece of evidence")
            .dateOccurred(java.sql.Date.valueOf(now))
            .ownerId(1)
            .build();

        evidence1 = new Evidence.Builder()
                .evidenceId(3)
                .title("Another Evidence")
                .description("Additional piece of evidence")
                .dateOccurred(java.sql.Date.valueOf(now))
                .ownerId(1)
                .build();

    }

    /**
     * Test verification of evidence object and checks it redirect the user
     */
    @Test
    void givenServer_WhenSaveValidEvidence_ThenEvidenceVerifiedSuccessfully() {
        try{
//            when(evidenceService.saveEvidence(evidence)).thenReturn("Successfully Created " + evidence.getTitle());
//            when(evidenceService.saveEvidence(evidence1)).thenThrow(new IncorrectDetailsException("Failure saving evidence"));

            this.mockMvc
                    .perform(post("/evidence/saveEvidence").flashAttr("evidence", evidence))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(flash().attribute("messageDanger", nullValue()))
                    .andExpect(flash().attribute("messageSuccess", "Successfully Created " + evidence.getEvidenceId()));

            this.mockMvc
                    .perform(post("/evidence/saveEvidence").flashAttr("evidence", evidence1))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(flash().attribute("messageDanger", "Failure Saving Evidence"))
                    .andExpect(flash().attribute("messageSuccess", nullValue()));

        } catch (Exception e){
            e.printStackTrace();
        }

    }
}
