package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Event;
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
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = EvidenceController.class)
@AutoConfigureMockMvc(addFilters = false)
public class EvidenceControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private EvidenceService evidenceService;

    private static MockedStatic<PrincipalUtils> utilities;

    @BeforeAll
    private static void beforeAllInit() {

    }

    @BeforeEach
    public void init() {

    }

    /**
     * Test verification of evidence object and check it redirect the user
     */
    @Test
    void givenServer_WhenSaveValidEvidence_ThenEvidenceVerifiedSuccessfully() {


    }
}
