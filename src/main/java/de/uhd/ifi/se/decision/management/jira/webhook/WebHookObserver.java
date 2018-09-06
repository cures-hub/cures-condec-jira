package de.uhd.ifi.se.decision.management.jira.webhook;

public class WebHookObserver  {
    private String projectKey;
    private WebConnector connector;

    public WebHookObserver(String projectKey){
        this.projectKey = projectKey;
        connector = new WebConnector(projectKey);
    }

    public boolean sendIssueChanges(String issueKey){
        return connector.sendWebHookForIssueKey(projectKey,issueKey);
    }
}
