package nz.ac.canterbury.seng302.portfolio.model;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import javax.transaction.Transactional;
import java.util.List;

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
    void givenRepoExists_whenFindByGroupId_thenCorrectNumberOfReposReturned() {
        Repo repo = new Repo.Builder()
                .repoId(1)
                .repoName("No Groups")
                .hostAddress("http://Test.com")
                .groupId(0)
                .gitlabProjectId(1)
                .accessToken("token")
                .build();
        repoRepository.save(repo);

        List<Repo> searchResult1 = repoRepository.findByGroupId(0);
        List<Repo> searchResult2 = repoRepository.findByGroupId(1);


        assertEquals(1,searchResult1.size());
        assertEquals(0,searchResult2.size());

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

        List<Repo> searchResult3 = repoRepository.findByGroupId(2);

        assertEquals(repo2, searchResult3.get(0));

    }

}
