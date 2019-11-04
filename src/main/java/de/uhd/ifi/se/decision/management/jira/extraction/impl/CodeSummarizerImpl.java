package de.uhd.ifi.se.decision.management.jira.extraction.impl;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.jgit.revwalk.RevCommit;

import com.atlassian.jira.issue.Issue;

import de.uhd.ifi.se.decision.management.jira.extraction.CodeSummarizer;
import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;
import de.uhd.ifi.se.decision.management.jira.extraction.TangledChangeDetector;
import de.uhd.ifi.se.decision.management.jira.model.git.ChangedFile;
import de.uhd.ifi.se.decision.management.jira.model.git.Diff;

public class CodeSummarizerImpl implements CodeSummarizer {

	private GitClient gitClient;
	private int minProbabilityOfCorrectness;

	public CodeSummarizerImpl(String projectKey) {
		this.gitClient = new GitClientImpl(projectKey);
	}

	public CodeSummarizerImpl(GitClient gitClient) {
		this.gitClient = gitClient;
	}

	/**
	 * TODO: Enable filtering in Java script
	 * 
	 * @issue Currently, the filtering of classes regarding the probability of
	 *        correct linkage is done in the backend. How can we filter in the Java
	 *        script side?
	 */
	@Override
	public String createSummary(Issue jiraIssue, int minProbabilityOfCorrectness) {
		if (jiraIssue == null) {
			return "";
		}
		this.minProbabilityOfCorrectness = minProbabilityOfCorrectness;
		Diff diff = gitClient.getDiff(jiraIssue);
		return createSummary(diff);
	}

	@Override
	public String createSummary(RevCommit commit) {
		if (commit == null) {
			return "";
		}
		Diff diff = gitClient.getDiff(commit);
		return createSummary(diff);
	}

	/**
	 * @issue Which file types should be shown in the summary of code changes?
	 * @decision Only include Java files into the summary of code changes!
	 * @pro The package distance to predict whether a change is tangled/trace link
	 *      is wrong can only be calculated for Java classes in packages.
	 * 
	 * @issue Should the test classes be integrated into the summary of code
	 *        changes?
	 * @decision Integrate test class in the summary of code changes!
	 * @pro If both the "normal" and the test class are changed together, the change
	 *      might be untangled and the package distance returns a better prediction.
	 */
	@Override
	public String createSummary(Diff diff) {
		if (diff == null || diff.getChangedFiles().size() == 0) {
			return "";
		}

		diff.getChangedFiles().removeIf(changedFile -> !changedFile.isExistingJavaClass());
		TangledChangeDetector tangledCommitDetection = new TangledChangeDetectorImpl();
		tangledCommitDetection.estimateWhetherChangedFilesAreCorrectlyIncludedInDiff(diff);

		return generateSummary(diff);
	}

	private String generateSummary(Diff diff) {
		String rows = "";
		for (ChangedFile changedFile : diff.getChangedFiles()) {
			if (changedFile.getProbabilityOfCorrectness() >= this.minProbabilityOfCorrectness) {
				rows += this.addRow(this.addTableItem(FilenameUtils.removeExtension(changedFile.getFile().getName()),
						this.summarizeMethods(changedFile),
						String.format("%.2f", changedFile.getProbabilityOfCorrectness())));
			}
		}
		return this.generateTable(rows);
	}

	private String summarizeMethods(ChangedFile changedFile) {
		String summarizedMethods = "";
		for (String methodDeclaration : changedFile.getMethodDeclarations()) {
			summarizedMethods += methodDeclaration + "<br/>";
		}
		return summarizedMethods;
	}

	// TODO The table should be built on the frontend, not here. Only the data should be transmitted.
	private String generateTable(String rows) {
		return "<table style=\"width:100%; border: 1px solid black; border-collapse: collapse;\">" + "<tr>\n"
				+ "    <th style=\"width:40%; border: 1px solid black; border-collapse: collapse; padding: 15px; text-align: left;\">Class Name</th>\n"
				+ "    <th style=\"width:40%; border: 1px solid black; border-collapse: collapse; padding: 15px; text-align: left;\">Method Names</th> \n"
				+ "    <th style=\"width:20%; border: 1px solid black; border-collapse: collapse; padding: 15px; text-align: left;\">Probability of Correct Link</th>\n"
				+ "</tr>\n" + rows + "</table>";
	}

	private String addRow(String tableItem) {
		return "<tr>\n" + tableItem + "</tr>\n";
	}

	private String addTableItem(String item1, String item2, String item3) {
		return "<td style=\"width:40%; border: 1px solid black; border-collapse: collapse; padding: 15px; text-align: left;\">"
				+ item1 + "</td>\n"
				+ "<td style=\"width:40%; border: 1px solid black; border-collapse: collapse; padding: 15px; text-align: left;\">"
				+ item2 + "</td>\n"
				+ "<td style=\"width:20%; border: 1px solid black; border-collapse: collapse; padding: 15px; text-align: left;\">"
				+ item3 + "% </td>\n";
	}
}