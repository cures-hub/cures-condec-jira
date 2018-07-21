package de.uhd.ifi.se.decision.management.jira.persistence;

import java.util.ArrayList;
import java.util.Iterator;
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

	public abstract DecisionKnowledgeElement insertDecisionKnowledgeElement(DecisionKnowledgeElement element,
			ApplicationUser user);

	public abstract boolean updateDecisionKnowledgeElement(DecisionKnowledgeElement element, ApplicationUser user);

	public abstract boolean deleteDecisionKnowledgeElement(DecisionKnowledgeElement element, ApplicationUser user);

	public abstract List<DecisionKnowledgeElement> getDecisionKnowledgeElements();

	public abstract DecisionKnowledgeElement getDecisionKnowledgeElement(String key);

	public abstract DecisionKnowledgeElement getDecisionKnowledgeElement(long id);

	public List<DecisionKnowledgeElement> getDecisionKnowledgeElements(KnowledgeType type) {
		List<DecisionKnowledgeElement> elements = this.getDecisionKnowledgeElements();
		Iterator<DecisionKnowledgeElement> iterator = elements.iterator();
		while (iterator.hasNext()) {
			DecisionKnowledgeElement element = iterator.next();
			if (element.getType() != type) {
				iterator.remove();
			}
		}
		return elements;
	}

	public abstract List<DecisionKnowledgeElement> getElementsLinkedWithOutwardLinks(DecisionKnowledgeElement element);

	public abstract List<DecisionKnowledgeElement> getElementsLinkedWithInwardLinks(DecisionKnowledgeElement element);

	public List<DecisionKnowledgeElement> getLinkedElements(DecisionKnowledgeElement element) {
		List<DecisionKnowledgeElement> linkedElements = new ArrayList<DecisionKnowledgeElement>();
		linkedElements.addAll(this.getElementsLinkedWithOutwardLinks(element));
		linkedElements.addAll(this.getElementsLinkedWithInwardLinks(element));
		return linkedElements;
	}

	public List<DecisionKnowledgeElement> getLinkedElements(long id) {
		DecisionKnowledgeElement element = this.getDecisionKnowledgeElement(id);
		return this.getLinkedElements(element);
	}

	public List<DecisionKnowledgeElement> getUnlinkedElements(long id) {
		List<DecisionKnowledgeElement> elements = this.getDecisionKnowledgeElements();
		DecisionKnowledgeElement rootElement = this.getDecisionKnowledgeElement(id);
		if (rootElement == null) {
			return elements;
		}
		elements.remove(rootElement);

		List<DecisionKnowledgeElement> linkedElements = this.getLinkedElements(rootElement);
		elements.removeAll(linkedElements);

		return elements;
	}

	public abstract long insertLink(Link link, ApplicationUser user);

	public abstract boolean deleteLink(Link link, ApplicationUser user);

	public abstract List<Link> getInwardLinks(DecisionKnowledgeElement element);

	public abstract List<Link> getOutwardLinks(DecisionKnowledgeElement element);
}
