package de.uhd.ifi.se.decision.management.jira.git;

import java.util.List;

import de.uhd.ifi.se.decision.management.jira.git.model.ChangedFile;
import de.uhd.ifi.se.decision.management.jira.git.model.Diff;
import de.uhd.ifi.se.decision.management.jira.git.model.DiffForSingleRepository;

/**
 * Estimates whether a {@link DiffForSingleRepository} of {@ChangedFile}s
 * contains wrong links, i.e., is tangled.
 */
public class TangledChangeDetector {

	/**
	 * Currently, this function calls
	 * {@link #calculatePackageDistances(DiffForSingleRepository)}. After the
	 * package distances are set, the {@link ChangedFile}s will be sorted by their
	 * package distances. Subsequently, the package distance will be normalized and
	 * represented as a probability of correctness. This function can be updated
	 * later if we decided to use more than one metric for our prediction.
	 *
	 * @param diff
	 *            The {@link Diff} might be a single git commit, a whole feature
	 *            branch (with many commits), or all commits belonging to a JIRA
	 *            issue.
	 */
	public void estimateWhetherChangedFilesAreCorrectlyIncludedInDiff(Diff diff) {
		this.calculatePackageDistances(diff);
		this.standardization(diff);
	}

	/**
	 * Calculates a distance for each {@link ChangedFile}, which depends on the
	 * package distances between other ChangedFiles.
	 *
	 * @param diff
	 *            The {@link Diff} might be a single git commit, a whole feature
	 *            branch (with many commits), or all commits belonging to a JIRA
	 *            issue.
	 * @return two-dimensional integer matrix with package distances.
	 */
	public int[][] calculatePackageDistances(Diff diff) {
		List<ChangedFile> changedFiles = diff.getChangedFiles();

		int numberOfFiles = changedFiles.size();
		int[][] matrix = new int[numberOfFiles][numberOfFiles];

		if (numberOfFiles == 0) {
			return matrix;
		}

		if (numberOfFiles == 1) {
			changedFiles.get(0).setPackageDistance(100);
			return matrix;
		}

		for (int i = 0; i < numberOfFiles; i++) {
			matrix[i] = calculatePackageDistance(changedFiles.get(i), changedFiles);
		}

		return matrix;
	}

	private int[] calculatePackageDistance(ChangedFile file, List<ChangedFile> changedFiles) {
		int[] packageDistances = new int[changedFiles.size()];
		for (int j = 0; j < changedFiles.size(); j++) {
			packageDistances[j] = calculatePackageDistance(file, changedFiles.get(j));
		}
		return packageDistances;
	}

	private int calculatePackageDistance(ChangedFile fileA, ChangedFile fileB) {
		List<String> leftPackageDeclaration = fileA.getPartsOfPackageDeclaration();
		List<String> rightPackageDeclaration = fileB.getPartsOfPackageDeclaration();
		if (fileA.equals(fileB)) {
			return 0;
		}

		int packageDistance = calculatePackageDistance(leftPackageDeclaration, rightPackageDeclaration);
		fileA.setPackageDistance(fileA.getPackageDistance() + packageDistance);
		return packageDistance;
	}

	private int calculatePackageDistance(List<String> leftPackageDeclaration, List<String> rightPackageDeclaration) {

		if (leftPackageDeclaration.size() < rightPackageDeclaration.size()) {
			return calculatePackageDistance(rightPackageDeclaration, leftPackageDeclaration);
		}

		for (int k = 0; k < rightPackageDeclaration.size(); k++) {
			String leftPackageSegment = leftPackageDeclaration.get(k);
			String rightPackageSegment = rightPackageDeclaration.get(k);
			if (!leftPackageSegment.equals(rightPackageSegment)) {
				return leftPackageDeclaration.size() - k;
			} else if (isLastSegment(rightPackageDeclaration, k)) {
				return leftPackageDeclaration.size() - rightPackageDeclaration.size();
			}
		}

		return 0;
	}

	private boolean isLastSegment(List<String> packageDeclaration, int segmentNumber) {
		return packageDeclaration.size() - segmentNumber == 1;
	}

	/**
	 * Normalizes the result of
	 * {@link #calculatePackageDistances(DiffForSingleRepository)}, from integer
	 * package distances into a percentage value. The changed file(s) with the
	 * lowest package distance is/are are estimated as correctly linked, i.e. set to
	 * 100%.
	 *
	 * @param diff
	 *            The {@link Diff} might be a single git commit, a whole feature
	 *            branch (with many commits), or all commits belonging to a JIRA
	 *            issue.
	 */
	public void standardization(Diff diff) {
		diff.getChangedFiles()
				.sort((ChangedFile c1, ChangedFile c2) -> c1.getPackageDistance() - c2.getPackageDistance());
		if (diff.getChangedFiles().size() > 1) {
			float max = diff.getChangedFiles().get(diff.getChangedFiles().size() - 1).getPackageDistance();
			float min = diff.getChangedFiles().get(0).getPackageDistance();
			for (ChangedFile changedFile : diff.getChangedFiles()) {
				changedFile.setProbabilityOfCorrectness(((max - changedFile.getPackageDistance()) / (max - min)) * 100);
			}
		} else if (!diff.getChangedFiles().isEmpty()) {
			diff.getChangedFiles().get(0).setProbabilityOfCorrectness(100);
		}
	}

}
