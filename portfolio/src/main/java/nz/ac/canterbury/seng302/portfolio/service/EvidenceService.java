package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Evidence;
import nz.ac.canterbury.seng302.portfolio.model.EvidenceRepository;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.User;
import nz.ac.canterbury.seng302.portfolio.utils.IncorrectDetailsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.PersistenceException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;


/**
 * Client service used to communicate to the database and perform business logic for evidence
 */
@Service
public class EvidenceService {
    @Autowired
    private EvidenceRepository evidenceRepository;

    private final Logger logger = LoggerFactory.getLogger(EvidenceService.class);

    public EvidenceService(EvidenceRepository evidenceRepository) {
        this.evidenceRepository = evidenceRepository;
    }

    /**
     * Returns an evidence object from the database. If the evidence is not present then it throws an exception.
     * @param evidenceId The unique id (integer) of the requested evidence.
     * @return Evidence object associated with the given ID
     * @throws IncorrectDetailsException If null value is returned by {@link EvidenceRepository(Object) getEvidenceByEvidenceId}
     */
    public Evidence getEvidence(int evidenceId) throws IncorrectDetailsException {
        Evidence result = evidenceRepository.getEvidenceByEvidenceId(evidenceId);
        if (result != null)
            return result;
        else {
            logger.error("Failure locating evidence with ID: {}",evidenceId);
            throw new IncorrectDetailsException(String.format("Failed to locate the piece of evidence with ID: %d", evidenceId));
        }
    }

    /**
     * Creates a new evidence object
     * @param userId User ID who the evidence relates to
     * @return Evidence object
     */
    public Evidence getNewEvidence(int userId) {
        LocalDate now = LocalDate.now();
        return new Evidence.Builder()
                .dateOccurred(java.sql.Date.valueOf(now))
                .title("New evidence")
                .ownerId(userId)
                .build();
    }

    /**
     * Returns a list of evidence objects that are related to the given user
     * @param userId An ID that contains evidence objects
     * @return a list of evidence objects.
     */
    public List<Evidence> getEvidenceByUserId(int userId) {
        return evidenceRepository.getAllByOwnerIdEquals(userId);
    }

    /**
     * Verifies the evidence has correct validation
     * @param evidence The evidence object to verify
     * @throws IncorrectDetailsException Message explaining any incorrect values
     */
    public void verifyEvidence(Evidence evidence) throws IncorrectDetailsException {
        if (evidence == null)
            throw new IncorrectDetailsException ("No evidence to verify");

        else if (evidence.getDateOccurred() == null || evidence.getDescription() == null ||
                evidence.getTitle() == null || evidence.getProject() == null)
            throw new IncorrectDetailsException ("Evidence values are null");

        // Removes leading and trailing white spaces from the title
        evidence.setTitle(evidence.getTitle().strip());

        if (evidence.getTitle().length() < 1)
            throw new IncorrectDetailsException ("Evidence title must not be empty");

        else if (evidence.getTitle().length() > 50)
            throw new IncorrectDetailsException ("Evidence title cannot be more than 50 characters");

        else if (evidence.getDateOccurred().before(evidence.getProject().getStartDate()))
            throw new IncorrectDetailsException("The evidence cannot exist before the project date");

        else if (evidence.getDateOccurred().after(evidence.getProject().getEndDate()))
            throw new IncorrectDetailsException("The evidence cannot exist after the project date");

    }

    /**
     * Saves evidence into the database
     * @param evidence The evidence object to be saved
     * @throws IncorrectDetailsException If the evidence has incorrect
     */
    public String saveEvidence(Evidence evidence) throws IncorrectDetailsException {
        try {
            evidenceRepository.save(evidence);
            logger.info("Successfully created evidence {} for user with ID: {}", evidence.getEvidenceId(),evidence.getOwnerId());
            return "Successfully Created " + evidence.getTitle();
        } catch (PersistenceException e) {
            logger.error("Failure saving evidence", e);
            throw new IncorrectDetailsException("Failure Saving Evidence");
        }
    }

    /**
     * Delete a specific piece of evidence
     * @param evidenceId id of evidence to be deleted
     * @return Message of the outcome of the delete operation
     * @throws IncorrectDetailsException
     */
    public String deleteEvidence(int evidenceId) {
        try {
            evidenceRepository.deleteById(evidenceId);
            logger.info("Successfully deleted evidence {}", evidenceId);
            return "Successfully Deleted " + evidenceId;
        } catch (IllegalArgumentException ex) {
            logger.error("Failure deleting evidence");
            throw new IllegalArgumentException("Could not find an existing piece of evidence");
        }
    }
}
