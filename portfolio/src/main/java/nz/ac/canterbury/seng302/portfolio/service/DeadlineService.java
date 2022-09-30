package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.*;
import nz.ac.canterbury.seng302.portfolio.utils.IncorrectDetailsException;
import nz.ac.canterbury.seng302.portfolio.utils.SprintColor;
import nz.ac.canterbury.seng302.portfolio.utils.ValidationUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.PersistenceException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Client service used to communicate to the database and perform business logic
 * for Deadlines
 */
@Service
public class DeadlineService {
    @Autowired
    private DeadlineRepository deadlineRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private SprintRepository sprintRepository;
    private Logger logger = LoggerFactory.getLogger(DeadlineService.class);

    public DeadlineService(DeadlineRepository deadlineRepository, ProjectRepository projectRepository,
            SprintRepository sprintRepository) {
        this.deadlineRepository = deadlineRepository;
        this.sprintRepository = sprintRepository;
        this.projectRepository = projectRepository;
    }
    /**
     * Creates a new deadline
     * @param project Project of the deadline
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
     * Creates a mapping between a deadline and the name of the sprint that occurs
     * on the deadline
     *
     * @param deadlineList A list of deadlines to create mappings for
     * @return A mapping between the deadline id and the name of the sprint
     *         occurring on the deadline
     */
    public Map<Integer, String> getSprintOccurringOnDeadlines(List<Deadline> deadlineList) {
        HashMap<Integer, String> deadlineDateMapping = new HashMap<>();
        for (Deadline deadline : deadlineList) {
            List<String> sprintNames = new ArrayList<>();
            java.sql.Date date = new Date(deadline.getDate().getTime());
            Sprint sprint = sprintRepository.findByDateAndProject(deadline.getProject(), date);
            if (sprint == null) {
                sprintNames.add("(No Sprint)");
            } else {
                sprintNames.add("(" + sprint.getSprintLabel() + ")");
            }
            deadlineDateMapping.put(deadline.getDeadlineId(), sprintNames.get(0));
        }
        return deadlineDateMapping;
    }

    /**
     * Gets deadline object from the database
     *
     * @param deadlineId of type int
     * @return of type Deadline
     * @throws IncorrectDetailsException if given Deadline ID does not exist
     */
    public Deadline getDeadline(int deadlineId) throws IncorrectDetailsException {
        Optional<Deadline> result = deadlineRepository.findById(deadlineId);
        if (result.isPresent())
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
     * Returns a list of deadlines that occur within the given sprint related to the
     * sprint ID.
     *
     * @param sprintId The id of the sprint (int).
     * @return A list of deadlines from a sprint specified by its id.
     */
    public List<Deadline> getDeadlinesBySprintId(int sprintId) {
        Optional<Sprint> current = sprintRepository.findById(sprintId);
        return current.map(sprint -> deadlineRepository.findDeadlinesBySprint(sprint).stream()
                .sorted(Comparator.comparing(Deadline::getDate)).toList()).orElse(List.of());
    }

    /**
     * Deletes deadline object from the database
     *
     * @param deadlineId of type int
     * @return Message of type String
     * @throws IncorrectDetailsException if unable to delete the deadline
     */
    public String deleteDeadline(int deadlineId) throws IncorrectDetailsException {
        try {
            Optional<Deadline> deadline = deadlineRepository.findById(deadlineId);
            if (deadline.isPresent()) {
                deadlineRepository.deleteById(deadlineId);
                return "Successfully deleted " + deadline.get().getName();
            } else {
                throw new IncorrectDetailsException("Could not find given Deadline");
            }
        } catch (PersistenceException e) {
            logger.error("Failure deleting Deadline", e);
            throw new IncorrectDetailsException("Failure deleting Deadline");
        }
    }

    /**
     * Saves a deadline object to the database
     *
     * @param deadline of type Deadline
     * @return message based on whether it is a new deadline or existing deadline
     *         being updated
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
            logger.error("Failed to save the deadline", e);
            throw new IncorrectDetailsException("Failed to save the deadline");
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
        } else if (deadline.getName().length() > 50) {
            throw new IncorrectDetailsException("Deadline name cannot exceed 50 characters");
        } else if (deadline.getName().length() < 3) {
            throw new IncorrectDetailsException("Deadline name must be at least 3 characters");
        } else if (deadline.getDate().after(deadline.getProject().getEndDate())) {
            throw new IncorrectDetailsException("Deadline date cannot be after project end date");
        } else if (deadline.getDate().before(deadline.getProject().getStartDate())) {
            throw new IncorrectDetailsException("Deadline date cannot be before project start date");
        } else if (ValidationUtilities.hasEmoji(deadline.getName())) {
            throw new IncorrectDetailsException("Deadline name must not contain an emoji");
        }

    }

    /**
     * Updates the colours for the given deadline
     * @param deadline of type deadline
     */
    public void updateDeadlineColors(Deadline deadline) {
        Optional<Sprint> sprint = sprintRepository.findSprintsByDeadline(deadline);
        sprint.ifPresent(value -> deadline.setColor(value.getColor()));
    }



}
