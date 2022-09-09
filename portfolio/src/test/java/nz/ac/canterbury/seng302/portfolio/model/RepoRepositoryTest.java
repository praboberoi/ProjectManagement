package nz.ac.canterbury.seng302.portfolio.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

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
    void givenRepoExists_whenFindByGroup_thenCorrectNumberOfReposReturned() {
        Groups group = new Groups("Members without a group", "This is a long name", 0, List.of());
        Groups group1 = new Groups("Members of teachers group", "This is a new long name", 1, List.of());
        Repo repo = new Repo.Builder()
                .repoId(1)
                .repoName("No Groups")
                .hostAddress("http://Test.com")
                .groups(group)
                .gitlabProjectId(1)
                .build();
        repoRepository.save(repo);

        List<Repo> searchResult1 = repoRepository.findByGroups(group);
        List<Repo> searchResult2 = repoRepository.findByGroups(group1);

        assertTrue(searchResult1.size() == 1);
        assertTrue(searchResult2.size() == 0);

    }

    /**
     * Asserts that when findByGroups() is called, the correct repo is returned
     */
    @Test
    @Transactional
    void givenRepoExists_whenFindByGroup_thenCorrectTheCorrectRepoIsReturned() {
        Groups group = new Groups("New group", "This is a long name", 0, List.of());
        Repo repo = new Repo.Builder()
                .repoId(2)
                .repoName("New Groups")
                .hostAddress("http://Test.com")
                .groups(group)
                .gitlabProjectId(2)
                .build();
        repoRepository.save(repo);

        List<Repo> searchResult3 = repoRepository.findByGroups(group);

        assertTrue(searchResult3.get(0).equals(repo));

    }

}
