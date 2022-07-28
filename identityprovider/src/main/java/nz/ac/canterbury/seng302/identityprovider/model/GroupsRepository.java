package nz.ac.canterbury.seng302.identityprovider.model;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA repository interface to get group information from the database
 */
@Repository
public interface GroupsRepository extends PagingAndSortingRepository<Groups, Integer> {
    @Query(value = "SELECT user FROM User user LEFT JOIN user.groups groups WHERE groups IS NULL")
    List<User> findUsersNotInGroup();
    
    @Query(value = "SELECT user FROM User user JOIN user.roles role WHERE role = 1")
    List<User> findTeacherGroup();
}
