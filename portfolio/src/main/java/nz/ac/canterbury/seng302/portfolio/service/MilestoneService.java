package nz.ac.canterbury.seng302.portfolio.service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import javax.persistence.PersistenceException;
import nz.ac.canterbury.seng302.portfolio.model.*;
import nz.ac.canterbury.seng302.portfolio.utils.IncorrectDetailsException;
import nz.ac.canterbury.seng302.portfolio.utils.SprintColor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Client service used to communicate to the database and perform business logic
 * for Milestones
 */
@Service
public class MilestoneService {
    @Autowired
    private MilestoneRepository milestoneRepository;
    @Autowired
    private SprintRepository sprintRepository;
    private Logger logger = LoggerFactory.getLogger(MilestoneService.class);

    public MilestoneService(MilestoneRepository milestoneRepository, SprintRepository sprintRepository) {
        this.milestoneRepository = milestoneRepository;
        this.sprintRepository = sprintRepository;
    }

    /**
     * Returns a list of milestones that are related to the given project
     * 
     * @param project The project to retrieve the milestones from
     * @return a list of milestones from a project specified by its Id.
     */
    public List<Milestone> getMilestonesByProject(Project project) {
        return milestoneRepository.findByProject(project).stream().sorted(Comparator.comparing(Milestone::getDate)).toList();
    }

    /**
     * Creates a new milestone
     * 
     * @param project Project of the milestone
     * @return of type Milestone
     */
    public Milestone getNewMilestone(Project project) {
        return new Milestone.Builder().project(project).name("New Milestone")
                .date(java.sql.Date.valueOf(LocalDate.now())).build();
    }

    /**
     * Saves a milestone object to the database
     * 
     * @param milestone of type Milestone
     * 
     * @throws IncorrectDetailsException if unable to save a milestone
     */
    public void saveMilestone(Milestone milestone) throws IncorrectDetailsException {
        try {
            milestoneRepository.save(milestone);
        } catch (PersistenceException e) {
            logger.error("Failed to save the milestone", e);
            throw new IncorrectDetailsException("Failed to save the milestone");
        }
    }

    /**
     * Deletes milestone object from the database
     *
     * @param milestoneId of type int
     * @return Message of type String
     * @throws IncorrectDetailsException if unable to delete the deadline
     */
    public String deleteMilestone(int milestoneId) throws IncorrectDetailsException {
        try {
            Optional<Milestone> milestone = milestoneRepository.findById(milestoneId);
            if (milestone.isPresent()) {
                milestoneRepository.deleteById(milestoneId);
                return "Successfully deleted " + milestone.get().getName();
            } else {
                throw new IncorrectDetailsException("Could not find given Milestone");
            }
        } catch (PersistenceException e) {
            logger.error("Failure deleting Milestone", e);
            throw new IncorrectDetailsException("Failure deleting Milestone");
        }
    }

    /**
     * Updates the colors for the given milestone
     * @param milestone Milestone to set the colors for
     */
    public void updateMilestoneColor(Milestone milestone) {
        Optional<Sprint> sprint = sprintRepository.findSprintByMilestone(milestone);
        milestone.setColor(sprint.map(Sprint::getColor).orElse(SprintColor.WHITE));
    }
}
