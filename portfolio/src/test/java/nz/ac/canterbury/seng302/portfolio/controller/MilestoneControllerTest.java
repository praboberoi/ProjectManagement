package nz.ac.canterbury.seng302.portfolio.controller;

import com.google.protobuf.Timestamp;
import nz.ac.canterbury.seng302.portfolio.model.Milestone;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.User;
import nz.ac.canterbury.seng302.portfolio.model.dto.MilestoneDTO;
import nz.ac.canterbury.seng302.portfolio.service.MilestoneService;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.portfolio.utils.ControllerAdvisor;
import nz.ac.canterbury.seng302.portfolio.utils.IncorrectDetailsException;
import nz.ac.canterbury.seng302.portfolio.utils.PrincipalUtils;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for methods in MilestoneController Class
 */
@WebMvcTest(controllers = MilestoneController.class)
@AutoConfigureMockMvc(addFilters = false)
class MilestoneControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private MilestoneService milestoneService;

	@MockBean
	private SimpMessagingTemplate template;

	@MockBean
	private ProjectService projectService;

	@MockBean
	private ControllerAdvisor controllerAdvisor;

	@MockBean
	private UserAccountClientService userAccountClientService;

	@Value("${apiPrefix}")
	private String apiPrefix;

	private Project project;

	private Milestone milestone;

	private Milestone invalidMilestone;

	private Milestone newMilestone;

	private static MockedStatic<PrincipalUtils> utilities;

	private User user;

	private UserResponse.Builder userResponse;

	@BeforeAll
	private static void initialise() {
		utilities = Mockito.mockStatic(PrincipalUtils.class);
		utilities.when(() -> PrincipalUtils.checkUserIsTeacherOrAdmin(any())).thenReturn(true);
	}

	/**
	 * Initialises project and three milestones before running each test
	 */
	@BeforeEach
	public void setup() {
		when(PrincipalUtils.checkUserIsTeacherOrAdmin(any())).thenReturn(true);

		project = new Project.Builder()
				.description("This is a test project")
				.startDate(new java.sql.Date(2020 - 1900, 3, 12))
				.endDate(new java.sql.Date(2023 - 1900, 1, 10))
				.projectName("Project 2020")
				.projectId(1)
				.build();

		milestone = new Milestone.Builder()
				.milestoneId(1)
				.date(new Date())
				.name("Milestone 1")
				.project(project)
				.build();

		invalidMilestone = new Milestone.Builder()
				.date(new Date())
				.milestoneId(3)
				.name("12")
				.project(project)
				.build();

		newMilestone = new Milestone.Builder()
				.date(new Date())
				.milestoneId(0)
				.name("New Milestone")
				.project(project)
				.build();

		user = new User.Builder()
				.userId(0)
				.username("Tester")
				.firstName("Time")
				.lastName("Tester")
				.email("Test@tester.nz")
				.creationDate(new Date())
				.build();

		userResponse = UserResponse.newBuilder();
		userResponse.setUsername(user.getUsername());
		userResponse.setFirstName(user.getFirstName());
		userResponse.setLastName(user.getLastName());
		userResponse.setEmail(user.getEmail());
		userResponse.setCreated(Timestamp.newBuilder()
				.setSeconds(user.getDateCreated().getTime())
				.build());
	}

	public MilestoneDTO toDTO(Milestone milestone) {
		return new MilestoneDTO(
				milestone.getMilestoneId(),
				milestone.getName(),
				milestone.getProject(),
				milestone.getDate());
	}

	/**
	 * Tests to make sure a student is unable to save a milestone
	 * 
	 * @throws IncorrectDetailsException
	 */
	@Test
	void givenStudentUser_whenSaveMilestone_thenRequestIsRejected() throws Exception {
		when(PrincipalUtils.checkUserIsTeacherOrAdmin(any())).thenReturn(false);
		when(projectService.getProjectById(anyInt())).thenReturn(project);

		mockMvc.perform(MockMvcRequestBuilders
				.post("/project/1/milestone")
				.flashAttr("milestoneDTO", toDTO(newMilestone)))
				.andExpect(status().isForbidden())
				.andExpect(content().string("Insufficient Permissions"));
	}

	/**
	 * Tests to make sure an appropriate message is displayed when a post request is
	 * made to create a milestone.
	 * 
	 * @throws IncorrectDetailsException
	 */
	@Test
	void givenMilestoneDoesNotExist_whenMilestoneCreated_thenAppropriateMessageIsDisplayed() throws Exception {
		when(projectService.getProjectById(anyInt())).thenReturn(project);

		mockMvc.perform(MockMvcRequestBuilders
				.post("/project/1/milestone")
				.flashAttr("milestoneDTO", toDTO(newMilestone)))
				.andExpect(status().isOk())
				.andExpect(content().string("Successfully Created New Milestone"));
	}

	/**
	 * Tests to make sure an appropriate message is displayed when a post request is
	 * made to update a milestone.
	 */
	@Test
	void givenMilestoneExists_whenMilestoneUpdated_thenAppropriateMessageIsDisplayed() throws Exception {
		when(projectService.getProjectById(anyInt())).thenReturn(project);

		mockMvc.perform(MockMvcRequestBuilders
				.post("/project/1/milestone")
				.flashAttr("milestoneDTO", toDTO(milestone)))
				.andExpect(status().isOk())
				.andExpect(content().string("Successfully Updated Milestone 1"));
	}

	/**
	 * Tests to make sure an appropriate message is displayed when an invalid post
	 * request is made to update a milestone.
	 */
	@Test
	void givenBadMilestoneRequest_whenSubmitted_thenAppropriateMessageIsDisplayed() throws Exception {
		when(projectService.getProjectById(anyInt())).thenReturn(project);

		mockMvc.perform(MockMvcRequestBuilders
				.post("/project/1/milestone")
				.flashAttr("milestoneDTO", toDTO(invalidMilestone)))
				.andExpect(status().isBadRequest())
				.andExpect(content().string("Milestone name must be at least 3 characters"));
	}

	/**
	 * Tests that the page returns an error message if the project doesn't exist
	 * @throws Exception
	 */
	@Test
	void givenProjectDoesNotExist_whenGetMilestones_thenErrorPageReturned() throws Exception {
		when(projectService.getProjectById(anyInt())).thenThrow(new IncorrectDetailsException("Project not found"));

		mockMvc.perform(MockMvcRequestBuilders.get("/project/1/milestones"))
		.andExpect(status().isNotFound())
		.andExpect(model().attribute("errorMessage", "Project 1 does not exist"));
	}

	/**
	 * Tests that the page returns all the milestones that exist within a project
	 */
	@Test
	void givenProjectExists_whenGetMilestones_thenMilestones() throws Exception {
		when(projectService.getProjectById(anyInt())).thenReturn(project);
		when(milestoneService.getMilestonesByProject(any())).thenReturn(List.of(milestone, newMilestone));

		mockMvc.perform(MockMvcRequestBuilders.get("/project/1/milestones"))
		.andExpect(status().isOk())
		.andExpect(model().attribute("listMilestones", List.of(milestone, newMilestone)));
	}

	@AfterAll
	private static void tearDown() {
		utilities.close();
	}
}
