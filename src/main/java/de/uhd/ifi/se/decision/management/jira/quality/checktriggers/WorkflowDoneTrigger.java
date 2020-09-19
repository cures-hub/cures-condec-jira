package de.uhd.ifi.se.decision.management.jira.quality.checktriggers;


import com.atlassian.jira.event.issue.IssueEvent;

public class WorkflowDoneTrigger extends TriggerChain {

	public WorkflowDoneTrigger(IssueEvent issueEvent) {
		super.setIssueEvent(issueEvent);
	}

	public WorkflowDoneTrigger() {
	}

	@Override
	public boolean isTriggered() {
		return super.getIssueEvent() != null &&
			"workflow".equals(super.getIssueEvent().getParams().get("eventsource"))
			&& "done".equals(super.getIssueEvent().getIssue().getStatus().getStatusCategory().getKey());
	}

	@Override
	public String getName() {
		return "done";
	}

}
