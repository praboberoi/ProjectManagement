package nz.ac.canterbury.seng302.portfolio.model;

import javax.persistence.*;

@Entity
public class Repo {

    /**
     * ID for the repo
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int repoId;

    /**
     * Group that the repo is contained in
     * Foreign key
     */
    @OneToOne(optional = false)
    @JoinColumn(name = "groupId", nullable = false)
    private Groups group;


    /**
     * Name of the repo
     */
    @Column(nullable = false)
    private String repoName;

    /**
     * No args Constructor of the repo.
     */
    public Repo() {}

    public Repo(int repoId, Groups group, String repoName){
        this.repoId = repoId;
        this.group = group;
        this.repoName = repoName;
    }


}


