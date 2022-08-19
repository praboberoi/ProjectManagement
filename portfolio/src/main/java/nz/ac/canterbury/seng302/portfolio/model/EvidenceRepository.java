package nz.ac.canterbury.seng302.portfolio.model;

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

    /**
     * Get the piece of evidence with the specified id.
     * @param evidenceId id of the piece of evidence to be retrieved.
     * @return the evidence object.
     */
    Evidence getEvidenceByEvidenceId(Integer evidenceId);
}
