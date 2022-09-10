package nz.ac.canterbury.seng302.portfolio.model.dto;

import java.util.Objects;

/**
 * The repoDTO entity stored in the database for the portfolio application
 */

public class RepoDTO {

    private int repoId;

    private int groupId;

    private String repoName;

    private int gitlabProjectId;

    private String accessToken;

    private String hostAddress;


    public int getGroupId() {return this.groupId;}

    public int getRepoId() {return this.repoId;}

    public String getRepoName() {return this.repoName;}

    public int getGitlabProjectId() {return this.gitlabProjectId;}

    public String getAccessToken() {return this.getAccessToken();}

    public String getHostAddress() {return this.hostAddress;}




    /**
     * Creates a new repoDTO object with the provided details
     * @param repoId The ID of the repo object
     * @param groupId GroupId of the repo object
     * @param repoName Name of the repo object
     * @param gitlabProjectId The ID of the gitlab project
     * @param accessToken The access token
     * @param hostAddress The host address
     */
    public RepoDTO(int repoId, int groupId, String repoName, int gitlabProjectId, String accessToken, String hostAddress){
        this.repoId = repoId;
        this.groupId = groupId;
        this.repoName = repoName;
        this.gitlabProjectId = gitlabProjectId;
        this.accessToken = accessToken;
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
        return repoId == repoDTO.repoId && Objects.equals(groupId, repoDTO.groupId) && Objects.equals(repoName, repoDTO.repoName) && Objects.equals(gitlabProjectId, repoDTO.gitlabProjectId) && Objects.equals(accessToken, repoDTO.accessToken) && Objects.equals(hostAddress, repoDTO.hostAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(repoId, groupId, repoName, gitlabProjectId, accessToken, hostAddress);
    }

    /**
     * Builder class to build to RepoDTO object
     */
    public static class Builder{
        private int repoId;
        private int groupId;
        private String repoName;
        private int gitlabProjectId;
        private String accessToken;
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
         * @param groupId The groupId of the repo
         * @return The current Builder.
         */
        public RepoDTO.Builder groupId(int groupId) {
            this.groupId = groupId;
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
         * Builds the repo's access token
         * @param accessToken The access token of the repo
         * @return Repo builder
         */
        public RepoDTO.Builder accessToken(String accessToken) {
            this.accessToken = accessToken;
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
            return new RepoDTO(repoId, groupId, repoName, gitlabProjectId, accessToken, hostAddress);
        }
    }


}
