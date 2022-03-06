package nz.ac.canterbury.seng302.portfolio;

import com.google.type.DateTime;

import nz.ac.canterbury.seng302.portfolio.entity.Project;
import nz.ac.canterbury.seng302.portfolio.repository.ProjectRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
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

        Project project1 =  new Project("Project 2020", "First project","1-1-2020","1-8-2020");
        repository.save(project1);
        System.out.println(repository.findAll());





    }
}
