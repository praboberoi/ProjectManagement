package nz.ac.canterbury.seng302.portfolio.model.dto;

import nz.ac.canterbury.seng302.portfolio.model.Groups;

import java.util.Objects;

/**
 * The repoDTO entity stored in the database for the portfolio application
 */

public class RepoDTO {

    private int repoId;

    private Groups groups;

    private String repoName;

    private int gitlabProjectId;

    private String hostAddress;


    public Groups getGroups() {return this.groups;}

    public int getRepoId() {return this.repoId;}

    public String getRepoName() {return this.repoName;}

    public int getGitlabProjectId() {return this.gitlabProjectId;}

    public String getHostAddress() {return this.hostAddress;}




    /**
     * Creates a new repoDTO object with the provided details
     * @param repoId The ID of the repo object
     * @param groups Group of the repo object
     * @param repoName Name of the repo object
     * @param gitlabProjectId The ID of the gitlab project
     * @param hostAddress The host address
     */
    public RepoDTO(int repoId, Groups groups, String repoName, int gitlabProjectId, String hostAddress){
        this.repoId = repoId;
        this.groups = groups;
        this.repoName = repoName;
        this.gitlabProjectId = gitlabProjectId;
        this.hostAddress = hostAddress;
    }

    /**
     * Overrides for comparing repoDTO objects
     * @param o Object being compared
     * @return True or False
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RepoDTO)) return false;
        RepoDTO repoDTO = (RepoDTO) o;
        return repoId == repoDTO.repoId && Objects.equals(groups, repoDTO.groups) && Objects.equals(repoName, repoDTO.repoName) && Objects.equals(gitlabProjectId, repoDTO.gitlabProjectId) && Objects.equals(hostAddress, repoDTO.hostAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(repoId, groups, repoName, gitlabProjectId, hostAddress);
    }

    /**
     * Builder class to build to RepoDTO object
     */
    public static class Builder{
        private int repoId;
        private Groups groups;
        private String repoName;
        private int gitlabProjectId;
        private String hostAddress;

        /**
         * Builds the current Builder with the given repo id
         * @param repoId Numerical ID of the repo
         * @return The current Builder.
         */
        public RepoDTO.Builder repoId(int repoId) {
            this.repoId = repoId;
            return this;
        }

        /**
         * Builds the current Builder with the given group
         * @param groups The group of the repo
         * @return The current Builder.
         */
        public RepoDTO.Builder groups(Groups groups) {
            this.groups = groups;
            return this;
        }

        /**
         * Builds the current Builder with the given repo name
         * @param repoName The name of the repo
         * @return The current Builder.
         */
        public RepoDTO.Builder repoName(String repoName) {
            this.repoName = repoName;
            return this;
        }

        /**
         * Builds the current Builder with the given gitlabprojectID
         * @param gitlabProjectId The Id of the gitlab project
         * @return The current Builder.
         */
        public RepoDTO.Builder gitlabProjectId(int gitlabProjectId) {
            this.gitlabProjectId = gitlabProjectId;
            return this;
        }

        /**
         * Builds the current Builder with the given host address
         * @param hostAddress The host address of the repo
         * @return The current Builder.
         */
        public RepoDTO.Builder hostAddress(String hostAddress) {
            this.hostAddress = hostAddress;
            return this;
        }

        /**
         * Returns a new RepoDTO with all the parameters of the current Builder
         * @return The RepoDTO object
         */
        public RepoDTO build(){
            return new RepoDTO(repoId, groups, repoName, gitlabProjectId, hostAddress);
        }
    }


}
