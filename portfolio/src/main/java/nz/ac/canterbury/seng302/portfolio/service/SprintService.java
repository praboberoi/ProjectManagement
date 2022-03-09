package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.entity.Sprint;
import nz.ac.canterbury.seng302.portfolio.repository.ProjectRepository;
import nz.ac.canterbury.seng302.portfolio.repository.SprintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SprintService {
    @Autowired private ProjectRepository projectRepo;

    @Autowired
    private SprintRepository sprintRepo;

    /**
     * Returns a list containing all sprint objects in the database
     * @return
     */
    public List<Sprint> listAll() {
        return (List<Sprint>) sprintRepo.findAll();
    }

    /**
     * Saves a sprint object to the database
     * @param sprint
     */
    public void saveSprint(Sprint sprint) {
        sprintRepo.save(sprint);
    }

    /**
     * Returns a sprint object from the database
     * @param sprintId Key used to find the sprint object
     * @return Sprint object
     */
    public Sprint getSprint(Long sprintId){
        Optional<Sprint> result = sprintRepo.findById(sprintId);
        return result.get();
    }

    /**
     * Deletes a sprint from the database
     * @param sprintId Key used to find the sprint object
     */
    public void deleteSprint(Long sprintId){
        Long count = sprintRepo.countBySprintName(sprintId); //change
        /*if (count != null || count == 0){
            throw new Exception("error" + id);
        }
         */
        sprintRepo.deleteById(sprintId);
    }

    // new function to get list by project id
    /*
    public List<Sprint> listByProjectId(Integer projectId) {
        //projectRepo.findById(projectId);
        sprintRepo.findByProject();
    }

     */
}
