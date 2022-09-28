package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.*;
import nz.ac.canterbury.seng302.portfolio.utils.IncorrectDetailsException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.persistence.PersistenceException;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
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
    void whenGetNewEvidenceIsCalled_thenNewEvidenceObjectReturned() {
        User user = new User.Builder().userId(1000).build();
        LocalDate now = LocalDate.now();
        Evidence expectedEvidence = new Evidence.Builder()
                .dateOccurred(java.sql.Date.valueOf(now))
                .title("New evidence")
                .ownerId(user.getUserId())
                .build();
        Evidence testEvidence = evidenceService.getNewEvidence(1000);
        assertEquals(testEvidence, expectedEvidence);
    }

    /**
     * Asserts that a list of evidence is returned when getEvidenceByUserId is called
     */
    @Test
    void givenEvidenceWithUser_whenGetEvidenceByUserIdCalled_thenCorrectEvidenceReturned() {
        List<Evidence> expectedEvidence = List.of(evidence1);
        when(evidenceRepository.getAllByOwnerIdEqualsOrderByDateOccurredDesc(999)).thenReturn(expectedEvidence);
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
     * Asserts that successfully updated string is returned when saveEvidence is called with a correct evidence object
     * @throws IncorrectDetailsException If there is an error saving the evidence
     */
    @Test
    void givenCorrectEvidence_whenSaveEvidenceCalled_thenUpdatedStringReturned() throws IncorrectDetailsException {
        String success = evidenceService.saveEvidence(evidence1);
        assertEquals("Successfully Updated " + evidence1.getTitle(), success);

    }

    /**
     * Asserts that successfully created string is returned when saveEvidence is called with a correct evidence object
     * @throws IncorrectDetailsException If there is an error saving the evidence
     */
    @Test
    void givenCorrectEvidence_whenSaveEvidenceCalled_thenCreatedStringReturned() throws IncorrectDetailsException {
        Evidence evidence = new Evidence.Builder()
                                .evidenceId(0)
                                .title("New evidence 1")
                                .description("This is a new piece of evidence")
                                .dateOccurred(new Date())
                                .project(project)
                                .ownerId(999)
                                .build();

        String success = evidenceService.saveEvidence(evidence);
        assertEquals("Successfully Created " + evidence.getTitle(), success);

    }

    /**
     * Test that when a piece of evidence exists, its ID is specified and delete evidence is called, then it is successfully deleted.
     * @throws IncorrectDetailsException
     */
    @Test
    void givenAPieceOfEvidence_whenDeleteEvidenceIsCalled_thenEvidenceNoLongerExists() {
        when(evidenceRepository.getEvidenceByEvidenceId(evidence1.getEvidenceId())).thenReturn(evidence1);
        String messageResponse = null;
        try {
            messageResponse = evidenceService.deleteEvidence(evidence1.getEvidenceId());
        } catch (IncorrectDetailsException e) {
            e.printStackTrace();
        }
        assertEquals("Successfully Deleted " + evidence1.getTitle(), messageResponse);
    }

    /**
     * Test that when a piece of evidence does not exist, and delete evidence is called, then an Illegal Argument exception is thrown.
     */
    @Test
    void givenNoEvidence_whenDeleteEvidenceIsCalled_thenAnExceptionIsThrown(){
        int evidenceId = evidence1.getEvidenceId();
        doThrow(new IllegalArgumentException()).when(evidenceRepository).deleteById(evidenceId);
        IncorrectDetailsException exception = assertThrows(IncorrectDetailsException.class, () ->
                evidenceService.deleteEvidence(evidenceId));
        assertEquals("Could not find an existing piece of evidence", exception.getMessage() );
    }

    /**
     * Test to check when an invalid evidence with title containing an emoji is verified an appropriate Exception is thrown
     */
    @Test
    void givenInvalidEvidenceWithEmojiInTitle_whenVerifyRequested_thenAppropriateExceptionIsThrown() {
        evidence1.setTitle("Test 😀");

        IncorrectDetailsException exception = assertThrows(IncorrectDetailsException.class, () ->
                evidenceService.verifyEvidence(evidence1));
        Assertions.assertEquals("Evidence title must not contain an emoji", exception.getMessage());
    }

    /**
     * Test to check when an invalid evidecen with description containing an emoji is verified an appropriate Exception is thrown
     */
    @Test
    void givenInvalidEvidenceWithEmojiInDescription_whenVerifyRequested_thenAppropriateExceptionIsThrown() {
        evidence1.setDescription("Test 😀");

        IncorrectDetailsException exception = assertThrows(IncorrectDetailsException.class, () ->
                evidenceService.verifyEvidence(evidence1));
        Assertions.assertEquals("Evidence description must not contain an emoji", exception.getMessage());
    }

}