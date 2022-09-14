package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Repo;
import nz.ac.canterbury.seng302.portfolio.model.RepoRepository;
import nz.ac.canterbury.seng302.portfolio.utils.IncorrectDetailsException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
public class RepoServiceTest {
    @MockBean
    private RepoRepository repoRepository;
    @MockBean
    private GroupService groupService;
    private RepoService repoService;
    private Repo repo1;
    private final Repo.Builder repoBuilder = new Repo.Builder();


    /**
     * Initialises evidence and project objects
     */
    @BeforeEach
    public void setup() {
        repo1 = repoBuilder.repoId(999)
                .repoName("Test Repo")
                .gitlabProjectId(999)
                .hostAddress("https://www.TestLink.com/")
                .build();

        repoService = new RepoService(repoRepository);

    }

    /**
     * Asserts that when getRepo is called with a valid ID then a repo object is returned
     */
    @Test
    void givenRepoExists_whenGetRepoCalled_thenRepoObjectReturned() {
        when(repoRepository.getByGroupId(999)).thenReturn(repo1);
        Repo result = repoRepository.getByGroupId(999);
        assertEquals(result, repo1);

    }

    /**
     * Asserts that given a correct repo object, the repo is saved correctly
     * @throws IncorrectDetailsException
     */
    @Test
    void givenValidRepoObject_whenSaveRepoCalled_thenRepoSavedSuccessfully() throws IncorrectDetailsException {
        String result = repoService.saveRepo(repo1);
        assertEquals("Successfully saved " + repo1.getRepoName(), result);
    }

    /**
     * Asserts that when a repo has no name the correct exception is thrown
     */
    @Test
    void givenRepoWithNoName_whenValidateRepoCalled_thenExceptionThrown() {
        repo1.setRepoName(null);
        IncorrectDetailsException exception = assertThrows(IncorrectDetailsException.class, () ->
                repoService.validateRepo(repo1));
        Assertions.assertEquals("Repo must have a name and host address", exception.getMessage());

    }

    /**
     * Asserts that when a repo has no host address the correct exception is thrown
     */
    @Test
    void givenRepoWithNoAddress_whenValidateRepoCalled_thenExceptionThrown() {
        repo1.setHostAddress(null);
        IncorrectDetailsException exception = assertThrows(IncorrectDetailsException.class, () ->
                repoService.validateRepo(repo1));
        Assertions.assertEquals("Repo must have a name and host address", exception.getMessage());

    }
    /**
     * Asserts that when a repo has an invalid repoName the correct exception is thrown
     */
    @Test
    void givenRepoWithInvalidRepoName_whenValidateRepoCalled_thenExceptionThrown() {
        repo1.setRepoName("");
        IncorrectDetailsException exception = assertThrows(IncorrectDetailsException.class, () ->
                repoService.validateRepo(repo1));
        Assertions.assertEquals("Project Alias field must be between 1 and 50 characters", exception.getMessage());

        repo1.setRepoName("1234568901234568901234568901234568901234568901901230912930129391023");
        IncorrectDetailsException exception2 = assertThrows(IncorrectDetailsException.class, () ->
                repoService.validateRepo(repo1));
        Assertions.assertEquals("Project Alias field must be between 1 and 50 characters", exception2.getMessage());
    }

    /**
     * Asserts that when a repo has an empty host address the correct exception is thrown
     */
    @Test
    void givenRepoWithEmptyAddress_whenValidateRepoCalled_thenExceptionThrown() {
        repo1.setHostAddress("");
        IncorrectDetailsException exception = assertThrows(IncorrectDetailsException.class, () ->
                repoService.validateRepo(repo1));
        Assertions.assertEquals("Project host address field must not be empty", exception.getMessage());
    }

    /**
     * Asserts that when a repo has an invalid host address the correct exception is thrown
     */
    @Test
    void givenRepoWithInvalidAddress_whenValidateRepoCalled_thenExceptionThrown() {
        repo1.setHostAddress("www.incorrectness.com");
        IncorrectDetailsException exception = assertThrows(IncorrectDetailsException.class, () ->
                repoService.validateRepo(repo1));
        Assertions.assertEquals("Project host address must be a valid HTTP URL", exception.getMessage());
    }
}
