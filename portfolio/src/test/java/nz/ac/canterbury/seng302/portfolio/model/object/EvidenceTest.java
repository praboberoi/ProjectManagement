package nz.ac.canterbury.seng302.portfolio.model.object;

import nz.ac.canterbury.seng302.portfolio.model.Evidence;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class EvidenceTest {

    private Evidence evidence;

    @BeforeEach
    void setup() {
        evidence = new Evidence();
    }
    /**
     * Test the adding a single link to the list of web links related to evidence.
     */
    @Test
    void givenAListOfWeblinksWhenANewLinkIsSubmittedThenTheyAreAddedToTheExistingList(){
        List<String> evidences = Arrays.asList("https://www.WebsiteOne.co.nz", "https://www.WebsiteTwo.co.nz");
        List<String> newLinks = Arrays.asList("https://www.WebsiteOne.co.nz", "https://www.WebsiteTwo.co.nz", "https://www.newLink.co.nz");
        List<String> newLinkToAdd = Arrays.asList("https://www.newLink.co.nz");
        evidence.setWeblinks(evidences);
        evidence.addWeblink(newLinkToAdd);
        assertEquals(newLinks, evidence.getWeblinks());
    }

    /**
     * Test the adding multiple links to the list of web links related to evidence.
     */
    @Test
    void givenAListOfWeblinksWhenANewListOfLinksIsSubmittedThenTheyAreAddedToTheExistingList(){
        List<String> evidences = Arrays.asList("https://www.WebsiteOne.co.nz", "https://www.WebsiteTwo.co.nz");
        List<String> newLinks = Arrays.asList("https://www.WebsiteOne.co.nz", "https://www.WebsiteTwo.co.nz", "https://www.newLink.co.nz", "https://www.newLinkTwo.co.nz");
        List<String> newLinkToAdd = Arrays.asList("https://www.newLink.co.nz", "https://www.newLinkTwo.co.nz");
        evidence.setWeblinks(evidences);
        evidence.addWeblink(newLinkToAdd);
        assertEquals(newLinks, evidence.getWeblinks());
    }

    /**
     * Test the deleting a single link from the list of web links related to evidence.
     */
    @Test
    void givenAListOfWeblinksWhenALinkIsRemovedThenTheyAreRemovedFromTheExistingList(){
        List<String> currentLinks = Arrays.asList("https://www.WebsiteOne.co.nz", "https://www.WebsiteTwo.co.nz", "https://www.newLink.co.nz");
        List<String> newEvidences = Arrays.asList("https://www.WebsiteOne.co.nz", "https://www.WebsiteTwo.co.nz");

        List<String> linkToRemove = Arrays.asList("https://www.newLink.co.nz");
        Evidence evidence = new Evidence();
        evidence.setWeblinks(currentLinks);
        evidence.removeWeblink(linkToRemove);
        assertEquals(newEvidences, evidence.getWeblinks());
    }

    /**
     * Test the deleting a single link from the list of web links related to evidence.
     */
    @Test
    void givenAListOfWeblinksWhenLinksAreRemovedThenTheyAreRemovedFromTheExistingList(){
        List<String> currentLinks = Arrays.asList("https://www.WebsiteOne.co.nz", "https://www.WebsiteTwo.co.nz", "https://www.newLink.co.nz", "https://www.newLinkTwo.co.nz");
        List<String> newEvidences = Arrays.asList("https://www.WebsiteOne.co.nz", "https://www.WebsiteTwo.co.nz");

        List<String> linkToRemove = Arrays.asList("https://www.newLink.co.nz", "https://www.newLinkTwo.co.nz");
        Evidence evidence = new Evidence();
        evidence.setWeblinks(currentLinks);
        evidence.removeWeblink(linkToRemove);
        assertEquals(newEvidences, evidence.getWeblinks());
    }



}
