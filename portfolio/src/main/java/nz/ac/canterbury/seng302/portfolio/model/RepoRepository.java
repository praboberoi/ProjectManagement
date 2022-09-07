package nz.ac.canterbury.seng302.portfolio.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepoRepository extends CrudRepository <Repo, Integer> {

    /**
     * Obtains a list repos with the given group
     * @param group of type Groups
     * @return a List of type Repo
     */
    public List<Repo> findByGroups(Groups group);


}
