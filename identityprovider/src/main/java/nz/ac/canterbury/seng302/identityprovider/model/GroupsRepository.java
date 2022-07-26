package nz.ac.canterbury.seng302.identityprovider.model;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA repository interface to get group information from the database
 */
@Repository
public interface GroupsRepository extends PagingAndSortingRepository<Groups, Integer> {
}
