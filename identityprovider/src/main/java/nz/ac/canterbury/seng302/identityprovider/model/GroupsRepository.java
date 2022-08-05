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
    /**
     * Selects all users that are not in a group
     * @return List of groupless users
     */
    @Query(value = "SELECT user FROM User user LEFT JOIN user.groups groups WHERE groups IS NULL")
    List<User> findUsersNotInGroup();
    
    /**
     * Selects all users that have the role Teacher
     * @return List of Teacher users
     */
    @Query(value = "SELECT user FROM User user JOIN user.roles role WHERE role = 1")
    List<User> findTeacherGroup();

    /**
     * Delete the selected group from the database
     * @param groupId Group to delete
     * @return number of groups deleted (1=success, 0=failed)
     */
    long deleteGroupByGroupId(int groupId);

    /**
     * Get the group that has the short name selected.
     * @param groupShortName group to get
     * @return
     */
    Groups getAllByShortNameEquals(String groupShortName);

    /**
     * Get the group that has the long name selected.
     * @param groupLongName group to get
     * @return
     */
    Groups getAllByLongNameEquals(String groupLongName);
}
