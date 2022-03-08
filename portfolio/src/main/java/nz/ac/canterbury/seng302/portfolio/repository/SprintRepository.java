package nz.ac.canterbury.seng302.portfolio.repository;

import nz.ac.canterbury.seng302.portfolio.entity.Project;
import nz.ac.canterbury.seng302.portfolio.entity.Sprint;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SprintRepository extends CrudRepository<Sprint, Long> {

    public List<Sprint> findById(String id);

    public List<String> findBySprintName(String sprintName);

    public List<String> findBySprintNameContaining(String name);

    public List<Sprint> findByDescription(String description);

    public List<Sprint> findByDescriptionContaining(String description);

    public List<Sprint> findByStartDate(String startDate);

    public List<Sprint> findByEndDate(String endDate);

    public List<Sprint> findByProject(Project project);

}
