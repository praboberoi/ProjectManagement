package nz.ac.canterbury.seng302.portfolio.model;

import nz.ac.canterbury.seng302.portfolio.model.dto.RepoDTO;

import javax.persistence.*;
import java.util.Objects;

/**
 * Creates a Repo class required for creating a table in the databse
 */
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
    @Column(unique = true)
    @Lob
    private Groups groups;

    /**
     * Name of the repo
     */
    @Column(nullable = false)
    private String repoName;

    /**
     * ID of gitlab project
     */
    @Column(nullable = false)
    private int gitlabProjectId;

    /**
     * Access token
     */
    @Column(nullable = false)
    private String accessToken;

    /**
     * The host address
     */
    @Column(nullable = false)
    private String hostAddress;


    public Groups getGroups() {return this.groups;}

    public void setGroups(Groups groups) {this.groups = groups;}

    public int getRepoId() {return this.repoId;}

    public void setRepoId(int repoId) {this.repoId = repoId;}

    public String getRepoName() {return this.repoName;}

    public void setRepoName(String repoName) {this.repoName = repoName;}

    public int getGitlabProjectId() {return this.gitlabProjectId;}

    public void setGitlabProjectId(int gitlabProjectId) {this.gitlabProjectId = gitlabProjectId;}

    public String getAccessToken() {return this.accessToken;}

    public void setAccessToken(String accessToken) {this.accessToken = accessToken;}

    public String getHostAddress() {return this.hostAddress;}

    public void setHostAddress(String hostAddress) {this.hostAddress = hostAddress;}


    /**
     * No args Constructor of the repo.
     */
    public Repo() {}

    /**
     * Constructor for a repo object
     * @param repoId The ID of the repo
     * @param groups Group of the repo
     * @param repoName Name of the repo
     * @param gitlabProjectId The ID of the gitlab project
     * @param accessToken The access token
     * @param hostAddress The host address
     */
    public Repo(int repoId, Groups groups, String repoName, int gitlabProjectId, String accessToken, String hostAddress){
        this.repoId = repoId;
        this.groups = groups;
        this.repoName = repoName;
        this.gitlabProjectId = gitlabProjectId;
        this.accessToken = accessToken;
        this.hostAddress = hostAddress;
    }

    /**
     * Creates a new repo object with the repoDTO
     * @param repoDTO the DTO of the repo object
     */
    public Repo(RepoDTO repoDTO) {
        setRepoId(repoDTO.getRepoId());
        setGroups(repoDTO.getGroups());
        setRepoName(repoDTO.getRepoName());
        setGitlabProjectId(repoDTO.getGitlabProjectId());
        setAccessToken(repoDTO.getAccessToken());
        setHostAddress(repoDTO.getHostAddress());
    }

    @Override
    public String toString() {
        return "Repo{" +
                "repoId=" + repoId +
                ", groups=" + groups +
                ", repoName='" + repoName + '\'' +
                ", gitlabProjectId=" + gitlabProjectId +
                ", accessToken='" + accessToken + '\'' +
                ", hostAddress='" + hostAddress + '\'' +
                '}';
    }

    /**
     * Overrides for comparing repo objects
     * @param o Object being compared
     * @return True or False
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Repo)) return false;
        Repo repo = (Repo) o;
        return repoId == repo.repoId && Objects.equals(groups, repo.groups) && Objects.equals(repoName, repo.repoName) && Objects.equals(gitlabProjectId, repo.gitlabProjectId) && Objects.equals(accessToken, repo.accessToken) && Objects.equals(hostAddress, repo.hostAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(repoId, groups, repoName, gitlabProjectId, accessToken, hostAddress);
    }


    /**
     * Builder class to build to Repo
     */
    public static class Builder{
        private int repoId;
        private Groups groups;
        private String repoName;
        private int gitlabProjectId;
        private String accessToken;
        private String hostAddress;

        /**
         * Updates the repoID
         * @param repoId Numerical ID of the repo
         * @return Repo builder
         */
        public Builder repoId(int repoId) {
            this.repoId = repoId;
            return this;
        }

        /**
         * Updates the group of the repo
         * @param groups The group of the repo
         * @return Repo builder
         */
        public Builder groups(Groups groups) {
            this.groups = groups;
            return this;
        }

        /**
         * Updates the repo's name
         * @param repoName The name of the repo
         * @return Repo builder
         */
        public Builder repoName(String repoName) {
            this.repoName = repoName;
            return this;
        }

        /**
         * Updates the repo's gitlab project Id
         * @param gitlabProjectId The Id of the gitlab project
         * @return Repo builder
         */
        public Builder gitlabProjectId(int gitlabProjectId) {
            this.gitlabProjectId = gitlabProjectId;
            return this;
        }

        /**
         * Updates the access token
         * @param accessToken The access token
         * @return Repo builder
         */
        public Builder accessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

        /**
         * Updates the repo's hostAddress
         * @param hostAddress The host address of the repo
         * @return Repo builder
         */
        public Builder hostAddress(String hostAddress) {
            this.hostAddress = hostAddress;
            return this;
        }

        /**
         * Build the Repo
         * @return The built repo
         */
        public Repo build(){
            return new Repo(repoId, groups, repoName, gitlabProjectId, accessToken, hostAddress);
        }
    }
}


