package nz.ac.canterbury.seng302.identityprovider.model;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvidenceRepository extends PagingAndSortingRepository<Evidence, Integer> {

    /**
     * Get the piece of evidence which is owned by a user with the specified id.
     * @param ownerId user id of the owner of the evidence
     * @return List of all the evidence owned by the specified user.
     */
    List<Evidence> getAllByOwnerIdEquals(Integer ownerId);
}
