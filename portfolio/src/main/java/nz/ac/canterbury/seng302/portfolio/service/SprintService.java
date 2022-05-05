package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.ProjectRepository;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.model.SprintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class SprintService {
    @Autowired private ProjectRepository projectRepo;
    @Autowired private SprintRepository sprintRepository;

    /**
     * To get a new sprint with the appropriate default values.
     * @param project of type Project
     * @return of type Sprint
     * @throws Exception if no more sprints can be created with the current project date range.
     */
    public Sprint getNewSprint(Project project) throws Exception {
        int sprintNo = countByProjectId(project.getProjectId()) + 1;

        Sprint sprint = new Sprint();
        sprint.setSprintLabel("Sprint " + sprintNo);
        sprint.setSprintName("Sprint " + sprintNo);
        List<Sprint> listSprints = getSprintByProject(project.getProjectId());
        if (listSprints.size() == 0) {
            LocalDate startDate = project.getStartDate().toLocalDate();
            sprint.setStartDate(Date.valueOf(startDate));
            sprint.setEndDate(Date.valueOf(startDate.plusWeeks(3)));
        } else {
            Sprint lastSprint = listSprints.get(listSprints.size() - 1);
            LocalDate lastSprintEndDate = lastSprint.getEndDate().toLocalDate();
            if (Date.valueOf(lastSprintEndDate).equals(project.getEndDate()))
                throw new Exception("Project date limit reached, cannot create a new sprint");
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
     * @param projectId of type int
     */
    public void deleteAllSprints(int projectId) {
        List<Sprint> sprintList = getSprintByProject(projectId);
        sprintList.forEach(sprint -> {
            try {
                deleteSprint(sprint.getSprintId());
            } catch (Exception e) {
                throw new RuntimeException("Failure Saving Sprint");
            }
        });
    }

    /**
     * Saves a sprint object to the database
     */
    public String saveSprint(Sprint sprint) throws Exception {
        String message;
        if (sprint.getSprintId() == 0)
            message = "Successfully Created " + sprint.getSprintLabel();
        else
            message = "Successfully Updated " + sprint.getSprintLabel();

        try {
            sprintRepository.save(sprint);
            return message;
        } catch (Exception e) {
            throw new Exception("Failure Saving Sprint");
        }
    }

    /**
     * Returns a sprint object from the database. If the sprint is not present then it throws an exception
     * @param sprintId Key used to find the sprint object
     * @return Sprint object
     */
    public Sprint getSprint(int sprintId) throws Exception {
        Optional<Sprint> result = sprintRepository.findById(sprintId);
        if(result.isPresent())
            return result.get();
        else
            throw new Exception("Failed to locate the sprint in the database");

    }

    /**
     * Deletes a sprint from the database
     * @param sprintId Key used to find the sprint object
     */
    public String deleteSprint(int sprintId) throws Exception {
        try {
            Optional<Sprint> sprint = sprintRepository.findById(sprintId);
            if(sprint.isPresent()) {
                sprintRepository.deleteById(sprintId);
                return "Successfully deleted " + sprint.get().getSprintLabel();
            }
            else
                throw new Exception("Could not find the given sprint");
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
            if(currentSprintIndex > 0 ) {
                previousSprintEndDate = sprints.get(currentSprintIndex - 1).getEndDate();
                sprintMinDate = previousSprintEndDate.toLocalDate().plusDays(1);
            } else {
                previousSprintEndDate = project.getStartDate();
                sprintMinDate = previousSprintEndDate.toLocalDate();
            }

            if(currentSprintIndex < sprints.size() - 1) {
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
     * Verifies the current sprint against other sprints in the project to make sure there is no manual changes have
     * been made to the HTML page at the client.
     * @throws Exception indicating page values of the HTML page are manually changed.
     */
    public void verifySprint(Sprint sprint) throws Exception {
        if(sprint.getStartDate().after(sprint.getEndDate()))
            throw new Exception("HTML page values manually changed. Cannot save the given sprint");

        if(sprint.getStartDate().before(sprint.getProject().getStartDate()))
            throw new Exception("HTML page values manually changed. Cannot save the given sprint");

        if(sprint.getStartDate().after(sprint.getProject().getEndDate()))
            throw new Exception("HTML page values manually changed. Cannot save the given sprint");

        if(sprint.getEndDate().after(sprint.getProject().getEndDate()))
            throw new Exception("HTML page values manually changed. Cannot save the given sprint");

        List<Sprint> sprints = sprintRepository.findByProject(sprint.getProject()).stream()
                .filter(sp -> !(sp.getSprintLabel().equals(sprint.getSprintLabel())))
                .filter(sp -> (betweenDateRange(sp, sprint))).toList();
        if(sprints.size() > 0)
            throw new Exception("HTML page values manually changed. Cannot save the given sprint");

    }

    /**
     * Compares the current sprint start and end dates with the given sprint start and end dates. Returns true if
     * the current sprint lies between or overlaps with the given sprint's date range otherwise returns false.
     * @param compSprint Given sprint
     * @return boolean value
     */
    private boolean betweenDateRange(Sprint compSprint, Sprint currentSprint) {
        Date currentSprintStartDate = currentSprint.getStartDate();
        Date currentSprintEndDate = currentSprint.getEndDate();

        Date compSprintStartDate = compSprint.getStartDate();
        Date compSprintEndDate = compSprint.getEndDate();

        if(currentSprintStartDate.compareTo(compSprintStartDate) == 0)
            return true;

        else if(currentSprintEndDate.compareTo(compSprintEndDate) == 0)
            return true;

        else if(currentSprintStartDate.after(compSprintStartDate) && currentSprintEndDate.before(compSprintEndDate))
            return true;

        else if(currentSprintStartDate.after(compSprintStartDate) && currentSprintStartDate.before(compSprintEndDate))
            return true;

        else if(currentSprintEndDate.after(compSprintStartDate) && currentSprintEndDate.before(compSprintEndDate))
            return true;

        else
            return false;
    }
}

