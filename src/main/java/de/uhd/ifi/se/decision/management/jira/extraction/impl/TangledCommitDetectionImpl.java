package de.uhd.ifi.se.decision.management.jira.extraction.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.PackageDeclaration;

import de.uhd.ifi.se.decision.management.jira.extraction.ChangedFile;
import de.uhd.ifi.se.decision.management.jira.extraction.Diff;
import de.uhd.ifi.se.decision.management.jira.extraction.TangledCommitDetection;

public class TangledCommitDetectionImpl implements TangledCommitDetection {

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

	@Override
	public void calculatePredication(Diff diff) {
		this.calculatePackageDistances(diff);
		diff.getChangedFiles()
				.sort((ChangedFile c1, ChangedFile c2) -> c1.getPackageDistance() - c2.getPackageDistance());
		this.standardization(diff);
	}

	@Override
	public void calculatePackageDistances(Diff diff) {
		Integer[][] maxtrix = new Integer[diff.getChangedFiles().size()][diff.getChangedFiles().size()];
		if (diff.getChangedFiles().size() > 1) {
			for (int i = 0; i < diff.getChangedFiles().size(); i++) {
				List<String> leftPackageDeclaration = this
						.parsePackage(diff.getChangedFiles().get(i).getCompilationUnit().getPackageDeclaration());
				for (int j = 0; j < diff.getChangedFiles().size(); j++) {
					List<String> rightPackageDeclaration = this
							.parsePackage(diff.getChangedFiles().get(j).getCompilationUnit().getPackageDeclaration());
					if (i != j) {
						if (leftPackageDeclaration.size() >= rightPackageDeclaration.size()) {
							for (int k = 0; k < rightPackageDeclaration.size(); k++) {
								if (!leftPackageDeclaration.get(k).equals(rightPackageDeclaration.get(k))) {
									diff.getChangedFiles().get(i)
											.setPackageDistance(diff.getChangedFiles().get(i).getPackageDistance()
													+ (leftPackageDeclaration.size() - k));
									maxtrix[i][j] = leftPackageDeclaration.size() - k;
									break;
								}
							}
						} else {
							for (int k = 0; k < leftPackageDeclaration.size(); k++) {
								if (!leftPackageDeclaration.get(k).equals(rightPackageDeclaration.get(k))) {
									diff.getChangedFiles().get(i)
											.setPackageDistance(diff.getChangedFiles().get(i).getPackageDistance()
													+ (rightPackageDeclaration.size() - k));
									maxtrix[i][j] = rightPackageDeclaration.size() - k;
									break;
								}
							}
						}
					} else {
						maxtrix[i][j] = 0;
					}
				}
			}
		} else {
			diff.getChangedFiles().get(0).setPackageDistance(0);
		}
	}

	@Override
	public List<String> parsePackage(Optional<PackageDeclaration> op) {
		return new ArrayList<>(Arrays.asList(op.get().toString().split("\\.")));
	}

}
