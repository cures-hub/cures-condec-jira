package de.uhd.ifi.se.decision.management.jira.consistency.checktriggers;


import com.atlassian.jira.event.issue.IssueEvent;

public class WorkflowDoneTrigger implements ConsistencyCheckEventTrigger {
	@Override
	public boolean isTriggered(IssueEvent issueEvent) {
		return issueEvent != null &&
			"workflow".equals(issueEvent.getParams().get("eventsource"))
			&& "done".equals(issueEvent.getIssue().getStatus().getStatusCategory().getKey());
	}

	@Override
	public String getName() {
		return "done";
	}
}
