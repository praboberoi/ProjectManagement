package nz.ac.canterbury.seng302.identityprovider.model;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA repository interface to get user information from the database
 */
@Repository
public interface GroupRepository extends PagingAndSortingRepository<Group, Integer> {
    List<User> findUsersByGroupIsEmpty();
}
