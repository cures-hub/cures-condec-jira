package de.uhd.ifi.se.decision.management.jira.extraction.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import de.uhd.ifi.se.decision.management.jira.extraction.TangledCommitDetection;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.revwalk.RevCommit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javaparser.ast.body.MethodDeclaration;

import de.uhd.ifi.se.decision.management.jira.extraction.CodeSummarizer;
import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;

public class CodeSummarizerImpl implements CodeSummarizer {

	private GitClient gitClient;
	private boolean useHtml;
	private static final Logger LOGGER = LoggerFactory.getLogger(CodeSummarizerImpl.class);

	public CodeSummarizerImpl(GitClient gitClient, boolean useHtml) {
		this.gitClient = gitClient;
		this.useHtml = useHtml;
	}

	public CodeSummarizerImpl(String projectKey, boolean useHtml) {
		this.gitClient = new GitClientImpl(projectKey);
		this.useHtml = useHtml;
	}

	public CodeSummarizerImpl(String projectKey) {
		this(projectKey, false);
	}

	@Override
	public String createSummary(String jiraIssueKey) {
		if (jiraIssueKey == null || jiraIssueKey.equalsIgnoreCase("")) {
			return "";
		}
		Map<DiffEntry, EditList> diff = gitClient.getDiff(jiraIssueKey);
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
		String summary = "";
		Diff allDiffs = new Diff();
		for (Map.Entry<DiffEntry, EditList> entry : diff.entrySet()) {
			File file = new File(gitClient.getDirectory().toString().replace(".git", "") + entry.getKey().getNewPath());
			allDiffs.addChangedFiles(new ChangedFile(entry.getKey(), entry.getValue(), file));
		}
		TangledCommitDetectionImpl tcd = new TangledCommitDetectionImpl();
		// tcd.getLineDistances(allDiffs);
		// tcd.getPackageDistances(allDiffs);
		// tcd.getPathDistance(allDiffs);
		TangledCommitDetection.getMethods(allDiffs);
		summary += createSummaryOfDiffEntry(allDiffs);
		return summary;
	}

	private String createSummaryOfDiffEntry(Diff diffs) {
		String allSummary ="";
		for(ChangedFile changedFile: diffs.getChangedFiles()){
			String className = FilenameUtils.removeExtension(changedFile.getFile().getName());
			String summary = "The class " + makeBold(className)+ " with following methods were changed: " + lineBreak();
			// @issue How can we parse methods from diffs?
			// @decision Use parser on existing files in file system.
			// @con Files might be deleted in the current version.
			// @con All methods are included, also the methods not in the diff.
			if (!changedFile.getFile().exists()) {
				return summary;
			}
			allSummary +=  summary + summarizeChangedMethods(changedFile);
		}


		return allSummary;
	}

	private String summarizeChangedMethods(ChangedFile changedFile) {
		String summary = "";
		for(MethodDeclaration methodDeclaration : changedFile.getMethodDeclarations()){
			String method = methodDeclaration.getNameAsString();
			if (!summary.contains(method)) {
				summary += method + lineBreak();
			}
		}
		return summary;
	}

	private String makeBold(String text, boolean useHtml) {
		if (useHtml) {
			return "<b>" + text + "</b>";
		}
		return "*" + text + "*";
	}

	private String makeBold(String text) {
		return makeBold(text, useHtml);
	}

	private String lineBreak(boolean useHtml) {
		if (useHtml) {
			return "<br/>";
		}
		return "\n";
	}

	private String lineBreak() {
		return lineBreak(useHtml);
	}
}