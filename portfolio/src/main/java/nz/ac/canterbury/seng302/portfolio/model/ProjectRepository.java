package nz.ac.canterbury.seng302.portfolio.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;

@Repository
public interface ProjectRepository extends CrudRepository<Project, Integer> {

    public List<Project> findByProjectName(String projectName);

    public List<Project> findByProjectNameContaining(String name);

    public List<Project> findByDescription(String description);

    public List<Project> findByDescriptionContaining(String description);

    public List<Project> findByStartDate(String startDate);

    public List<Project> findByEndDate(String endDate);

}
