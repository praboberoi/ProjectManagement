package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.entity.Project;
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

    public List<Sprint> listAll() {
        return (List<Sprint>) sprintRepo.findAll();
    }

    public void saveSprint(Sprint sprint) {
        sprintRepo.save(sprint);
    }

    public Sprint getSprint(Long sprintId){
        Optional<Sprint> result = sprintRepo.findById(sprintId);
        return result.get();
    }

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
