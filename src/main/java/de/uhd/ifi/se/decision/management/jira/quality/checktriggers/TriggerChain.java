package de.uhd.ifi.se.decision.management.jira.quality.checktriggers;


import com.atlassian.jira.event.issue.IssueEvent;

/**
 * Implementation of the next of responsibility pattern.
 */
public abstract class TriggerChain implements QualityCheckEventTrigger {

	private TriggerChain nextChainLink;
	private IssueEvent issueEvent;

	public void setNextChain(TriggerChain next) {
		this.nextChainLink = next; }

	public TriggerChain getNextChain() {
		return this.nextChainLink;
	}

	public void setIssueEvent(IssueEvent event) {
		this.issueEvent = event;
	}


	public boolean calculate() {
		boolean activated = this.isTriggered() && this.isActivated();
		if (!activated && getNextChain() != null) {
			activated = getNextChain().calculate();
		}
		return activated;
	}

	protected IssueEvent getIssueEvent(){
		return this.issueEvent;
	}

	@Override
	public String getCurrentProjectKey() {
		return getIssueEvent().getProject().getKey();
	}
}
