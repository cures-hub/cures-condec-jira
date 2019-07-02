package de.uhd.ifi.se.decision.management.jira.extraction.impl;

import java.util.List;

import de.uhd.ifi.se.decision.management.jira.extraction.ChangedFile;
import de.uhd.ifi.se.decision.management.jira.extraction.Diff;
import de.uhd.ifi.se.decision.management.jira.extraction.TangledCommitDetection;

public class TangledCommitDetectionImpl implements TangledCommitDetection {

	@Override
	public void calculatePredication(Diff diff) {
		this.calculatePackageDistances(diff);
		this.standardization(diff);
	}

	@Override
	public void calculatePackageDistances(Diff diff) {
		int numberOfFiles = diff.getChangedFiles().size();
		int[][] matrix = new int[numberOfFiles][numberOfFiles];

		List<ChangedFile> changedFiles = diff.getChangedFiles();

		if (changedFiles.isEmpty()) {
			return;
		}

		if (changedFiles.size() == 1) {
			changedFiles.get(0).setPackageDistance(100);
			return;
		}

		for (int i = 0; i < numberOfFiles; i++) {
			List<String> leftPackageDeclaration = changedFiles.get(i).getPackageName();
			for (int j = 0; j < numberOfFiles; j++) {
				List<String> rightPackageDeclaration = changedFiles.get(j).getPackageName();
				if (i != j) {
					if (leftPackageDeclaration.size() >= rightPackageDeclaration.size()) {
						for (int k = 0; k < rightPackageDeclaration.size(); k++) {
							if (!leftPackageDeclaration.get(k).equals(rightPackageDeclaration.get(k))) {
								changedFiles.get(i).setPackageDistance(
										changedFiles.get(i).getPackageDistance() + (leftPackageDeclaration.size() - k));
								matrix[i][j] = leftPackageDeclaration.size() - k;
								break;
							} else if ((rightPackageDeclaration.size() - 1) == k
									&& (leftPackageDeclaration.get(k).equals(rightPackageDeclaration.get(k)))) {
								changedFiles.get(i).setPackageDistance(changedFiles.get(i).getPackageDistance()
										+ (leftPackageDeclaration.size() - rightPackageDeclaration.size()));
								matrix[i][j] = leftPackageDeclaration.size() - rightPackageDeclaration.size();
							}
						}
					} else {
						for (int k = 0; k < leftPackageDeclaration.size(); k++) {
							if (!leftPackageDeclaration.get(k).equals(rightPackageDeclaration.get(k))) {
								changedFiles.get(i).setPackageDistance(changedFiles.get(i).getPackageDistance()
										+ (rightPackageDeclaration.size() - k));
								matrix[i][j] = rightPackageDeclaration.size() - k;
								break;
							} else if (leftPackageDeclaration.get(k).equals(rightPackageDeclaration.get(k))
									&& (k == leftPackageDeclaration.size() - 1)) {
								changedFiles.get(i).setPackageDistance(changedFiles.get(i).getPackageDistance()
										+ (rightPackageDeclaration.size() - leftPackageDeclaration.size()));
								matrix[i][j] = rightPackageDeclaration.size() - leftPackageDeclaration.size();
								break;
							}
						}
					}
				} else {
					matrix[i][j] = 0;
				}
			}
		}
	}

	public int packageDistance(ChangedFile file1, ChangedFile fileB) {
		return 0;
	}

	@Override
	public void standardization(Diff diff) {
		diff.getChangedFiles()
				.sort((ChangedFile c1, ChangedFile c2) -> c1.getPackageDistance() - c2.getPackageDistance());
		if (diff.getChangedFiles().size() > 1) {
			float max = diff.getChangedFiles().get(diff.getChangedFiles().size() - 1).getPackageDistance();
			float min = diff.getChangedFiles().get(0).getPackageDistance();
			for (ChangedFile changedFile : diff.getChangedFiles()) {
				changedFile.setProbabilityOfCorrectness(((max - changedFile.getPackageDistance()) / (max - min)) * 100);
			}
		} else {
			diff.getChangedFiles().get(0).setProbabilityOfCorrectness(100);
		}
	}

}
