package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Evidence;
import nz.ac.canterbury.seng302.portfolio.model.EvidenceRepository;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.User;
import nz.ac.canterbury.seng302.portfolio.utils.IncorrectDetailsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.persistence.PersistenceException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Test class for the functionality in the evidence service class
 */
@SpringBootTest
class EvidenceServiceTest {
    @MockBean
    private EvidenceRepository evidenceRepository;
    private EvidenceService evidenceService;
    private final Evidence.Builder evidenceBuilder = new Evidence.Builder();
    private Evidence evidence1;
    private Project project;

    /**
     * Initialises evidence and project objects
     */
    @BeforeEach
    public void setup() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 1);
        project = new Project.Builder()
                .projectName("Project 2020")
                .description("First Attempt")
                .startDate(new java.sql.Date(Calendar.getInstance().getTimeInMillis()))
                .endDate(new java.sql.Date(c.getTimeInMillis()))
                .build();

        evidence1 = evidenceBuilder.evidenceId(999)
                .title("New evidence")
                .description("This is a new piece of evidence")
                .dateOccurred(new Date())
                .project(project)
                .ownerId(999)
                .build();

        evidenceService = new EvidenceService(evidenceRepository);

    }

    /**
     * Asserts that the correct exception is thrown when verify evidence is called when given a null value
     */
    @Test
    void givenEvidenceIsNull_whenVerifyEvent_thenCorrectExceptionIsThrown() {
        IncorrectDetailsException exception = assertThrows(IncorrectDetailsException.class, () ->
                evidenceService.verifyEvidence(null));

        assertEquals("No evidence to verify", exception.getMessage());
    }

    /**
     * Asserts that the correct exception is thrown when verify evidence is called when given an object with null attributes
     */
    @Test
    void givenEvidenceContainsNull_whenVerifyEvent_thenCorrectExceptionIsThrown() {
        Evidence incorrectEvidence = evidenceBuilder
                .evidenceId(99)
                .title(null)
                .description(null)
                .dateOccurred(null)
                .project(null)
                .ownerId(1)
                .build();
        IncorrectDetailsException exception = assertThrows(IncorrectDetailsException.class, () ->
                evidenceService.verifyEvidence(incorrectEvidence));

        assertEquals("Evidence values are null", exception.getMessage());
    }

    /**
     * Asserts that the correct exception is thrown by verifyEvidence when given evidence with an empty title
     */
    @Test
    void givenEvidenceTitleIsEmpty_whenVerifyEvent_thenCorrectExceptionIsThrown() {
        evidence1.setTitle("");
        IncorrectDetailsException exception = assertThrows(IncorrectDetailsException.class, () ->
                evidenceService.verifyEvidence(evidence1));

        assertEquals("Evidence title must not be empty", exception.getMessage());
    }

    /**
     * Asserts that the correct exception is thrown by verifyEvidence when given evidence with a too long title
     */
    @Test
    void givenEvidenceTitleIsTooLong_whenVerifyEvent_thenCorrectExceptionIsThrown() {
        evidence1.setTitle("This is a too long title for a piece of evidence, This is a too long title for a piece of evidence");
        IncorrectDetailsException exception = assertThrows(IncorrectDetailsException.class, () ->
                evidenceService.verifyEvidence(evidence1));

        assertEquals("Evidence title cannot be more than 50 characters", exception.getMessage());
    }

    /**
     * Asserts that the correct exception is thrown by verifyEvidence when given evidence with a date too early
     */
    @Test
    void givenEvidenceDateTooEarly_whenVerifyEvent_thenCorrectExceptionIsThrown() {
        evidence1.setDateOccurred(new Date(0));
        IncorrectDetailsException exception = assertThrows(IncorrectDetailsException.class, () ->
                evidenceService.verifyEvidence(evidence1));

        assertEquals("The evidence cannot exist before the project date", exception.getMessage());
    }

    /**
     * Asserts that the correct exception is thrown by verifyEvidence when given evidence with a date too late
     */
    @Test
    void givenEvidenceDateTooLate_whenVerifyEvent_thenCorrectExceptionIsThrown() {
        evidence1.setDateOccurred(new Date(3025, Calendar.MARCH, 2));
        IncorrectDetailsException exception = assertThrows(IncorrectDetailsException.class, () ->
                evidenceService.verifyEvidence(evidence1));

        assertEquals("The evidence cannot exist after the project date", exception.getMessage());
    }

    /**
     * Asserts that the no exception is thrown by verifyEvidence when given evidence is a valid object
     */
    @Test
    void givenCorrectEvidence_whenVerifyEvent_thenNoExceptionIsThrown() throws IncorrectDetailsException {
        evidenceService.verifyEvidence(evidence1);
    }

    /**
     * Asserts that the correct exception is thrown when getEvidence() called with an incorrect ID
     */
    @Test
    void givenIncorrectId_whenGetEvidenceCalled_thenExceptionIsThrown() {
        when(evidenceRepository.getEvidenceByEvidenceId(999)).thenReturn(null);
        IncorrectDetailsException exception = assertThrows(IncorrectDetailsException.class, () ->
                evidenceService.getEvidence(999));

        assertEquals("Failed to locate the piece of evidence with ID: 999", exception.getMessage());

    }

    /**
     * Asserts that the evidence object is returned correctly with a correctly given ID
     * @throws IncorrectDetailsException If the evidence object can't be found
     */
    @Test
    void givenCorrectId_whenGetEvidenceCalled_thenEvidenceIsReturned() throws IncorrectDetailsException {
        when(evidenceRepository.getEvidenceByEvidenceId(999)).thenReturn(evidence1);
        Evidence testEvidence = evidenceService.getEvidence(999);
        assertEquals(evidence1, testEvidence);
    }

    /**
     * Asserts that when getNewEvidence is called then a new evidence object with correct information is returned
     */
    @Test
    void given_whenGetNewEvidenceIsCalled_thenNewEvidenceObjectReturned() {
        User user = new User.Builder().userId(999).build();
        Evidence expectedEvidence = new Evidence.Builder()
                .evidenceId(999)
                .project(project)
                .dateOccurred(new Date())
                .title("New evidence")
                .description("A new piece of evidence")
                .ownerId(999)
                .build();
        Evidence testEvidence = evidenceService.getNewEvidence(user, project);
        assertEquals(testEvidence, expectedEvidence);
    }

    /**
     * Asserts that a list of evidence is returned when getEvidenceByUserId is called
     */
    @Test
    void givenEvidenceWithUser_whenGetEvidenceByUserIdCalled_thenCorrectEvidenceReturned() {
        List<Evidence> expectedEvidence = List.of(evidence1);
        when(evidenceRepository.getAllByOwnerIdEquals(999)).thenReturn(expectedEvidence);
        List<Evidence> listEvidence = evidenceService.getEvidenceByUserId(999);
        assertArrayEquals(listEvidence.toArray(), expectedEvidence.toArray());
    }

    /**
     * Asserts that correct exception is thrown when saveEvidence is called with an incorrect evidence object
     */
    @Test
    void givenIncorrectEvidence_whenSaveEvidenceCalled_thenThrowCorrectException() {
        when(evidenceRepository.save(evidence1)).thenThrow(new PersistenceException("This is a test"));
        IncorrectDetailsException exception = assertThrows(IncorrectDetailsException.class, () ->
                evidenceService.saveEvidence(evidence1));
        assertEquals("Failure Saving Evidence", exception.getMessage());

    }

    /**
     * Asserts that success string is returned when saveEvidence is called with a correct evidence object
     * @throws IncorrectDetailsException If there is an error saving the evidence
     */
    @Test
    void givenCorrectEvidence_whenSaveEvidenceCalled_thenStringReturned() throws IncorrectDetailsException {
        String success = evidenceService.saveEvidence(evidence1);
        assertEquals("Successfully Created " + evidence1.getTitle(), success);

    }

}