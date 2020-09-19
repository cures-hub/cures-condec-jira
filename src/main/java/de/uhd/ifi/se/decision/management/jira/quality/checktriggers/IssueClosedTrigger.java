package de.uhd.ifi.se.decision.management.jira.quality.checktriggers;

import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventType;


public class IssueClosedTrigger extends TriggerChain {

	public IssueClosedTrigger(IssueEvent issueEvent) {
		super.setIssueEvent(issueEvent);
	}

	public IssueClosedTrigger() {
	}

	@Override
	public boolean isTriggered() {
		return super.getIssueEvent() != null && EventType.ISSUE_CLOSED_ID.equals(super.getIssueEvent().getEventTypeId());
	}

	@Override
	public String getName() {
		return "consistency-closed";
	}


}
