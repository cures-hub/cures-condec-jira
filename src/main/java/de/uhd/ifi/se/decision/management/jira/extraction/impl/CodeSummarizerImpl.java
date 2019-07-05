package de.uhd.ifi.se.decision.management.jira.extraction.impl;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.jgit.revwalk.RevCommit;

import com.atlassian.jira.issue.Issue;

import de.uhd.ifi.se.decision.management.jira.extraction.CodeSummarizer;
import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;
import de.uhd.ifi.se.decision.management.jira.extraction.TangledCommitDetection;
import de.uhd.ifi.se.decision.management.jira.model.git.ChangedFile;
import de.uhd.ifi.se.decision.management.jira.model.git.Diff;

public class CodeSummarizerImpl implements CodeSummarizer {

	private GitClient gitClient;
	private int minProbabilityOfCorrectness;
	private String projectKey;
	private String issueKey;

	public CodeSummarizerImpl(String projectKey) {
		this.projectKey = projectKey;
		this.gitClient = new GitClientImpl(projectKey);
	}

	public CodeSummarizerImpl(GitClient gitClient) {
		this.gitClient = gitClient;
	}

	// TODO Add overloaded method createSummary(Issue jiraIssue)
	@Override
	public String createSummary(Issue jiraIssue, int minProbabilityOfCorrectness) {
		if (jiraIssue == null) {
			return "";
		}
		this.minProbabilityOfCorrectness = minProbabilityOfCorrectness;
		this.issueKey = jiraIssue.getKey();
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

	@Override
	public String createSummary(Diff diff) {
		if (diff == null || diff.getChangedFiles().size() == 0) {
			return "";
		}

		TangledCommitDetection tangledCommitDetection = new TangledCommitDetectionImpl();
		tangledCommitDetection.estimateWhetherChangedFilesAreCorrectlyIncludedInDiff(diff);

		return generateSummary(diff);
	}

	private String generateSummary(Diff diff) {
		String rows = "";
		for (ChangedFile changedFile : diff.getChangedFiles()) {
			if (changedFile.getProbabilityOfCorrectness() >= this.minProbabilityOfCorrectness) {
				rows += this.addRow(this.addTableItem(FilenameUtils.removeExtension(changedFile.getFile().getName()),
						this.summarizeMethods(changedFile), Float.toString(changedFile.getProbabilityOfCorrectness())));
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

	private String generateTable(String rows) {
		return "<table style=\"width:100%; border: 1px solid black; border-collapse: collapse;\">" + "<tr>\n"
				+ "    <th style=\"border: 1px solid black; border-collapse: collapse; padding: 15px; text-align: left;\">Class Name</th>\n"
				+ "    <th style=\"border: 1px solid black; border-collapse: collapse; padding: 15px; text-align: left;\">Method Names</th> \n"
				+ "    <th style=\"border: 1px solid black; border-collapse: collapse; padding: 15px; text-align: left;\">Probability of Correct Link</th>\n"
				+ "</tr>\n" + rows + "</table>";
	}

	private String addRow(String tableItem) {
		return "<tr>\n" + tableItem + "</tr>\n";
	}

	private String addTableItem(String item1, String item2, String item3) {
		return "<td style=\"border: 1px solid black; border-collapse: collapse; padding: 15px; text-align: left;\">"
				+ item1 + "</td>\n"
				+ "<td style=\"border: 1px solid black; border-collapse: collapse; padding: 15px; text-align: left;\">"
				+ item2 + "</td>\n"
				+ "<td style=\"border: 1px solid black; border-collapse: collapse; padding: 15px; text-align: left;\">"
				+ item3 + "% </td>\n";
	}

	public String getProjectKey() {
		return projectKey;
	}

	public String getIssueKey() {
		return issueKey;
	}
}