package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.ProjectRepository;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.model.SprintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class SprintService {
    @Autowired
    private ProjectRepository projectRepo;
    @Autowired
    private SprintRepository sprintRepository;
    private Sprint currentSprint = null;

    public Sprint getNewSprint(Project project) {
        int sprintNo = countByProjectId(project.getProjectId()) + 1;

        Sprint newSprint = new Sprint();
        newSprint.setSprintName("Sprint " + sprintNo);
        List<Sprint> listSprints = getSprintByProject(project.getProjectId());
        if (listSprints.size() == 0) {

            LocalDate startDate = project.getStartDate().toLocalDate();
            newSprint.setStartDate(Date.valueOf(startDate));
            newSprint.setEndDate(Date.valueOf(startDate.plusWeeks(3)));
        } else {
            Sprint last_sprint = listSprints.get(listSprints.size() - 1);
            LocalDate startDate = last_sprint.getEndDate().toLocalDate();
            newSprint.setStartDate(Date.valueOf(startDate.plusDays(1)));
            newSprint.setEndDate(Date.valueOf(startDate.plusWeeks(3)));
        }
        return newSprint;
    }

    //  TODO: Write Exceptions
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
     *
     * @param sprint
     */
    public String saveSprint(Sprint sprint) throws Exception {
        String message;
        if (currentSprint == null) {
            currentSprint = sprint;
            message = "Sprint created successfully";
        } else {
            currentSprint.setSprintName(sprint.getSprintName());
            currentSprint.setDescription(sprint.getDescription());
            currentSprint.setStartDate(sprint.getStartDate());
            currentSprint.setEndDate(sprint.getEndDate());
            message = "Sprint updated successfully";
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
     *
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
     *
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

    public void updateSprintNames(List<Sprint> sprintList) {
        AtomicInteger count = new AtomicInteger(1);
        sprintList.stream().forEach(sprint -> {
            sprint.setSprintName("Sprint " + count.getAndIncrement());
            sprintRepository.save(sprint);
        });

    }

    public int countByProjectId(int projectId) {
        Optional<Project> current = projectRepo.findById(projectId);
        return sprintRepository.countByProject(current.get());
    }

    public List<Sprint> getSprintByProject(int projectId) {
        Optional<Project> current = projectRepo.findById(projectId);
        List<Sprint> sprints = sprintRepository.findByProject(current.get());
        return sprints;
    }

}

