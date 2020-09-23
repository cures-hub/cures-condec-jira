package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;

import java.util.Map;

import static java.util.Map.entry;

final public class CompletenessHandler {

	private CompletenessHandler() {}

	private static final Map<KnowledgeType, CompletenessCheck> completenessCheckMap = Map.ofEntries(
		entry(KnowledgeType.DECISION, new DecisionCompletenessCheck()),
		entry(KnowledgeType.ISSUE, new IssueCompletenessCheck()),
		entry(KnowledgeType.ALTERNATIVE, new AlternativeCompletenessCheck()),
		entry(KnowledgeType.ARGUMENT, new ArgumentCompletenessCheck()),
		entry(KnowledgeType.PRO, new ArgumentCompletenessCheck()),
		entry(KnowledgeType.CON, new ArgumentCompletenessCheck()));


	/**
	 * @issue Should knowledge elements without definition of done be assumed to be
	 *        complete or incomplete?
	 * @decision If no definition of done can be found, the knowledge element is
	 *           assumed to be complete!
	 */
	public static boolean checkForCompletion(KnowledgeElement knowledgeElement) {
		CompletenessCheck completenessCheck = completenessCheckMap.get(knowledgeElement.getType());
		return completenessCheck == null || completenessCheck.execute(knowledgeElement);
	}

	/**
	 * Iterates recursively over the knowledge graph of @param source {@link KnowledgeElement} and
	 * @return true if there is at least one incompletely documented knowledge element, else it returns false.
	 */
	public static boolean hasIncompleteKnowledgeLinked(KnowledgeElement source) {
		if (source.isIncomplete()) {
			return true;
		}
		else if (source.isLinked() != 0) {
			for (Link link : source.getLinks()
				 ) {
				if (link.getTarget().isIncomplete()) {
					return true;
				} else if (hasIncompleteKnowledgeLinked(link.getTarget(), source)){
					return true;
				}
			}
		}
		return false;
	}


	public static boolean hasIncompleteKnowledgeLinked(KnowledgeElement target, KnowledgeElement source) {
		if (target.isLinked() != 0) {
			for (Link link : target.getLinks()) {
				KnowledgeElement nextTarget = link.getTarget();
				if (nextTarget.equals(source)) {
					continue;
				}
				else if (nextTarget.isIncomplete()) {
					return true;
				} else if (hasIncompleteKnowledgeLinked(nextTarget, target)) {
					return true;
				}
			}
		}
		return false;
	}

}
