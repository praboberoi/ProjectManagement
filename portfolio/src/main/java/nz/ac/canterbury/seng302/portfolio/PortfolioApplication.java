package nz.ac.canterbury.seng302.portfolio;

import com.google.type.DateTime;

import nz.ac.canterbury.seng302.portfolio.entity.Project;
import nz.ac.canterbury.seng302.portfolio.entity.Sprint;
import nz.ac.canterbury.seng302.portfolio.repository.ProjectRepository;
import nz.ac.canterbury.seng302.portfolio.repository.SprintRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.*;


@SpringBootApplication
public class PortfolioApplication {

    public static void main(String[] args) {
        SpringApplication.run(PortfolioApplication.class, args);

    }

    @Bean
    public CommandLineRunner run(ProjectRepository projectRepository, SprintRepository sprintRepository) {
        return (args -> {
            insertJavaAdvocates(projectRepository, sprintRepository);
            System.out.println(projectRepository.findAll());
            System.out.println(sprintRepository.findAll());
            //projectRepository.deleteById(66L);
        });
    }

    //TODO: Resolve the UTC for TimeDates

    private void insertJavaAdvocates(ProjectRepository projectRepository, SprintRepository sprintRepository) {

        //Project project1 =  new Project("Project 2020", new java.util.ArrayList<>(),"First Attempt", "1-1-2020", "1-8-2020");
        Project project1 = new Project.Builder()
                        .projectName("Project 2020")
                        .description("First Attempt")
                        .startDate("1-1-2020")
                        .endDate("1-8-2020")
                        .build();
        projectRepository.save(project1);

        sprintRepository.save(new Sprint.Builder()
                .sprintName("First Sprint")
                .project(project1)
                .description("This is first sprint")
                .startDate("1-1-2020")
                .endDate("20-1-2020")
                .build());




    }
}
