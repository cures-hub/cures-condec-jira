package de.uhd.ifi.se.decision.management.jira.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;

public interface PersistenceInterface {

	/**
	 * Persistence instances that are identified by the project key.
	 */
	public static Map<String, PersistenceInterface> instances = new HashMap<String, PersistenceInterface>();

	public static PersistenceInterface getOrCreate(String projectKey) {
		if (projectKey == null) {
			return null;
		}
		if (instances.containsKey(projectKey)) {
			return instances.get(projectKey);
		}
		PersistenceInterface persistenceInterface = new PersistenceInterfaceImpl(projectKey);
		instances.put(projectKey, persistenceInterface);
		return instances.get(projectKey);
	}

	public List<DecisionKnowledgeElement> getDecisionKnowledgeElements();

	public static List<DecisionKnowledgeElement> getDecisionKnowledgeElements(String projectKey) {
		return PersistenceInterface.getOrCreate(projectKey).getDecisionKnowledgeElements();
	}
}
