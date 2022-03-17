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
    public void saveSprint(Sprint sprint) {
        sprintRepository.save(sprint);
    }

    /**
     * Returns a sprint object from the database
     * @param sprintId Key used to find the sprint object
     * @return Sprint object
     */
    public Sprint getSprint(int sprintId){
        Optional<Sprint> result = sprintRepository.findById(sprintId);
        return result.get();
    }

    /**
     * Deletes a sprint from the database
     * @param sprintId Key used to find the sprint object
     */
    public void deleteSprint(int sprintId){
        //Long count = sprintRepo.countBySprintName(sprintId); //change
        /*if (count != null || count == 0){
            throw new Exception("error" + id);
        }
         */
        sprintRepository.deleteById(sprintId);
    }

    // new function to get list by project id

    public int countByProjectId(int projectId) {
        Optional<Project> current = projectRepo.findById(projectId);
        return sprintRepository.countByProject(current.get());
    }


}

