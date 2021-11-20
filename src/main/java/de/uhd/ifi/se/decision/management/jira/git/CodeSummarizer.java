package de.uhd.ifi.se.decision.management.jira.git;

import org.apache.commons.io.FilenameUtils;

import com.atlassian.jira.issue.Issue;

import de.uhd.ifi.se.decision.management.jira.git.model.ChangedFile;
import de.uhd.ifi.se.decision.management.jira.git.model.Diff;

/**
 * Creates a summary of code changes linked to Jira issues (e.g. to work items).
 *
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
 * 
 * @issue Currently, the filtering of classes regarding the probability of
 *        correct linkage is done in the backend. How can we filter in the Java
 *        script side?
 */
public class CodeSummarizer {

	private GitClient gitClient;
	private int minProbabilityOfCorrectness;
	private boolean formatForComments;

	public CodeSummarizer(String projectKey) {
		this.gitClient = GitClient.getInstance(projectKey);
	}

	public CodeSummarizer(GitClient gitClient) {
		this.gitClient = gitClient;
	}

	/**
	 * Creates a summary of code changes for all commits associated to a Jira issue.
	 * 
	 * @param jiraIssue
	 *            Jira issue. Its key is searched for in commit messages.
	 * @param minProbabilityOfCorrectness
	 *            probabilityOfCorrectness. Integer value for filter over
	 *            correctness
	 * @return summary as a String.
	 */
	public String createSummary(Issue jiraIssue, int minProbabilityOfCorrectness) {
		if (jiraIssue == null) {
			return "";
		}
		this.minProbabilityOfCorrectness = minProbabilityOfCorrectness;
		Diff diff = gitClient.getDiffForJiraIssueOnDefaultBranchAndFeatureBranches(jiraIssue);
		return createSummary(diff);
	}

	/**
	 * Creates a summary of code changes for a diff.
	 * 
	 * @param diff
	 *            object of {@link Diff} class containing {@link ChangedFile}s.
	 * @return summary as a String.
	 */
	public String createSummary(Diff diff) {
		if (diff == null || diff.getChangedFiles().isEmpty()) {
			return "";
		}

		diff.getChangedFiles().removeIf(changedFile -> !changedFile.isExistingJavaFile());
		TangledChangeDetector tangledCommitDetection = new TangledChangeDetector();
		tangledCommitDetection.estimateWhetherChangedFilesAreCorrectlyIncludedInDiff(diff);

		if (formatForComments) {
			return generateSummaryForJiraIssueComment(diff);
		}
		return generateSummaryForHtmlDialog(diff);
	}

	private static String generateSummaryForJiraIssueComment(Diff diff) {
		String summary = "The following classes were changed: ";
		for (ChangedFile changedFile : diff.getChangedFiles()) {
			summary += changedFile.getName() + "; ";
		}
		return summary;
	}

	private String generateSummaryForHtmlDialog(Diff diff) {
		String rows = "";
		for (ChangedFile changedFile : diff.getChangedFiles()) {
			if (changedFile.getProbabilityOfCorrectness() >= minProbabilityOfCorrectness) {
				rows += addRow(addTableItem(FilenameUtils.removeExtension(changedFile.getName()),
						summarizeMethods(changedFile),
						String.format("%.2f", changedFile.getProbabilityOfCorrectness())));
			}
		}
		return generateTable(rows);
	}

	private String summarizeMethods(ChangedFile changedFile) {
		String summarizedMethods = "";
		for (String methodDeclaration : changedFile.getMethodDeclarations()) {
			summarizedMethods += methodDeclaration + "<br/>";
		}
		return summarizedMethods;
	}

	// TODO The table should be built on the frontend, not here. Only the data
	// should be transmitted.
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

	public void setFormatForComments(boolean formatForComments) {
		this.formatForComments = formatForComments;
	}
}