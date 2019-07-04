package de.uhd.ifi.se.decision.management.jira.extraction;

import de.uhd.ifi.se.decision.management.jira.model.git.ChangedFile;
import de.uhd.ifi.se.decision.management.jira.model.git.Diff;

/**
 * Interface for the estimation whether a {@link Diff} of {@ChangedFile}s
 * contains wrong links, i.e., is tangled.
 */
public interface TangledCommitDetection {

	/**
	 * Currently, this function calls {@link #calculatePackageDistances(Diff)}.
	 * After the package distances are set, the {@link ChangedFile}s will be sorted
	 * by their package distances. Subsequently, the package distance will be
	 * normalized and represented as a probability of correctness. This function can
	 * be updated later if we decided to use more than one metric for our
	 * prediction.
	 *
	 * @param diff
	 *            The {@link Diff} might be a single git commit, a whole feature
	 *            branch (with many commits), or all commits belonging to a JIRA
	 *            issue.
	 */
	void estimateWhetherChangedFilesAreCorrectlyIncludedInDiff(Diff diff);

	/**
	 * Calculate for each {@link ChangedFile} a distance, which depends on the
	 * package distances between other ChangedFiles.
	 *
	 * @param diff
	 *            The {@link Diff} might be a single git commit, a whole feature
	 *            branch (with many commits), or all commits belonging to a JIRA
	 *            issue.
	 * @return two-dimensional integer matrix with package distances.
	 */
	int[][] calculatePackageDistances(Diff diff);

	/**
	 * Normalizes the result of  {@link #calculatePackageDistances(Diff)}, from integer package
	 * distances into a percentage value. The changed file(s) with the lowest package
	 * distance is/are are estimated as correctly linked, i.e. set to 100%.
	 *
	 * @param diff
	 *            The {@link Diff} might be a single git commit, a whole feature
	 *            branch (with many commits), or all commits belonging to a JIRA
	 *            issue.
	 */
	void standardization(Diff diff);
}
