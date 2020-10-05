package de.uhd.ifi.se.decision.management.jira.persistence;

import java.util.ArrayList;
import java.util.List;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssueTextPersistenceManager;

/**
 * Responsible for automatic link creation (=creation of edges/relations)
 * between nodes in the {@link KnowledgeGraph}. Is currently only working for
 * decision knowledge elements documented in the description or the comments of
 * a Jira issue.
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
	public static long createSmartLinkForElement(KnowledgeElement element) {
		if (element == null) {
			return 0;
		}
		long linkId = element.isLinked();
		if (linkId > 0) {
			return linkId;
		}
		KnowledgeElement lastElement = getPotentialParentElement(element);
		linkId = KnowledgePersistenceManager.getOrCreate(element.getProject()).insertLink(lastElement, element, null);
		return linkId;
	}

	public static KnowledgeElement getPotentialParentElement(KnowledgeElement element) {
		if (element == null) {
			return null;
		}
		List<KnowledgeElement> potentialParentElements = getPotentialParentElements(element);
		if (potentialParentElements.isEmpty()) {
			return new KnowledgeElement(element.getJiraIssue());
		}
		if (potentialParentElements.size() == 2) {
			return getRecentlyUpdatedElement(potentialParentElements.get(0), potentialParentElements.get(1));
		}
		return potentialParentElements.get(0);
	}

	private static List<KnowledgeElement> getPotentialParentElements(KnowledgeElement element) {
		List<KnowledgeElement> potentialParentElements = new ArrayList<KnowledgeElement>();
		List<KnowledgeType> parentTypes = KnowledgeType.getParentTypes(element.getType());
		long jiraIssueId = ((PartOfJiraIssueText) element).getJiraIssue().getId();
		for (KnowledgeType parentType : parentTypes) {
			KnowledgeElement potentialParentElement = JiraIssueTextPersistenceManager
					.getYoungestElementForJiraIssue(jiraIssueId, parentType);
			if (potentialParentElement != null) {
				potentialParentElements.add(potentialParentElement);
			}
		}
		return potentialParentElements;
	}

	public static KnowledgeElement getRecentlyUpdatedElement(KnowledgeElement first, KnowledgeElement second) {
		if (first == null) {
			return second;
		}
		if (second == null) {
			return first;
		}
		if (first.getUpdatingDate().compareTo(second.getCreationDate()) > 0) {
			return first;
		}
		return second;
	}
}