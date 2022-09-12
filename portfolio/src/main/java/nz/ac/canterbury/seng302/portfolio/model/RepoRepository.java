package nz.ac.canterbury.seng302.portfolio.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepoRepository extends CrudRepository <Repo, Integer> {

    /**
     * Obtains a list of repos with the given group
     * @param groupId of type int
     * @return a List of Repo objects
     */
    Repo findByGroupId(int groupId);


}
