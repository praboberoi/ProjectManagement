package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * Client service used to communicate to the database and perform business logic for Milestones
 */
@Service
public class MilestoneService {
    @Autowired private MilestoneRepository milestoneRepository;
    @Autowired private ProjectRepository projectRepository;
    @Autowired private SprintRepository sprintRepository;
    private Logger logger = LoggerFactory.getLogger(MilestoneService.class);


    public MilestoneService(MilestoneRepository milestoneRepository, ProjectRepository projectRepository,
                           SprintRepository sprintRepository) {
        this.milestoneRepository = milestoneRepository;
        this.sprintRepository = sprintRepository;
        this.projectRepository = projectRepository;
    }
}
