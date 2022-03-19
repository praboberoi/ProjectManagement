package nz.ac.canterbury.seng302.portfolio.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SprintRepository extends CrudRepository<Sprint, Integer> {

    public List<Sprint> findBySprintId(int sprintId);

    public List<String> findBySprintName(String sprintName);

    public List<String> findBySprintNameContaining(String name);

    public List<Sprint> findByDescription(String description);

    public List<Sprint> findByDescriptionContaining(String description);

    public List<Sprint> findByStartDate(String startDate);

    public List<Sprint> findByEndDate(String endDate);

    public List<Sprint> findByProject(Project project);

    public int countBySprintName(int sprintId);

    public int countByProject(Project project);

}
