package nz.ac.canterbury.seng302.portfolio.model;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import javax.transaction.Transactional;

import static org.junit.Assert.*;

@DataJpaTest
@ActiveProfiles("test")

/**
 * Test class for the repo repository
 */
class RepoRepositoryTest {
    @Autowired private RepoRepository repoRepository;


    /**
     * Asserts that when findByGroups() is called, the correct number of repos are returned
     */
    @Test
    @Transactional
    void givenRepoExists_whenFindByGroupId_thenCorrectRepoReturned() {
        Repo repo = new Repo.Builder()
                .repoName("No Groups")
                .hostAddress("http://Test.com")
                .groupId(1)
                .gitlabProjectId(1)
                .accessToken("token")
                .build();
        repo = repoRepository.save(repo);

        Repo searchResult1 = repoRepository.getByGroupId(1);
        Repo searchResult2 = repoRepository.getByGroupId(0);


        assertEquals(repo, searchResult1);
        assertNull(searchResult2);

    }

    /**
     * Asserts that when findByGroups() is called, the correct repo is returned
     */
    @Test
    @Transactional
    void givenRepoExists_whenFindByGroupId_thenCorrectTheCorrectRepoIsReturned() {
        Repo repo2 = new Repo.Builder()
                .repoId(1)
                .repoName("New Repo")
                .hostAddress("http://Test.com")
                .groupId(2)
                .gitlabProjectId(2)
                .accessToken("token")
                .build();
        repoRepository.save(repo2);

        Repo searchResult3 = repoRepository.getByGroupId(2);

        assertEquals(repo2, searchResult3);

    }

}
