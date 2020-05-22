package de.uhd.ifi.se.decision.management.jira.eventlistener;

import com.atlassian.jira.event.issue.IssueEvent;

public interface IssueEventListener {

	/**
	 * Event listener method.
	 *
	 * @param issueEvent that triggered the listener
	 */
	void onIssueEvent(IssueEvent issueEvent);
}
