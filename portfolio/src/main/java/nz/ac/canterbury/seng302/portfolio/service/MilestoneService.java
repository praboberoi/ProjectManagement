package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Milestone;
import nz.ac.canterbury.seng302.portfolio.model.MilestoneRepository;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.ProjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.PersistenceException;
import java.util.List;
import java.util.Optional;

/**
 * Client service used to communicate to the database and perform business logic for Milestones
 */
@Service
public class MilestoneService {

    @Autowired
    private MilestoneRepository milestoneRepository;
    @Autowired
    private ProjectRepository projectRepository;
    private final Logger logger = LoggerFactory.getLogger(MilestoneService.class);

    /**
     * Creates a new milestone
     * @return of type milestone
     */
    public Milestone getNewMilestone() {return new Milestone();}

    /**
     * Gets milestone object from the database
     * @param milestoneId of type int
     * @return Object of type Milestone
     * @throws IncorrectDetailsException if given milestone id does not exist
     */
    public Milestone getMilestone(int milestoneId) throws IncorrectDetailsException {
        Optional<Milestone> result = milestoneRepository.findById(milestoneId);
        if (result.isPresent())
            return result.get();
        else
            throw new IncorrectDetailsException("Failed to locate milestone in the database");
    }

    /**
     * Returns a list of milestones that are related to the given project ID
     * @param projectId of type int
     * @return  A list of milestones from a project specified by the given ID
     * @throws IncorrectDetailsException If unable to find the project with the given ID, or the project has no milestones
     */
    public List<Milestone> getMilestoneByProject(int projectId) throws IncorrectDetailsException {
        Optional<Project> current = projectRepository.findById(projectId);
        if (current.isEmpty()) {
            throw new IncorrectDetailsException("Failed to locate and project with ID: " + projectId);
        }

        List<Milestone> result = milestoneRepository.findByProject(current.get());
        if (result.isEmpty())
            throw new IncorrectDetailsException("Failed to locate any milestones under a project with ID: " + projectId);

        return result;
    }

    /**
     * Deletes a milestone object from the database
     * @param milestoneId of type int
     * @return Successful deletion message of type String
     * @throws IncorrectDetailsException If unable to find or delete the milestone
     */
    public String deleteMilestone(int milestoneId) throws IncorrectDetailsException {
        try {
            Optional<Milestone> milestone = milestoneRepository.findById(milestoneId);
            if (milestone.isEmpty()) {
                throw new IncorrectDetailsException("Could not find the given milestone");
            }

            return "Successfully deleted " + milestone.get().getName();
        } catch (PersistenceException e) {
            logger.error("Failure deleting milestone with ID " + milestoneId + ": " + e);
            throw new IncorrectDetailsException("Failure deleting Milestone");
        }
    }

    /**
     * Saves a milestone object to the database
     * @param milestone Object of type milestone
     * @return Success message of creation if it is a new milestone or updating if it is an existing milestone
     * @throws IncorrectDetailsException If unable to save the milestone
     */
    public String saveMilestone(Milestone milestone) throws IncorrectDetailsException {
        String message;
        try {
            if (milestone.getMilestoneId() == 0)
                message = "Successfully created " + milestone.getName();
            else
                message = "Successfully updated " + milestone.getName();

            milestoneRepository.save(milestone);
            return message;

        } catch (PersistenceException e) {
            logger.error("Failure to save the milestone ", e);
            throw new IncorrectDetailsException("Failure to save the milestone");
        }
    }

    /**
     * Verifies the current milestone's name, date and project
     * @param milestone The milestone object to verify
     * @throws IncorrectDetailsException Raised if the milestone values are invalid
     */
    public void verifyMilestone(Milestone milestone) throws IncorrectDetailsException {

        Project milestoneProject;

        if (milestone == null)
            throw new IncorrectDetailsException("No milestone");

        if (milestone.getName() == null || milestone.getDate() == null || milestone.getProject() == null)
            throw new IncorrectDetailsException("Milestone values cannot be null");
        else
            milestoneProject = milestone.getProject();

        if (milestone.getName().matches("^[A-Za-z\\d]+(?: +[A-Za-z\\d]+)*$"))
            throw new IncorrectDetailsException("Milestone name must not start or end with space characters");
        if (milestone.getDate().after(milestoneProject.getEndDate()))
            throw new IncorrectDetailsException("The milestone's date cannot be after the project end date");
        if (milestone.getDate().before(milestoneProject.getStartDate()))
            throw new IncorrectDetailsException("The milestone's date cannot be before the project start date");
    }
}
