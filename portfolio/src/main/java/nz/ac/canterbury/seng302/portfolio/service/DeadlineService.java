package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.PersistenceException;
import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;
import org.slf4j.Logger;

/**
 * Client service used to communicate to the database and perform business logic for Deadlines
 */
@Service
public class DeadlineService {
    @Autowired private DeadlineRepository deadlineRepository;
    @Autowired private ProjectRepository projectRepository;
    private Logger logger = LoggerFactory.getLogger(DeadlineService.class);

    /**
     * Creates a new deadline
     * @return of type Deadline
     */
    public Deadline getNewDeadline() {return new Deadline();}

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

    /**
     * Returns a list of deadlines that are related to the given project ID
     * @param projectId of type int
     * @return a list of deadlines from a project specified by its Id.
     */
    public List<Deadline> getDeadlineByProject(int projectId) {
        Optional<Project> current = projectRepository.findById(projectId);
        return current.map(project -> deadlineRepository.findByProject(project)).orElse(List.of());
    }

    /**
     * Deletes deadline object from the database
     * @param deadlineId
     */
    public String deleteDeadline(int deadlineId) throws IncorrectDetailsException, PersistenceException {
        try {
            Optional<Deadline> deadline = deadlineRepository.findById(deadlineId);
            if(deadline.isPresent()) {
                deadlineRepository.deleteById(deadlineId);
                return "Successfully deleted " + deadline.get().getName();
            }
            else {
                throw new IncorrectDetailsException("Could not find given Deadline");
            }

        } catch (PersistenceException e) {
            logger.error("Failure deleting Deadline", e);
            throw new IncorrectDetailsException("Failure deleting Deadline");
        }
    }

    /**
     * Saves a deadline object to the database
     * @param deadline of type Deadline
     * @return message  based on whether it is a new deadline or existing deadline being updated
     * @throws PersistenceException
     */
    public String saveDeadline(Deadline deadline) throws PersistenceException, Exception {
        try {
            String message;
            if (deadline.getDeadlineId() == 0)
                message = "Successfully Created " + deadline.getName();
            else
                message = "Successfully Updated " + deadline.getName();
            deadlineRepository.save(deadline);
            return message;
        } catch (Exception e) {
            throw new Exception("Internal server error");
        }

    }

    /**
     * Verifies the current deadline name and date
     * @param deadline The deadline object to verify
     * @throws IncorrectDetailsException raised if deadline values are invalid
     */
    public void verifyDeadline(Deadline deadline) throws Exception, IncorrectDetailsException {
        try {
            if (deadline == null) {
                throw new IncorrectDetailsException("No deadline");
            } else if (deadline.getName() == null || deadline.getDate() == null || deadline.getTime() == null) {
                throw new IncorrectDetailsException("Deadline values cannot be null");
            } else if (deadline.getName().matches("^[A-Za-z0-9]+(?: +[A-Za-z0-9]+)*$")) {
                throw new IncorrectDetailsException("Deadline name must not start or end with space characters");
            } else if (deadline.getDate().after(deadline.getProject().getEndDate())) {
                throw new IncorrectDetailsException("Deadline date cannot be after project end date");
            } else if (deadline.getDate().before(deadline.getProject().getStartDate())) {
                throw new IncorrectDetailsException("Deadline date cannot be before project start date");
            }
        } catch(Exception e) {
            throw new Exception("Internal Server Error");
        }

    }

}
