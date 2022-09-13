package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Evidence;
import nz.ac.canterbury.seng302.portfolio.model.EvidenceRepository;
import nz.ac.canterbury.seng302.portfolio.model.Repo;
import nz.ac.canterbury.seng302.portfolio.model.RepoRepository;
import nz.ac.canterbury.seng302.portfolio.utils.IncorrectDetailsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.PersistenceException;

/**
 * Client service used to communicate to the database and perform business logic for repo entities
 */
@Service
public class RepoService {
    @Autowired
    private RepoRepository repoRepository;
    private final Logger logger = LoggerFactory.getLogger(EvidenceService.class);


    public RepoService(RepoRepository repoRepository) {this.repoRepository = repoRepository;}

    /**
     * Returns a repo object from the database. If the repo is not present then it throws an exception.
     * @param groupId The unique id (integer) of the requested repog rup.
     * @return Repo object associated with the given ID
     * @throws IncorrectDetailsException If null value is returned by {@link RepoRepository (Object) getByGroupId}
     */
    public Repo getRepo(int groupId) {
        Repo result = repoRepository.getByGroupId(groupId);
        return result;
    }

    /**
     * Saves Repo object into the database
     * @param repo The Repo object to be saved
     * @throws IncorrectDetailsException If the repo has incorrect details
     */
    public String saveRepo(Repo repo) throws IncorrectDetailsException {
        try {
//            validateRepo(repo);
            repoRepository.save(repo);
            logger.info("Successfully saved repo {}", repo.getRepoId());
            return "Successfully saved " + repo.getRepoName();
        } catch (PersistenceException e) {
            logger.error("Failure saving repo", e);
            throw new IncorrectDetailsException("Failure Saving Repo");
        }
    }
}
