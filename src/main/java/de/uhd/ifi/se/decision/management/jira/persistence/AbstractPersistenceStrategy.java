package de.uhd.ifi.se.decision.management.jira.persistence;

import java.util.ArrayList;
import java.util.List;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;

/**
 * Abstract class to create, edit, delete and retrieve decision knowledge
 * elements and their links
 */
public abstract class AbstractPersistenceStrategy {

	private String projectKey;

	public AbstractPersistenceStrategy() {
	}

	public AbstractPersistenceStrategy(String projectKey) {
		this.setProjectKey(projectKey);
	}

	public abstract DecisionKnowledgeElement insertDecisionKnowledgeElement(
			DecisionKnowledgeElement decisionKnowledgeElement, ApplicationUser user);

	public abstract boolean updateDecisionKnowledgeElement(DecisionKnowledgeElement decisionKnowledgeElement,
			ApplicationUser user);

	public abstract boolean deleteDecisionKnowledgeElement(DecisionKnowledgeElement decisionKnowledgeElement,
			ApplicationUser user);

	public abstract List<DecisionKnowledgeElement> getDecisionKnowledgeElements(String projectKey);

	public List<DecisionKnowledgeElement> getDecisionKnowledgeElements() {
		return this.getDecisionKnowledgeElements(this.projectKey);
	};

	public List<DecisionKnowledgeElement> getDecisionKnowledgeElements(String projectKey, KnowledgeType type) {
		List<DecisionKnowledgeElement> decisionKnowledgeElements = this.getDecisionKnowledgeElements(projectKey);
		List<DecisionKnowledgeElement> decisionKnowledgeElementsWithType = new ArrayList<DecisionKnowledgeElement>();
		for (DecisionKnowledgeElement decisionKnowledgeElement : decisionKnowledgeElements) {
			if (decisionKnowledgeElement.getType() == type) {
				decisionKnowledgeElementsWithType.add(decisionKnowledgeElement);
			}
		}
		return decisionKnowledgeElementsWithType;
	}

	public List<DecisionKnowledgeElement> getDecisionKnowledgeElements(KnowledgeType type) {
		return this.getDecisionKnowledgeElements(this.projectKey, type);
	};

	public List<DecisionKnowledgeElement> getDecisions(String projectKey) {
		return getDecisionKnowledgeElements(projectKey, KnowledgeType.DECISION);
	}

	public List<DecisionKnowledgeElement> getDecisions() {
		return getDecisionKnowledgeElements(this.projectKey, KnowledgeType.DECISION);
	}

	public abstract DecisionKnowledgeElement getDecisionKnowledgeElement(String key);

	public abstract DecisionKnowledgeElement getDecisionKnowledgeElement(long id);

	public abstract List<DecisionKnowledgeElement> getElementsLinkedWithOutwardLinks(
			DecisionKnowledgeElement decisionKnowledgeElement);

	public abstract List<DecisionKnowledgeElement> getElementsLinkedWithInwardLinks(
			DecisionKnowledgeElement decisionKnowledgeElement);

	public List<DecisionKnowledgeElement> getLinkedElements(DecisionKnowledgeElement decisionKnowledgeElement) {
		List<DecisionKnowledgeElement> linkedElements = new ArrayList<DecisionKnowledgeElement>();
		linkedElements.addAll(this.getElementsLinkedWithOutwardLinks(decisionKnowledgeElement));
		linkedElements.addAll(this.getElementsLinkedWithInwardLinks(decisionKnowledgeElement));
		return linkedElements;
	}

	public List<DecisionKnowledgeElement> getLinkedElements(long id) {
		DecisionKnowledgeElement decisionKnowledgeElement = this.getDecisionKnowledgeElement(id);
		return this.getLinkedElements(decisionKnowledgeElement);
	}

	public List<DecisionKnowledgeElement> getUnlinkedDecisionComponents(long id, String projectKey) {
		DecisionKnowledgeElement rootElement = this.getDecisionKnowledgeElement(id);
		if (rootElement == null) {
			return new ArrayList<DecisionKnowledgeElement>();
		}
		List<DecisionKnowledgeElement> decisionKnowledgeElements = this.getDecisionKnowledgeElements(projectKey);
		List<DecisionKnowledgeElement> unlinkedDecisionComponents = new ArrayList<DecisionKnowledgeElement>();
		List<DecisionKnowledgeElement> outwardElements = this.getLinkedElements(rootElement);
		for (DecisionKnowledgeElement decisionKnowledgeElement : decisionKnowledgeElements) {
			if (decisionKnowledgeElement.getId() == id
					|| decisionKnowledgeElement.getType() == KnowledgeType.DECISION) {
				continue;
			}
			boolean linked = false;
			for (DecisionKnowledgeElement element : outwardElements) {
				if (element.getId() == decisionKnowledgeElement.getId()) {
					linked = true;
					break;
				}
			}
			if (!linked) {
				unlinkedDecisionComponents.add(decisionKnowledgeElement);
			}
		}
		return unlinkedDecisionComponents;
	}

	public List<DecisionKnowledgeElement> getUnlinkedDecisionComponents(long id) {
		return getUnlinkedDecisionComponents(id, this.projectKey);
	}

	public abstract long insertLink(Link link, ApplicationUser user);

	public abstract boolean deleteLink(Link link, ApplicationUser user);

	public abstract List<Link> getInwardLinks(DecisionKnowledgeElement element);

	public abstract List<Link> getOutwardLinks(DecisionKnowledgeElement element);

	public String getProjectKey() {
		return projectKey;
	}

	public void setProjectKey(String projectKey) {
		this.projectKey = projectKey;
	}
}
