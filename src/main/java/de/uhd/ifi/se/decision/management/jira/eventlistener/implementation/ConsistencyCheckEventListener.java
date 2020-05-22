package de.uhd.ifi.se.decision.management.jira.eventlistener.implementation;

import com.atlassian.jira.event.issue.IssueEvent;
import de.uhd.ifi.se.decision.management.jira.eventlistener.IssueEventListener;

public class ConsistencyCheckEventListener implements IssueEventListener {

	public void onIssueEvent(IssueEvent issueEvent) {
		System.out.println(issueEvent.getChangeLog().toString());
	}

}
