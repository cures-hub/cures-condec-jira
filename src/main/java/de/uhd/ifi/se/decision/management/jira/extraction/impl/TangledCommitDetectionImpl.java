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
		Integer[][] matrix = new Integer[diff.getChangedFiles().size()][diff.getChangedFiles().size()];
		if (diff.getChangedFiles().size() > 1) {
			for (int i = 0; i < diff.getChangedFiles().size(); i++) {
				List<String> leftPackageDeclaration = diff.getChangedFiles().get(i).getPackageName();
				for (int j = 0; j < diff.getChangedFiles().size(); j++) {
					List<String> rightPackageDeclaration = diff.getChangedFiles().get(j).getPackageName();
					if (i != j) {
						if (leftPackageDeclaration.size() >= rightPackageDeclaration.size()) {
							for (int k = 0; k < rightPackageDeclaration.size(); k++) {
								if (!leftPackageDeclaration.get(k).equals(rightPackageDeclaration.get(k))) {
									diff.getChangedFiles().get(i)
											.setPackageDistance(diff.getChangedFiles().get(i).getPackageDistance()
													+ (leftPackageDeclaration.size() - k));
									matrix[i][j] = leftPackageDeclaration.size() - k;
									break;
								} else if ((rightPackageDeclaration.size() - 1) == k
										&& (leftPackageDeclaration.get(k).equals(rightPackageDeclaration.get(k)))) {
									diff.getChangedFiles().get(i)
											.setPackageDistance(diff.getChangedFiles().get(i).getPackageDistance()
													+ (leftPackageDeclaration.size() - rightPackageDeclaration.size()));
									matrix[i][j] = leftPackageDeclaration.size() - rightPackageDeclaration.size();
								}
							}
						} else {
							for (int k = 0; k < leftPackageDeclaration.size(); k++) {
								if (!leftPackageDeclaration.get(k).equals(rightPackageDeclaration.get(k))) {
									diff.getChangedFiles().get(i)
											.setPackageDistance(diff.getChangedFiles().get(i).getPackageDistance()
													+ (rightPackageDeclaration.size() - k));
									matrix[i][j] = rightPackageDeclaration.size() - k;
									break;
								} else if (leftPackageDeclaration.get(k).equals(rightPackageDeclaration.get(k))
										&& (k == leftPackageDeclaration.size() - 1)) {
									diff.getChangedFiles().get(i)
											.setPackageDistance(diff.getChangedFiles().get(i).getPackageDistance()
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
		} else {
			diff.getChangedFiles().get(0).setPackageDistance(100);
		}
	}

	@Override
	public void standardization(Diff diff) {
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
