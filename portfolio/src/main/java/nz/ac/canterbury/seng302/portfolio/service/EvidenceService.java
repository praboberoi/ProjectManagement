package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.controller.EventController;
import nz.ac.canterbury.seng302.portfolio.model.EventRepository;
import nz.ac.canterbury.seng302.portfolio.utils.IncorrectDetailsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * Client service used to communicate to the database and perform business logic for evidence
 */
@Service
public class EvidenceService {
    @Autowired
    private EvidenceRepository evidenceRepository;
    @Autowired
    private UserAccountClientService userAccountClientService;
    private Logger logger = LoggerFactory.getLogger(EventController.class);

    public EventService(EvidenceRepository evidenceRepository, UserAccountClientService userAccountClientService) {
        this.evidenceRepository = evidenceRepository;
        this.userAccountClientService = userAccountClientService;
    }

    /**
     * Returs an evidence object from the database. If the evidence is not present then it throws an exception.
     * @param evidenceId The unique id (integer) of the requested evidence.
     * @return Evidence object associated with the given ID
     * @throws IncorrectDetailsException If null value is returned by {@link EvidenceRepository#getEvidenceByEvidenceId(Object) findById}
     */
    public Evidence getEvidence(int evidenceId) throws IncorrectDetailsException {
        Evidence result = evidenceRepository.getEvidenceByEvidenceId(evidenceId);
        if (result != null)
            return result;
        else {
            logger.error("Failure locating evidence with ID:" + evidenceId);
            throw new IncorrectDetailsException("Failed to locate the piece of evidence with ID: " + evidenceId);
        }
    }

    public Evidence getNewEvidence(Evidence evidence) {
        LocalDate now = LocalDate.now();
        Evidence newEvidence = new Evidence.Builder()
                .dateOccured(now)
                .title("New evidence")
    }
}
