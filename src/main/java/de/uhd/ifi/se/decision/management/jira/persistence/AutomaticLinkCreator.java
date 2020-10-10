package de.uhd.ifi.se.decision.management.jira.persistence;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.atlassian.jira.issue.Issue;

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

	public static long createSmartLinkForSentenceIfRelevant(PartOfJiraIssueText sentence) {
		if (sentence.isRelevant()) {
			return createSmartLinkForElement(sentence);
		}
		return 0;
	}

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
		return getRecentlyUpdatedElement(potentialParentElements);
	}

	private static List<KnowledgeElement> getPotentialParentElements(KnowledgeElement element) {
		Issue jiraIssue = ((PartOfJiraIssueText) element).getJiraIssue();
		if (jiraIssue == null) {
			return new ArrayList<KnowledgeElement>();
		}
		List<KnowledgeElement> potentialParentElements = new ArrayList<KnowledgeElement>();
		List<KnowledgeType> parentTypes = KnowledgeType.getParentTypes(element.getType());
		JiraIssueTextPersistenceManager persistenceManager = KnowledgePersistenceManager
				.getOrCreate(element.getProject()).getJiraIssueTextManager();
		for (KnowledgeType parentType : parentTypes) {
			KnowledgeElement potentialParentElement = persistenceManager
					.getYoungestElementForJiraIssue(jiraIssue.getId(), parentType);
			if (potentialParentElement != null) {
				potentialParentElements.add(potentialParentElement);
			}
		}
		return potentialParentElements;
	}

	public static KnowledgeElement getRecentlyUpdatedElement(List<KnowledgeElement> elements) {
		if (elements.stream()
				.allMatch(element -> element.getUpdatingDate().equals(elements.get(0).getUpdatingDate()))) {
			return elements.stream().max(Comparator.comparing(KnowledgeElement::getId)).orElse(elements.get(0));
		}
		return elements.stream().max(Comparator.comparing(KnowledgeElement::getUpdatingDate)).orElse(elements.get(0));
	}
}