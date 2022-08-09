package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.PersistenceException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;

/**
 * Client service used to communicate to the database and perform business logic for Deadlines
 */
@Service
public class DeadlineService {
    @Autowired private DeadlineRepository deadlineRepository;
    @Autowired private ProjectRepository projectRepository;
    private Logger logger = LoggerFactory.getLogger(DeadlineService.class);


    public DeadlineService(DeadlineRepository deadlineRepository, ProjectRepository projectRepository) {
        this.deadlineRepository = deadlineRepository;
        this.projectRepository = projectRepository;
    }
    /**
     * Creates a new deadline
     * @return of type Deadline
     */
    public Deadline getNewDeadline(Project project) {
        LocalDate now = LocalDate.now();
        Deadline newDeadline = new Deadline.Builder()
                .project(project)
                .name("New Deadline")
                .date(java.sql.Date.valueOf(now))
                .build();
        return newDeadline;
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

    /**
     * Returns a list of deadlines that are related to the given project ID
     * @param projectId of type int
     * @return a list of deadlines from a project specified by its Id.
     */
    public List<Deadline> getDeadlineByProject(int projectId) {
        Optional<Project> current = projectRepository.findById(projectId);
        return current.map(project -> deadlineRepository
                .findByProject(project)
                .stream()
                .sorted(Comparator.comparing(Deadline::getDate))
                .collect(Collectors.toList()))
            .orElse(List.of());
    }

    /**
     * Deletes deadline object from the database
     * @param deadlineId of type int
     * @return Message of type String
     * @throws IncorrectDetailsException if unable to delete the deadline
     */
    public String deleteDeadline(int deadlineId) throws IncorrectDetailsException {
        try {
            Optional<Deadline> deadline = deadlineRepository.findById(deadlineId);
            if(deadline.isPresent()) {
                deadlineRepository.deleteById(deadlineId);
                return "Successfully deleted " + deadline.get().getName();
            }
            else
                throw new IncorrectDetailsException("Could not find given Deadline");


        } catch (PersistenceException e) {
            logger.error("Failure deleting Deadline", e);
            throw new IncorrectDetailsException("Failure deleting Deadline");
        }
    }

    /**
     * Saves a deadline object to the database
     * @param deadline of type Deadline
     * @return message  based on whether it is a new deadline or existing deadline being updated
     * @throws IncorrectDetailsException if unable to save a deadline
     */
    public String saveDeadline(Deadline deadline) throws IncorrectDetailsException {
        String message;
        try {
            if (deadline.getDeadlineId() == 0)
                message = "Successfully Created " + deadline.getName();
            else
                message = "Successfully Updated " + deadline.getName();
            deadlineRepository.save(deadline);
            return message;
        } catch (PersistenceException e) {
            logger.error("Failure to save the deadline", e);
            throw new IncorrectDetailsException("Failure to save the deadline");
        }
    }

    /**
     * Verifies the current deadline name and date
     * @param deadline The deadline object to verify
     * @throws IncorrectDetailsException raised if deadline values are invalid
     */
    public void verifyDeadline(Deadline deadline) throws IncorrectDetailsException {
        if (deadline == null) {
            throw new IncorrectDetailsException("No deadline");
        } else if (deadline.getName() == null || deadline.getDate() == null || deadline.getProject() == null) {
            throw new IncorrectDetailsException("Deadline values cannot be null");
        } else if (deadline.getName().startsWith(" ") || deadline.getName().endsWith(" ")) {
            throw new IncorrectDetailsException("Deadline name must not start or end with space characters");
        } else if (deadline.getName().length() > 50) {
            throw new IncorrectDetailsException("Deadline name cannot exceed 50 characters");
        } else if (deadline.getName().length() < 3) {
            throw new IncorrectDetailsException("Deadline name must be at least 3 characters");
        } else if (deadline.getDate().after(deadline.getProject().getEndDate())) {
            throw new IncorrectDetailsException("Deadline date cannot be after project end date");
        } else if (deadline.getDate().before(deadline.getProject().getStartDate())) {
            throw new IncorrectDetailsException("Deadline date cannot be before project start date");
        }
    }

}
