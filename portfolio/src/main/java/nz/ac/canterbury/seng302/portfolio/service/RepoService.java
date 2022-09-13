package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Repo;
import nz.ac.canterbury.seng302.portfolio.model.RepoRepository;
import nz.ac.canterbury.seng302.portfolio.utils.IncorrectDetailsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.PersistenceException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

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
            validateRepo(repo);
            repoRepository.save(repo);
            logger.info("Successfully saved repo {}", repo.getRepoId());
            return "Successfully saved " + repo.getRepoName();
        } catch (PersistenceException e) {
            logger.error("Failure saving repo", e);
            throw new IncorrectDetailsException("Failure Saving Repo");
        }
    }

    /**
     * Validates a repo objects values
     * @param repo Repo object to be validated
     * @throws IncorrectDetailsException If the repo object is not valid
     */
    public void validateRepo(Repo repo) throws IncorrectDetailsException {
        if (repo.getRepoName() == null || repo.getHostAddress() == null)
            throw new IncorrectDetailsException("Repo must have a name and host address");
        int repoIdLength = String.valueOf(repo.getGitlabProjectId()).length();
        if (repoIdLength < 1 || repoIdLength > 50)
            throw new IncorrectDetailsException("Project ID field must be between 1 and 50 characters");
        if (repo.getRepoName().length() < 1 || repo.getRepoName().length() > 50)
            throw new IncorrectDetailsException("Project Alias field must be between 1 and 50 characters");
        if (repo.getHostAddress().length() < 1)
            throw new IncorrectDetailsException("Project host address field must not be empty");
        if (!isValidHttpUrl(repo.getHostAddress()))
            throw new IncorrectDetailsException("Project host address must be a valid HTTP URL");
    }

    /**
     * Asserts whether a given string is a valid URL
     * @param urlTest A string to be validated
     * @return True if the given string is a valid URL, False otherwise
     */
    public boolean isValidHttpUrl(String urlTest) {
        URL url;
        try {
            url = new URL(urlTest);
        } catch (MalformedURLException e) {
            return false;
        }
        return Objects.equals(url.getProtocol(), "http:") || Objects.equals(url.getProtocol(), "https:");
    }
}
