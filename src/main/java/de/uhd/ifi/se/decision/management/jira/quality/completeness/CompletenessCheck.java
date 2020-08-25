package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import static java.util.Map.entry;

import java.util.Map;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

/**
 * Checks whether a given {@link KnowledgeElement} is documented completely
 * according to the definition of done.
 * 
 * For example, an argument needs to be linked to at least one solution option
 * (decision or alternative) in the {@link KnowledgeGraph}. Otherwise, it is
 * incomplete, i.e., its documentation needs to be improved.
 */
public interface CompletenessCheck {
	static final Map<KnowledgeType, CompletenessCheck> completenessCheckMap = Map.ofEntries(
			entry(KnowledgeType.DECISION, new DecisionCompletenessCheck()),
			entry(KnowledgeType.ISSUE, new IssueCompletenessCheck()),
			entry(KnowledgeType.ALTERNATIVE, new AlternativeCompletenessCheck()));

	public boolean execute(KnowledgeElement childElement);

	/**
	 * @issue Should knowledge elements without definition of done be assumed to be
	 *        complete or incomplete?
	 * @decision If no definition of done can be found, the knowledge element is
	 *           assumed to be complete!
	 */
	public static boolean isElementComplete(KnowledgeElement element) {
		CompletenessCheck completenessCheck = completenessCheckMap.get(element.getType());
		return completenessCheck == null || completenessCheck.execute(element);
	}
}
