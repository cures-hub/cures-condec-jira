package de.uhd.ifi.se.decision.management.jira.eventlistener.implementation;

import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventType;
import com.atlassian.jira.issue.Issue;
import de.uhd.ifi.se.decision.management.jira.eventlistener.IssueEventListener;

public class ConsistencyCheckEventListener implements IssueEventListener {

	public ConsistencyCheckEventListener() {
	}

	public void onIssueEvent(IssueEvent issueEvent) {
		Long eventTypeId = issueEvent.getEventTypeId();
		Issue issue = issueEvent.getIssue();
		if ("done".equals(issue.getStatus().getSimpleStatus().getStatusCategory().getKey())) {
			System.out.println("Issue has been set to done.");
		} else if (eventTypeId.equals(EventType.ISSUE_CLOSED_ID)) {
			System.out.println("Issue {} has been closed at {}.");
		}
	}

}
