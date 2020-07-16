package de.uhd.ifi.se.decision.management.jira.consistency.checktriggers;


/**
 * Implementation of the chain of responsibility pattern.
 */
public abstract class TriggerChain implements ConsistencyCheckEventTrigger {

	private TriggerChain nextChainLink;

	public TriggerChain setNextChain(TriggerChain chain) {
		this.nextChainLink = chain;
		return chain;
	}

	public TriggerChain getNextChain() {
		return this.nextChainLink;
	}

	public boolean calculate() {
		boolean activated = this.isTriggered() && this.isActivated();
		if (!activated && getNextChain() != null) {
			activated = getNextChain().calculate();
		}
		return activated;
	}

}
