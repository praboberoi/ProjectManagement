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
            if (Date.valueOf(startDate).equals(project.getEndDate()))
                throw new Exception("Project date limit reached, cannot create a new sprint");
            else {
                newSprint.setStartDate(Date.valueOf(startDate.plusDays(1)));

                if(Date.valueOf(startDate.plusWeeks(3)).after(project.getEndDate()))
                    newSprint.setEndDate(project.getEndDate());
                else
                    newSprint.setEndDate(Date.valueOf(startDate.plusWeeks(3)));
            }
        }
        return newSprint;
    }
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
     * @param sprint
     */
    public String saveSprint(Sprint sprint) throws Exception {
        String message;
        if (currentSprint == null) {
            currentSprint = sprint;
            message = "Successfully Created " + sprint.getSprintName();
        } else {
            currentSprint.setSprintName(sprint.getSprintName());
            currentSprint.setDescription(sprint.getDescription());
            currentSprint.setStartDate(sprint.getStartDate());
            currentSprint.setEndDate(sprint.getEndDate());
            message = "Successfully Updated " + sprint.getSprintName();
        }

        if(currentSprint.getStartDate().after(currentSprint.getEndDate()))
            throw new Exception("HTML page values manually changed. Cannot save the given sprint");

        if(currentSprint.getStartDate().before(currentSprint.getProject().getStartDate()))
            throw new Exception("HTML page values manually changed. Cannot save the given sprint");

        if(currentSprint.getStartDate().after(currentSprint.getProject().getEndDate()))
            throw new Exception("HTML page values manually changed. Cannot save the given sprint");

        //TODO : Change te below from sprintName to label once its been added
        List<Sprint> sprints = sprintRepository.findByProject(currentSprint.getProject()).stream()
                .filter(sp -> !(sp.getSprintName().equals(currentSprint.getSprintName())))
                .filter(this::betweenDateRange).toList();
        if(sprints.size() > 0) {
            throw new Exception("HTML page values manually changed. Cannot save the given sprint");
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
     * Returns a sprint object from the database. If the sprint is not present then it throws an exception
     * @param sprintId Key used to find the sprint object
     * @return Sprint object
     */
    public Sprint getSprint(int sprintId) throws Exception {
        Optional<Sprint> result = sprintRepository.findById(sprintId);
        if(result.isPresent()) {
            currentSprint = result.get();
            return currentSprint;
        }
        else
            throw new Exception("Failed to locate the sprint in the database");
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
        sprintList.forEach(sprint -> {
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
        return current.map(project -> sprintRepository.countByProject(project)).orElse(0);
    }

    /**
     * @param projectId
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
     * Compares the current sprint start and end dates with the given sprint start and end dates. Returns true if
     * the current sprint lies between or overlaps with the given sprint's date range otherwise returns false.
     * @param compSprint Given sprint
     * @return boolean value
     */
    public boolean betweenDateRange(Sprint compSprint) {
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

