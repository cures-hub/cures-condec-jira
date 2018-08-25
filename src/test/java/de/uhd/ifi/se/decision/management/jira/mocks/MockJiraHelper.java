package de.uhd.ifi.se.decision.management.jira.mocks;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.plugin.webfragment.model.JiraHelper;
import com.atlassian.jira.project.Project;

public class MockJiraHelper extends JiraHelper {
    @Override
    public Project getProject(){
        return ComponentAccessor.getProjectManager().getProjectByCurrentKey("TEST");
    }
}
