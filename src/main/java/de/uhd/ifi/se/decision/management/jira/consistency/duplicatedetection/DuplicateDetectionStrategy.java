package de.uhd.ifi.se.decision.management.jira.consistency.duplicatedetection;

import de.uhd.ifi.se.decision.management.jira.consistency.suggestions.DuplicateSuggestion;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;

import java.util.List;

public interface DuplicateDetectionStrategy {

	/**
	 * This interface is used to implement the duplicate detection as a strategy pattern.
	 * The content of two issues is compared. When a duplicate part is detected, a new
	 * `DuplicateTextFragment` is created. Finally, all found `DuplicateTextFragment`s
	 * are returned.
	 *
	 * @param baseElement base issue
	 * @param compareElement issue to compare to
	 * @return List of all found duplicate text fragments in i2 of i1
	 * @throws Exception
	 */
	List<DuplicateSuggestion> detectDuplicateTextFragments(KnowledgeElement baseElement, KnowledgeElement compareElement) throws Exception;

}

