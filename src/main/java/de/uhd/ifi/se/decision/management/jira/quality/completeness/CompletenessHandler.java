package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

import java.util.Map;

import static java.util.Map.entry;

public class CompletenessHandler {

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

}
