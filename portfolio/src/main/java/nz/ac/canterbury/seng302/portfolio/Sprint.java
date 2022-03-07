package nz.ac.canterbury.seng302.portfolio;

import org.springframework.web.bind.annotation.ModelAttribute;

import java.text.DateFormat;
import java.util.Date;

public class Sprint {
    private String name;
    private String startDate;
    private String endDate;
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
