package de.uhd.ifi.se.decision.management.jira.persistence.impl;

import java.util.ArrayList;
import java.util.List;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;

/**
 * Class responsible for automatic link creation (=edges) between nodes in the
 * {@link KnowledgeGraph}. Currently only working for decision knowledge
 * elements documented in the description or the comments of a certain Jira
 * issue.
 * 
 * @see Link
 * @see JiraIssueTextPersistenceManager
 */
public class AutomaticLinkCreator {

	/**
	 * Links arguments to the youngest parent alternative or decision. Links
	 * alternatives or decisions to the youngest parent issue.
	 * 
	 * @param element
	 *            to be linked. Currently only working for decision knowledge
	 *            elements documented in the description or the comments of a
	 *            certain Jira issue.
	 * @return link id.
	 */
	public static long createSmartLinkForElement(DecisionKnowledgeElement element) {
		if (element == null) {
			return 0;
		}
		long linkId = element.isLinked();
		if (linkId > 0) {
			return linkId;
		}
		DecisionKnowledgeElement lastElement = getPotentialParentElement(element);
		linkId = KnowledgePersistenceManager.getOrCreate(element.getProject()).insertLink(lastElement, element, null);
		return linkId;
	}

	public static DecisionKnowledgeElement getPotentialParentElement(DecisionKnowledgeElement element) {
		if (element == null) {
			return null;
		}
		List<DecisionKnowledgeElement> potentialParentElements = getPotentialParentElements(element);
		if (potentialParentElements.isEmpty()) {
			return new DecisionKnowledgeElementImpl(element.getJiraIssue());
		}
		if (potentialParentElements.size() == 2) {
			return getMostRecentElement(potentialParentElements.get(0), potentialParentElements.get(1));
		}
		return potentialParentElements.get(0);
	}

	private static List<DecisionKnowledgeElement> getPotentialParentElements(DecisionKnowledgeElement element) {
		List<DecisionKnowledgeElement> potentialParentElements = new ArrayList<DecisionKnowledgeElement>();
		List<KnowledgeType> parentTypes = KnowledgeType.getParentTypes(element.getType());
		long jiraIssueId = ((PartOfJiraIssueText) element).getJiraIssueId();
		for (KnowledgeType parentType : parentTypes) {
			DecisionKnowledgeElement potentialParentElement = JiraIssueTextPersistenceManager
					.getYoungestElementForJiraIssue(jiraIssueId, parentType);
			if (potentialParentElement != null) {
				potentialParentElements.add(potentialParentElement);
			}
		}
		return potentialParentElements;
	}

	public static DecisionKnowledgeElement getMostRecentElement(DecisionKnowledgeElement first,
			DecisionKnowledgeElement second) {
		if (first == null) {
			return second;
		}
		if (second == null) {
			return first;
		}
		if (first.getCreated().compareTo(second.getCreated()) > 0) {
			return first;
		}
		return second;
	}
}