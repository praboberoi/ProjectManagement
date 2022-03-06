package nz.ac.canterbury.seng302.portfolio.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
public class Project{
//   @Id
 //   @ManyToOne(optional = false)
 //  @JoinColumn(nullable = false)
 //   private Sprint name;

    @Id
    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    @Column(nullable = false)
    private String startDate;

    @Column(nullable = false)
    private String endDate;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public Project() {
    }

    public Project(String name, String description, String startDate, String endDate) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }
    @Override
    public String toString() {
        return "Project{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                '}';
    }
}

