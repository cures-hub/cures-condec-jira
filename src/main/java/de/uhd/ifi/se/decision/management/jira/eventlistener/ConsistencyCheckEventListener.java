package de.uhd.ifi.se.decision.management.jira.eventlistener;

import com.atlassian.jira.event.issue.IssueEvent;

public class ConsistencyCheckEventListener {

	public void onIssueEvent(IssueEvent issueEvent) {
		System.out.println(issueEvent.getChangeLog().toString());
	}

}
