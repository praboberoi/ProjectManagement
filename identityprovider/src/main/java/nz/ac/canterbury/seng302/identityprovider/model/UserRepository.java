package nz.ac.canterbury.seng302.identityprovider.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA repository interface to get user information from the database
 */
@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
    User getUserByUserId(int id);
    User getUserByUsername(String username);
}
