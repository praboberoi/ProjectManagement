package nz.ac.canterbury.seng302.portfolio.service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import javax.persistence.PersistenceException;
import javax.persistence.criteria.CriteriaBuilder;

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
        String message;
        try {
            milestoneRepository.save(milestone);
        } catch (PersistenceException e) {
            logger.error("Failed to save the milestone", e);
            throw new IncorrectDetailsException("Failed to save the milestone");
        }
    }

    public void verifyMileStone(Milestone milestone) throws IncorrectDetailsException {
        if (milestone == null) {
            throw new IncorrectDetailsException("No milestone");
        } else if (milestone.getName() == null || milestone.getDate() == null || milestone.getProject() == null) {
            throw new IncorrectDetailsException("Milestone values cannot be null");
        } else if (milestone.getName().length() < 1) {
            throw new IncorrectDetailsException("Milestone Name must not be empty");
        } else if (milestone.getName().length() < 3) {
            throw new IncorrectDetailsException("Milestone name must be at least 3 characters");
        } else if (milestone.getName().length() > 50) {
            throw new IncorrectDetailsException("Milestone Name cannot exceed 50 characters");
        } else if (milestone.getDate().after(milestone.getProject().getEndDate())) {
            throw new IncorrectDetailsException("Milestone date cannot be after project end date");
        } else if (milestone.getDate().before(milestone.getProject().getStartDate())) {
            throw new IncorrectDetailsException("Milestone date cannot be before project start date");
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
