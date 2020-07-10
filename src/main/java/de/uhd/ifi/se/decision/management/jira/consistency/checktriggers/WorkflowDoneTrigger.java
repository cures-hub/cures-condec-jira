package de.uhd.ifi.se.decision.management.jira.consistency.checktriggers;


import com.atlassian.jira.event.issue.IssueEvent;

public class WorkflowDoneTrigger implements ConsistencyCheckEventTrigger, TriggerChain<ConsistencyCheckEventTrigger> {

	private IssueEvent issueEvent;
	private TriggerChain nextChainLink;

	public WorkflowDoneTrigger(IssueEvent issueEvent) {
		this.issueEvent = issueEvent;
	}

	public WorkflowDoneTrigger() {
	}


	public void setIssueEvent(IssueEvent event) {
		this.issueEvent = event;
	}

	@Override
	public boolean isTriggered() {
		return issueEvent != null &&
			"workflow".equals(issueEvent.getParams().get("eventsource"))
			&& "done".equals(issueEvent.getIssue().getStatus().getStatusCategory().getKey());
	}


	@Override
	public String getCurrentProjectKey() {
		return this.issueEvent.getProject().getKey();
	}

	@Override
	public String getName() {
		return "done";
	}

	@Override
	public ConsistencyCheckEventTrigger getChainBase() {
		return this;
	}


	@Override
	public TriggerChain<ConsistencyCheckEventTrigger> setNextChain(TriggerChain<ConsistencyCheckEventTrigger> chain) {
		this.nextChainLink = chain;
		return chain;
	}

	@Override
	public TriggerChain getNextChain() {
		return this.nextChainLink;
	}

	@Override
	public boolean calculate() {
		boolean activated = isTriggered() && isActivated();
		if (!activated && getNextChain() != null) {
			activated = getNextChain().calculate();
		}
		return activated;
	}
}
