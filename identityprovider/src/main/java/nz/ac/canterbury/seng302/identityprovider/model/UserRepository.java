package nz.ac.canterbury.seng302.identityprovider.model;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA repository interface to get user information from the database
 */
@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Integer> {
    User getUserByUserId(int id);
    User getUserByUsername(String username);
    List<User> findAll();
    @Query(
        value = """
            FROM User user 
            LEFT JOIN user.roles AS roles
            WHERE INDEX(roles) = 0 OR roles IS NULL
            """
    )
    List<User> findAll(Sort sort);
    @Query(
        value = """
            FROM User user 
            LEFT JOIN user.roles AS roles
            WHERE INDEX(roles) = 0 OR roles IS NULL
            """
    )
    Page<User> findAll(Pageable pageable);
}