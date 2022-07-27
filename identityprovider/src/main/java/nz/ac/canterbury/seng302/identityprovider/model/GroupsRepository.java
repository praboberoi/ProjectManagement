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
    @Query(value = "SELECT u FROM User u LEFT JOIN u.groups g WHERE g IS NULL")
    List<User> findUsersNotInGroup();
}
