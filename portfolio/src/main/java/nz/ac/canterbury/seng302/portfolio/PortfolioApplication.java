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

}
