package nz.ac.canterbury.seng302.portfolio;

import com.google.type.DateTime;

import nz.ac.canterbury.seng302.portfolio.entity.Project;
import nz.ac.canterbury.seng302.portfolio.entity.Sprint;
import nz.ac.canterbury.seng302.portfolio.repository.ProjectRepository;
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
    public CommandLineRunner run(ProjectRepository repository) {
        return (args -> {
            insertJavaAdvocates(repository);
            System.out.println(repository.findAll());
        });
    }

    //TODO: Resolve the UTC for TimeDates

    private void insertJavaAdvocates(ProjectRepository repository) {

        //Project project1 =  new Project("Project 2020", new java.util.ArrayList<>(),"First Attempt", "1-1-2020", "1-8-2020");
        Project project1 = Project.builder()
                        .name("Project 2020")
                        .description("First Attempt")
                        .sprints(new HashSet<>())
                        .startDate("1-1-2020")
                        .endDate("1-8-2020")
                        .build();
        repository.save(project1);

    }
}
