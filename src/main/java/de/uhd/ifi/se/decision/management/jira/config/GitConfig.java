package de.uhd.ifi.se.decision.management.jira.config;

//TODO Creates a connection to the given Git Repository
public class GitConfig {


    private String path;

    public GitConfig(String path){
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
