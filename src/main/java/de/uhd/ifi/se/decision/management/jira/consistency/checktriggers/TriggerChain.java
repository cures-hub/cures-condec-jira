package de.uhd.ifi.se.decision.management.jira.consistency.checktriggers;


/**
 * Implementation of the chain of responsibility pattern.
 */
public interface TriggerChain<T extends ConsistencyCheckEventTrigger> {


	TriggerChain<T> setNextChain(TriggerChain<T> chain);

	TriggerChain<T> getNextChain();

	T getChainBase();

	public default boolean hasNextChain(){
		return getNextChain() != null;
	}

	boolean calculate();
}
