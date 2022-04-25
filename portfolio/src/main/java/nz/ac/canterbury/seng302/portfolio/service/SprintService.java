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
    private Sprint currentSprint = null;

    public Sprint getNewSprint(Project project) throws Exception {
        int sprintNo = countByProjectId(project.getProjectId()) + 1;

        Sprint newSprint = new Sprint();
        newSprint.setSprintName("Sprint " + sprintNo);
        List<Sprint> listSprints = getSprintByProject(project.getProjectId());
        if (listSprints.size() == 0) {
            LocalDate startDate = project.getStartDate().toLocalDate();
            newSprint.setStartDate(Date.valueOf(startDate));
            newSprint.setEndDate(Date.valueOf(startDate.plusWeeks(3)));
        } else {
            Sprint lastSprint = listSprints.get(listSprints.size() - 1);
            LocalDate startDate = lastSprint.getEndDate().toLocalDate();
            if (Date.valueOf(startDate.minusDays(1)).equals(project.getEndDate())) {
                throw new Exception("Project date limit reached, cannot create a new sprint");
            }
            else {
                newSprint.setStartDate(Date.valueOf(startDate.plusDays(1)));
                newSprint.setEndDate(Date.valueOf(startDate.plusWeeks(3)));
            }
        }
        return newSprint;
    }
    public void deleteAllSprints(int projectId) {
        List<Sprint> sprintList = getSprintByProject(projectId);
        sprintList.stream().forEach(sprint -> {
            try {
                deleteSprint(sprint.getSprintId());
            } catch (Exception e) {
                throw new RuntimeException("Failure Saving Sprint");
            }
        });
    }

    /**
     * Saves a sprint object to the database
     * @param sprint
     */
    public String saveSprint(Sprint sprint) throws Exception {
        String message;
        if (currentSprint == null) {
            currentSprint = sprint;
            message = "Successfully Created Sprint " + sprint.getSprintName();
        } else {
            currentSprint.setSprintName(sprint.getSprintName());
            currentSprint.setDescription(sprint.getDescription());
            currentSprint.setStartDate(sprint.getStartDate());
            currentSprint.setEndDate(sprint.getEndDate());
            message = "Successfully Updated Sprint " + sprint.getSprintName();
        }
        try {
            sprintRepository.save(currentSprint);
            currentSprint = null;
            return message;
        } catch (Exception e) {
            throw new Exception("Failure Saving Sprint");
        }
    }

    /**
     * Returns a sprint object from the database
     * @param sprintId Key used to find the sprint object
     * @return Sprint object
     */
    public Sprint getSprint(int sprintId) {
        Optional<Sprint> result = sprintRepository.findById(sprintId);
        currentSprint = result.get();
        return currentSprint;
    }

    /**
     * Deletes a sprint from the database
     * @param sprintId Key used to find the sprint object
     */
    public String deleteSprint(int sprintId) throws Exception {
        try {
            sprintRepository.deleteById(sprintId);
            return "Sprint Deleted Successfully";
        } catch (Exception e) {
            throw new Exception("Failure Deleting Sprint");
        }
    }

    /**
     * If the project sprint list is edited in some way, change the names of sprints accordingly.
     * @param sprintList
     */
    public void updateSprintNames(List<Sprint> sprintList) {
        AtomicInteger count = new AtomicInteger(1);
        sprintList.stream().forEach(sprint -> {
            sprint.setSprintName("Sprint " + count.getAndIncrement());
            sprintRepository.save(sprint);
        });

    }

    /**
     * Return the number of sprints created under a project.
     * @param projectId
     * @return total number of sprints in a project.
     */
    public int countByProjectId(int projectId) {
        Optional<Project> current = projectRepo.findById(projectId);
        return sprintRepository.countByProject(current.get());
    }

    /**
     * @param projectId
     * @return a list of sprints from a project specified by its Id.
     */
    public List<Sprint> getSprintByProject(int projectId) {
        Optional<Project> current = projectRepo.findById(projectId);
        List<Sprint> sprints = sprintRepository.findByProject(current.get());
        return sprints;
    }

    /**
     * Method that finds the required date range for the given sprint and project.
     * @param project of type Project.
     * @param sprint of type Sprint.
     * @return a list of Strings containing the date range.
     */
    public List<String> getSprintDateRange(Project project, Sprint sprint) {
        List<Sprint> sprints = getSprintByProject(project.getProjectId());
        if(sprints.size() == 0)
            return Arrays.asList(project.getStartDate().toString(), project.getEndDate().toString());

        else if (!sprints.contains(sprint)) {
            Date previousSprintEndDate = sprints.get(sprints.size() - 1).getEndDate();
            LocalDate sprintMinDate = previousSprintEndDate.toLocalDate().plusDays(1);
            return Arrays.asList(sprintMinDate.toString(), project.getEndDate().toString());
        }

        else {
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
            return Arrays.asList(sprintMinDate.toString(), sprintMaxDate.toString());

        }
    }
}

