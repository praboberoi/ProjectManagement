package nz.ac.canterbury.seng302.portfolio.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository class that performs crud actions on the Persistant Sort table in the database
 */
@Repository
public interface PersistentSortRepository extends CrudRepository<PersistentSort, Integer> {

    /**
     * Obtains a list of projects with the given name.
     * @param projectName of type String.
     * @return a list of type Project.
     */
    public List<PersistentSort> findByUserId(int userId);

}
