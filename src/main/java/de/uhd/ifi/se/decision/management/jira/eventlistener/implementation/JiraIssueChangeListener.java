package de.uhd.ifi.se.decision.management.jira.eventlistener.implementation;

import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.issue.Issue;

import de.uhd.ifi.se.decision.management.jira.eventlistener.IssueEventListener;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;

public class JiraIssueChangeListener implements IssueEventListener {

	@Override
	public void onIssueEvent(IssueEvent issueEvent) {
		String projectKey = issueEvent.getProject().getKey();
		Issue jiraIssue = issueEvent.getIssue();
		KnowledgeElement element = new KnowledgeElement(jiraIssue);
		KnowledgeGraph.getInstance(projectKey).updateElement(element);
	}

}
