package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.ProjectRepository;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.model.SprintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// more info here https://codebun.com/spring-boot-crud-application-using-thymeleaf-and-spring-data-jpa/

@Service
public class SprintService {
    @Autowired private ProjectRepository projectRepo;
    @Autowired private SprintRepository sprintRepository;
    private Sprint currentSprint = null;

    /**
     * Get list of all sprints
     */
    public List<Sprint> getAllSprints() {
        List<Sprint> list = (List<Sprint>) sprintRepository.findAll();
        return list;
    }

    /**
     * Get sprint by id
     */
    public Sprint getSprintById(int id) throws Exception {
        Optional<Sprint> sprint = sprintRepository.findById(id);
        if(sprint!=null) {
            return sprint.get();
        }
        else
        {
            throw new Exception("Project not found");
        }
    }

    /**
     * Saves a sprint object to the database
     * @param sprint
     */
    /*
    public void saveSprint(Sprint sprint) {
        sprintRepository.save(sprint);
    }

     */
    public void saveSprint(Sprint sprint) {
        if (currentSprint == null) {
            sprintRepository.save(sprint);

            System.out.println("new");
            System.out.println(sprint.getSprintId());
        } else {
            currentSprint.setSprintName(sprint.getSprintName());
            currentSprint.setDescription(sprint.getDescription());
            currentSprint.setStartDate(sprint.getStartDate());
            currentSprint.setEndDate(sprint.getEndDate());
            sprintRepository.save(currentSprint);

            System.out.println("edit");
            System.out.println(sprint.getSprintId());
            currentSprint = null;
        }
    }


    /**
     * Returns a sprint object from the database
     * @param sprintId Key used to find the sprint object
     * @return Sprint object
     */
    public Sprint getSprint(int sprintId){
        Optional<Sprint> result = sprintRepository.findById(sprintId);
        currentSprint = result.get();

        System.out.println("found same");
        System.out.println(sprintId);
        return currentSprint;
    }

    /**
     * Deletes a sprint from the database
     * @param sprintId Key used to find the sprint object
     */
    public void deleteSprint(int sprintId){

        sprintRepository.deleteById(sprintId);
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

