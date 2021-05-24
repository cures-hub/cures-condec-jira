package de.uhd.ifi.se.decision.management.jira.persistence;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
		JiraIssueTextPersistenceManager persistenceManager = KnowledgePersistenceManager
				.getOrCreate(element.getProject()).getJiraIssueTextManager();
		List<KnowledgeElement> otherElements = persistenceManager
				.getElementsInJiraIssue(element.getJiraIssue().getId());
		KnowledgeElement potentialParentElement = getPotentialParentElement(element, otherElements);
		if (potentialParentElement == null) {
			return new KnowledgeElement(element.getJiraIssue());
		}
		return potentialParentElement;
	}

	public static KnowledgeElement getPotentialParentElement(KnowledgeElement element,
			List<KnowledgeElement> otherElements) {
		List<KnowledgeElement> potentialParentElements = getPotentialParentElements(element, otherElements);
		if (potentialParentElements.isEmpty()) {
			return null;
		}
		return getClosestParentElement(potentialParentElements, element);
	}

	public static List<KnowledgeElement> getPotentialParentElements(KnowledgeElement element,
			List<KnowledgeElement> otherElements) {
		List<KnowledgeElement> potentialParentElements = new ArrayList<KnowledgeElement>();
		List<KnowledgeType> parentTypes = KnowledgeType.getParentTypes(element.getType());
		for (KnowledgeType parentType : parentTypes) {
			potentialParentElements.addAll(otherElements.stream()
					.filter(otherElement -> otherElement.getType() == parentType).collect(Collectors.toList()));
		}
		return potentialParentElements;
	}

	/**
	 * @issue How to identify the correct recently updated element for automatic
	 *        link creation?
	 * @decision Compare ids of the elements and return the element with smallest
	 *           delta in ids.
	 * @con Only works for knowledge elements with the same documentation location.
	 * @alternative Compare dates of last update! Use element with highest id if all
	 *              elements were updated on the same day!
	 * @alternative Only compare update timestamps and return element with recent
	 *              timestamp!
	 * @con If a comment is changed, the entire Jira issues is marked as updated as
	 *      well. Solution options (=alternatives and decisions) documented in the
	 *      comment could be incorrectly linked to an issue in the description even
	 *      if an issue in the comment exists.
	 * 
	 * @param potentialParents
	 *            knowledge elements that might be the parent of the child element.
	 * @param childElement
	 *            unlinked element that should be linked.
	 * @return parent element, e.g. closest alternative for an argument.
	 */
	public static KnowledgeElement getClosestParentElement(List<KnowledgeElement> potentialParents,
			KnowledgeElement childElement) {
		return potentialParents.stream()
				.min(Comparator.comparing(
						potentialElement -> calculateDifference(childElement.getId(), potentialElement.getId())))
				.orElse(potentialParents.get(0));
	}

	public static long calculateDifference(long id1, long id2) {
		long difference = id1 < 0 ? id2 - id1 : id1 - id2;
		if (difference < 0) {
			return 42;
		}
		return difference;
	}
}