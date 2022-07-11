package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.ProjectRepository;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.model.SprintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.PersistenceException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Client service used to communicate to the database and perform business logic for sprints
 */
@Service
public class SprintService {
    @Autowired private ProjectRepository projectRepo;
    @Autowired private SprintRepository sprintRepository;

    public SprintService(ProjectRepository projectRepository, SprintRepository sprintRepository) {
        this.projectRepo = projectRepository;
        this.sprintRepository = sprintRepository;
    }
    /**
     * To get a new sprint with the appropriate default values.
     * @param project Of type Project
     * @return Of type Sprint
     * @throws IncorrectDetailsException If no more sprints can be created with the current project date range.
     */
    public Sprint getNewSprint(Project project) throws IncorrectDetailsException {
        int sprintNo = countByProjectId(project.getProjectId()) + 1;
        Sprint sprint = new Sprint.Builder()
                                  .sprintLabel("Sprint " + sprintNo)
                                  .sprintName("Sprint " + sprintNo).build();

        List<Sprint> listSprints = getSprintByProject(project.getProjectId());
        if (listSprints.size() == 0) {
            LocalDate startDate = project.getStartDate().toLocalDate();
            sprint.setStartDate(Date.valueOf(startDate));
            sprint.setEndDate(Date.valueOf(startDate.plusWeeks(3)));
        } else {
            Sprint lastSprint = listSprints.get(listSprints.size() - 1);
            LocalDate lastSprintEndDate = lastSprint.getEndDate().toLocalDate();
            if (Date.valueOf(lastSprintEndDate).equals(project.getEndDate()))
                throw new IncorrectDetailsException("Project date limit reached, cannot create a new sprint");
            else {
                sprint.setStartDate(Date.valueOf(lastSprintEndDate.plusDays(1)));

                if(Date.valueOf(lastSprintEndDate.plusWeeks(3)).after(project.getEndDate()))
                    sprint.setEndDate(project.getEndDate());
                else
                    sprint.setEndDate(Date.valueOf(lastSprintEndDate.plusWeeks(3)));
            }

        }
        return sprint;
    }

    /**
     * Deletes all the sprints of the given project ID
     * @param projectId Of type int
     * @throws IncorrectDetailsException That has been passed by {@link #deleteSprint(int) deleteSprint}
     * @throws RuntimeException If there is an error deleting a sprint
     */
    public void deleteAllSprints(int projectId) throws IncorrectDetailsException, RuntimeException {
        List<Sprint> sprintList = getSprintByProject(projectId);
        sprintList.forEach(sprint -> {
            try {
                deleteSprint(sprint.getSprintId());
            } catch (Exception e) {
                throw new RuntimeException("Failure Deleting All Sprints");
            }
        });
    }

    /**
     * Saves a sprint object to the database
     * @param sprint of type Sprint
     * @return appropriate message depending on if the sprint is created or updated
     * @throws PersistenceException That has been passed by {@link SprintRepository#save(Object) save}
     */
    public String saveSprint(Sprint sprint) throws PersistenceException {
        String message;
        if (sprint.getSprintId() == 0)
            message = "Successfully Created " + sprint.getSprintLabel();
        else
            message = "Successfully Updated " + sprint.getSprintLabel();
        sprintRepository.save(sprint);
        return message;

    }

    /**
     * Returns a sprint object from the database. If the sprint is not present then it throws an exception
     * @param sprintId Key used to find the sprint object
     * @return Sprint object
     * @throws IncorrectDetailsException If null value is returned by {@link SprintRepository#findById(Object) findById}
     */
    public Sprint getSprint(int sprintId) throws IncorrectDetailsException {
        Optional<Sprint> result = sprintRepository.findById(sprintId);
        if(result.isPresent())
            return result.get();
        else
            throw new IncorrectDetailsException("Failed to locate the sprint in the database");

    }

    /**
     * Deletes the given sprint from the database
     * @param sprintId Key used to find the sprint object
     * @throws IncorrectDetailsException If null value is returned from the sprintRepository's findById function
     * @throws PersistenceException Passed by {@link SprintRepository#deleteById(Object) deleteById}
     */
    public String deleteSprint(int sprintId) throws Exception,IncorrectDetailsException, PersistenceException {
        try {
            Optional<Sprint> sprint = sprintRepository.findById(sprintId);
            if(sprint.isPresent()) {
                sprintRepository.deleteById(sprintId);
                return "Successfully deleted " + sprint.get().getSprintLabel();
            }
            else
                throw new IncorrectDetailsException("Could not find the given sprint");
        } catch (Exception e) {
            throw new Exception("Failure Deleting Sprint");
        }
    }

    /**
     * If the project sprint list is edited in some way, change the names of sprints accordingly.
     * @param sprintList a list of all the sprints
     */
    public void updateSprintLabels(List<Sprint> sprintList) {
        AtomicInteger count = new AtomicInteger(1);
        sprintList.forEach(sprint -> {
            sprint.setSprintLabel("Sprint " + count.getAndIncrement());
            sprintRepository.save(sprint);
        });
    }

    /**
     * Return the number of sprints created under a project.
     * @param projectId of type int
     * @return total number of sprints in a project.
     */
    public int countByProjectId(int projectId) {
        Optional<Project> current = projectRepo.findById(projectId);
        return current.map(project -> sprintRepository.countByProject(project)).orElse(0);
    }

    /**
     * Returns a list of sprints that are related to the given project ID
     * @param projectId of type int
     * @return a list of sprints from a project specified by its Id.
     */
    public List<Sprint> getSprintByProject(int projectId) {
        Optional<Project> current = projectRepo.findById(projectId);
        return current.map(project -> sprintRepository.findByProject(project)).orElse(List.of());
    }

    /**
     * Method that finds the required date range for the given sprint and project.
     * @param project of type Project.
     * @param sprint of type Sprint.
     * @return a list of Strings containing the date range.
     */
    public List<String> getSprintDateRange(Project project, Sprint sprint) {
        List<Sprint> sprints = getSprintByProject(project.getProjectId());
        String stringSprintMinDate;
        String stringSprintMaxDate;
        if(sprints.size() == 0) {
            stringSprintMinDate = project.getStartDate().toString();
            stringSprintMaxDate = project.getEndDate().toString();
        } else if (!sprints.contains(sprint)) {
            Date previousSprintEndDate = sprints.get(sprints.size() - 1).getEndDate();
            LocalDate sprintMinDate = previousSprintEndDate.toLocalDate().plusDays(1);
            stringSprintMinDate = sprintMinDate.toString();
            stringSprintMaxDate = project.getEndDate().toString();
        } else {
            Date previousSprintEndDate;
            LocalDate sprintMinDate;
            Date nextSprintStartDate;
            LocalDate sprintMaxDate;

            int currentSprintIndex = sprints.indexOf(sprint);
            if (currentSprintIndex > 0 ) {
                previousSprintEndDate = sprints.get(currentSprintIndex - 1).getEndDate();
                sprintMinDate = previousSprintEndDate.toLocalDate().plusDays(1);
            } else {
                previousSprintEndDate = project.getStartDate();
                sprintMinDate = previousSprintEndDate.toLocalDate();
            }

            if (currentSprintIndex < sprints.size() - 1) {
                nextSprintStartDate = sprints.get(currentSprintIndex + 1).getStartDate();
                sprintMaxDate = nextSprintStartDate.toLocalDate().minusDays(1);
            } else {
                nextSprintStartDate = project.getEndDate();
                sprintMaxDate = nextSprintStartDate.toLocalDate();
            }
            stringSprintMinDate = sprintMinDate.toString();
            stringSprintMaxDate = sprintMaxDate.toString();
        }
        return Arrays.asList(stringSprintMinDate, stringSprintMaxDate);
    }

    /**
     * Verifies the current sprint to make sure there are no manual changes
     * made to the HTML page at the client.
     * @throws IncorrectDetailsException Is raised if the sprint values are incorrect.
     * @return
     */
    public boolean verifySprint(Sprint sprint) throws IncorrectDetailsException {
        if (sprint.getStartDate().after(sprint.getEndDate()))
            throw new IncorrectDetailsException("Sprint start date can not be after sprint end date");

        if (sprint.getStartDate().before(sprint.getProject().getStartDate()))
            throw new IncorrectDetailsException("Sprint start date can not be before project start date");

        if (sprint.getStartDate().after(sprint.getProject().getEndDate()))
            throw new IncorrectDetailsException("Sprint start date can not be after project end date");

        if (sprint.getEndDate().after(sprint.getProject().getEndDate()))
            throw new IncorrectDetailsException("Sprint end date can not be after project end date");

        if(sprint.getDescription().length() > 250)
            throw new IncorrectDetailsException("Project description too long");

        List<Sprint> sprints = sprintRepository.findByProject(sprint.getProject());
        try {
            Sprint savedSprint = getSprint(sprint.getSprintId());
            if (!Objects.equals(savedSprint.getSprintLabel(), sprint.getSprintLabel()))
                throw new IncorrectDetailsException("Sprint label can not be modified");
        } catch (IncorrectDetailsException ignored) {
            if(!Objects.equals(sprint.getSprintLabel(), "Sprint " + (sprints.size() + 1))){
                throw new IncorrectDetailsException("Sprint label can not be modified");
            }
        }


        if(sprints.stream().filter(sp -> !Objects.equals(sp.getSprintLabel(), sprint.getSprintLabel()))
                            .anyMatch(sp -> (betweenDateRange(sp, sprint)))){
            throw new IncorrectDetailsException("Sprint dates can not overlap with another sprint");

        }
        return true;
    }

    /**
     * Compares the current sprint start and end dates with the given sprint start and end dates. Returns true if
     * the current sprint lies between or overlaps with the given sprint's date range otherwise returns false.
     * @param currentSprint The sprint to be compared against
     * @param compSprint Given sprint
     * @return If the currentSprint overlaps with the compSprint
     */
    private boolean betweenDateRange(Sprint compSprint, Sprint currentSprint) {
        Date currentSprintStartDate = currentSprint.getStartDate();
        Date currentSprintEndDate = currentSprint.getEndDate();

        Date compSprintStartDate = compSprint.getStartDate();
        Date compSprintEndDate = compSprint.getEndDate();

        // Check for overlaping dates retrived from https://www.codespeedy.com/check-if-two-date-ranges-overlap-or-not-in-java/
        return currentSprintStartDate.before(compSprintStartDate) && currentSprintEndDate.after(compSprintStartDate) ||
        currentSprintStartDate.before(compSprintEndDate) && currentSprintEndDate.after(compSprintEndDate) ||
        currentSprintStartDate.before(compSprintStartDate) && currentSprintEndDate.after(compSprintEndDate) ||
        currentSprintStartDate.after(compSprintStartDate) && currentSprintEndDate.before(compSprintEndDate) ||
                currentSprintEndDate.equals(compSprintStartDate) ||
                currentSprintStartDate.equals(compSprintEndDate);
    }
}

