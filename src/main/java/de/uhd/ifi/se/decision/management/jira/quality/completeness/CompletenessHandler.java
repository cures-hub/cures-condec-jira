package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import static java.util.Map.entry;

import java.util.Map;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.Recommendation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;

public final class CompletenessHandler {

	private CompletenessHandler() {
	}

	private static final Map<KnowledgeType, CompletenessCheck> completenessCheckMap = Map.ofEntries(
			entry(KnowledgeType.DECISION, new DecisionCompletenessCheck()),
			entry(KnowledgeType.ISSUE, new IssueCompletenessCheck()),
			entry(KnowledgeType.ALTERNATIVE, new AlternativeCompletenessCheck()),
			entry(KnowledgeType.ARGUMENT, new ArgumentCompletenessCheck()),
			entry(KnowledgeType.PRO, new ArgumentCompletenessCheck()),
			entry(KnowledgeType.CON, new ArgumentCompletenessCheck()),
			entry(KnowledgeType.CODE, new CodeCompletenessCheck()));

	/**
	 * @issue Should knowledge elements without definition of done be assumed to be
	 *        complete or incomplete?
	 * @decision If no definition of done can be found, the knowledge element is
	 *           assumed to be complete!
	 * 
	 * @param knowledgeElement
	 *            instance of {@link KnowledgeElement}.
	 * @return true if the element is completely documented according to the default
	 *         and configured rules of the {@link DefinitionOfDone}.
	 */
	public static boolean checkForCompleteness(KnowledgeElement knowledgeElement) {
		if (knowledgeElement instanceof Recommendation) {
			return true;
		}
		CompletenessCheck completenessCheck = completenessCheckMap.get(knowledgeElement.getType());
		return completenessCheck == null || completenessCheck.execute(knowledgeElement);
	}

	/**
	 * Iterates recursively over the knowledge graph of the
	 * {@link KnowledgeElement}.
	 * 
	 * @return true if there is at least one incompletely documented knowledge
	 *         element, else it returns false.
	 */
	public static boolean hasIncompleteKnowledgeLinked(KnowledgeElement knowledgeElement) {
		if (knowledgeElement.isIncomplete()) {
			return true;
		}
		for (Link link : knowledgeElement.getLinks()) {
			KnowledgeElement oppositeElement = link.getOppositeElement(knowledgeElement);
			if (oppositeElement.isIncomplete()) {
				return true;
			} else if (hasIncompleteKnowledgeLinked(oppositeElement, knowledgeElement)) {
				return true;
			}
		}
		return false;
	}

	public static boolean hasIncompleteKnowledgeLinked(KnowledgeElement oppositeElement,
			KnowledgeElement knowledgeElement) {
		for (Link link : oppositeElement.getLinks()) {
			KnowledgeElement nextElement = link.getOppositeElement(oppositeElement);
			if (nextElement.getId() == knowledgeElement.getId()) {
				continue;
			} else if (nextElement.isIncomplete()) {
				return true;
			}
		}
		return false;
	}
}
