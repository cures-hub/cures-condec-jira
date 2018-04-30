package de.uhd.ifi.se.decision.documentation.jira.persistence;

import java.util.ArrayList;
import java.util.List;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.documentation.jira.model.IDecisionKnowledgeElement;
import de.uhd.ifi.se.decision.documentation.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.documentation.jira.model.Link;

/**
 * @description Abstract Class to create, edit, delete and retrieve decision
 *              knowledge elements and their links
 */
public abstract class PersistenceStrategy {

	public abstract IDecisionKnowledgeElement insertDecisionKnowledgeElement(
			IDecisionKnowledgeElement decisionKnowledgeElement, ApplicationUser user);

	public abstract boolean updateDecisionKnowledgeElement(IDecisionKnowledgeElement decisionKnowledgeElement,
			ApplicationUser user);

	public abstract boolean deleteDecisionKnowledgeElement(IDecisionKnowledgeElement decisionKnowledgeElement,
			ApplicationUser user);

	public abstract List<IDecisionKnowledgeElement> getDecisionKnowledgeElements(String projectKey);

	public abstract IDecisionKnowledgeElement getDecisionKnowledgeElement(String key);

	public abstract IDecisionKnowledgeElement getDecisionKnowledgeElement(long id);

	public List<IDecisionKnowledgeElement> getDecisions(String projectKey) {
		List<IDecisionKnowledgeElement> decisionKnowledgeElements = this.getDecisionKnowledgeElements(projectKey);
		List<IDecisionKnowledgeElement> decisions = new ArrayList<IDecisionKnowledgeElement>();
		for (IDecisionKnowledgeElement decisionKnowledgeElement : decisionKnowledgeElements) {
			if (decisionKnowledgeElement.getType().toString().equalsIgnoreCase("Decision")) {
				decisions.add(decisionKnowledgeElement);
			}
		}
		return decisions;
	}

	public abstract List<IDecisionKnowledgeElement> getChildren(IDecisionKnowledgeElement decisionKnowledgeElement);
	
	public List<IDecisionKnowledgeElement> getChildren(long id) {
		IDecisionKnowledgeElement decisionKnowledgeElement = this.getDecisionKnowledgeElement(id);
		return this.getChildren(decisionKnowledgeElement);		
	}

	public abstract List<IDecisionKnowledgeElement> getParents(IDecisionKnowledgeElement decisionKnowledgeElement);

	public List<IDecisionKnowledgeElement> getUnlinkedDecisionComponents(long id, String projectKey){
		IDecisionKnowledgeElement rootElement = this.getDecisionKnowledgeElement(id);
		if (rootElement == null) {
			return new ArrayList<IDecisionKnowledgeElement>();
		}
		List<IDecisionKnowledgeElement> decisionKnowledgeElements = this.getDecisionKnowledgeElements(projectKey);
		List<IDecisionKnowledgeElement> unlinkedDecisionComponents = new ArrayList<IDecisionKnowledgeElement>();
		List<IDecisionKnowledgeElement> outwardElements = this.getChildren(rootElement);
		for (IDecisionKnowledgeElement decisionKnowledgeElement : decisionKnowledgeElements) {
			if (decisionKnowledgeElement.getId() == id
					|| decisionKnowledgeElement.getType() == KnowledgeType.DECISION) {
				continue;
			}
			boolean linked = false;
			for (IDecisionKnowledgeElement element : outwardElements) {
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

	public abstract long insertLink(Link link, ApplicationUser user);

	public abstract boolean deleteLink(Link link, ApplicationUser user);

	public abstract List<Link> getInwardLinks(IDecisionKnowledgeElement element);

	public abstract List<Link> getOutwardLinks(IDecisionKnowledgeElement element);
}
