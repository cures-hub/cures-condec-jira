package de.uhd.ifi.se.decision.management.jira.consistency.duplicatedetection;

import com.atlassian.jira.issue.Issue;
import de.uhd.ifi.se.decision.management.jira.consistency.suggestions.DuplicateFragment;

import java.util.List;

public interface DuplicateDetectionStrategy {

	/**
	 * This interface is used to implement the duplicate detection as a strategy pattern.
	 * The content of two issues is compared. When a duplicate part is detected, a new
	 * `DuplicateTextFragment` is created. Finally, all found `DuplicateTextFragment`s
	 * are returned.
	 *
	 * @param i1 base issue
	 * @param i2 issue to compare to
	 * @return List of all found duplicate text fragments in i2 of i1
	 * @throws Exception
	 */
	List<DuplicateFragment> detectDuplicateTextFragments(Issue i1, Issue i2) throws Exception;

}
