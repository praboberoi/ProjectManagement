package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Deadline;
import nz.ac.canterbury.seng302.portfolio.model.DeadlineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Client service used to communicate to the database and perform business logic for Deadlines
 */
@Service
public class DeadlineService {
    @Autowired private DeadlineRepository deadlineRepository;

    /**
     * Creates a new deadline
     * @return of type Deadline
     */
    public Deadline getNewDeadline() {
        return new Deadline();
    }

    /**
     * Gets deadline object from the database
     * @param deadlineId of type int
     * @return of type Deadline
     * @throws IncorrectDetailsException if given Deadline ID does not exist
     */
    public Deadline getDeadline(int deadlineId) throws IncorrectDetailsException{
        Optional<Deadline> result = deadlineRepository.findById(deadlineId);
        if(result.isPresent())
            return result.get();
        else
            throw new IncorrectDetailsException("Failed to locate deadline in the database");
    }

    public void deleteDeadline(int deadlineId) {
        deadlineRepository.deleteById(deadlineId);
    }

}
