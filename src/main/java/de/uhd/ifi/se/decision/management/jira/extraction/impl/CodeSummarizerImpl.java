package de.uhd.ifi.se.decision.management.jira.extraction.impl;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.revwalk.RevCommit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.issue.Issue;

import de.uhd.ifi.se.decision.management.jira.extraction.ChangedFile;
import de.uhd.ifi.se.decision.management.jira.extraction.CodeSummarizer;
import de.uhd.ifi.se.decision.management.jira.extraction.Diff;
import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;
import de.uhd.ifi.se.decision.management.jira.extraction.TangledCommitDetection;

public class CodeSummarizerImpl implements CodeSummarizer {

	private GitClient gitClient;
	private static final Logger LOGGER = LoggerFactory.getLogger(CodeSummarizerImpl.class);
	private int minProbabilityOfTangledness;
	private String projectKey;
	private String issueKey;

	public CodeSummarizerImpl(String projectKey) {
		this.projectKey = projectKey;
		this.gitClient = new GitClientImpl(projectKey);
	}
	
	public CodeSummarizerImpl(GitClient gitClient) {
		this.gitClient = gitClient;
	}

	@Override
	public String createSummary(Issue jiraIssue, int minProbabilityOfTangledness) {
		if (jiraIssue == null) {
			return "";
		}
		this.minProbabilityOfTangledness = minProbabilityOfTangledness;
		this.issueKey = jiraIssue.getKey();
		Map<DiffEntry, EditList> diff = gitClient.getDiff(jiraIssue);
		return createSummary(diff);
	}

	@Override
	public String createSummary(RevCommit commit) {
		if (commit == null) {
			return "";
		}
		Map<DiffEntry, EditList> diff = gitClient.getDiff(commit);
		return createSummary(diff);
	}

	@Override
	public String createSummary(Map<DiffEntry, EditList> diff) {
		if (diff == null || diff.size() == 0) {
			return "";
		}
		Diff allDiffs = new DiffImpl();
		for (Map.Entry<DiffEntry, EditList> entry : diff.entrySet()) {
			File file = new File(gitClient.getDirectory().toString().replace(".git", "") + entry.getKey().getNewPath());
			if (file.exists() && FilenameUtils.getExtension(file.toString()) != null
					&& FilenameUtils.getExtension(file.toString()).equalsIgnoreCase("java")) {
				allDiffs.addChangedFile(new ChangedFileImpl(file));
			}
		}
		try {
			TangledCommitDetection.getMethods(allDiffs);
			TangledCommitDetection tangledCommitDetection = new TangledCommitDetectionImpl();
			tangledCommitDetection.calculatePredication(allDiffs);
		} catch (Exception e) {
			LOGGER.error(e.toString());
			return "";
		}
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = "";
		try {
			jsonString = mapper.writeValueAsString(allDiffs);
			System.out.println(jsonString);
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			Diff.sendPost(this.projectKey, this.issueKey, jsonString);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return generateSummary(allDiffs);
	}

	private String generateSummary(Diff diff) {
		String rows = "";
		for (ChangedFile changedFile : diff.getChangedFiles()) {
			if (changedFile.getProbabilityOfTangledness() >= this.minProbabilityOfTangledness) {
				rows += this.addRow(this.addTableItem(FilenameUtils.removeExtension(changedFile.getFile().getName()),
						this.summarizeMethods(changedFile), Float.toString(changedFile.getProbabilityOfTangledness())));
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

	// TODO Change "Probability of Tangledness" in "Probability of Correct Link" or
	// "Probability of Untangledness". 100% should represent a correct link, 0% a
	// wrong link.
	private String generateTable(String rows) {
		return "<table style=\"width:100%; border: 1px solid black; border-collapse: collapse;\">" + "<tr>\n"
				+ "    <th style=\"border: 1px solid black; border-collapse: collapse; padding: 15px; text-align: left;\">Class Name</th>\n"
				+ "    <th style=\"border: 1px solid black; border-collapse: collapse; padding: 15px; text-align: left;\">Method Names</th> \n"
				+ "    <th style=\"border: 1px solid black; border-collapse: collapse; padding: 15px; text-align: left;\">Probability of Tangledness</th>\n"
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
}