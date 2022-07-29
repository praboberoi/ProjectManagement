package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Deadline;
import org.springframework.stereotype.Service;

/**
 * Client service used to communicate to the database and perform business logic for Deadlines
 */
@Service
public class DeadlineService {

    public Deadline getNewDeadline() {
        Deadline deadline = new Deadline.Builder()
    }


}
