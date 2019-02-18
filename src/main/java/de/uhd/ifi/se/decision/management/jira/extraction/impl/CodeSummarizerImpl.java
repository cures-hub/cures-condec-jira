package de.uhd.ifi.se.decision.management.jira.extraction.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.revwalk.RevCommit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ast.CompilationUnit;
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
		Map<DiffEntry, EditList> diff = gitClient.getDiff(jiraIssueKey);
		if (diff == null) {
			return "";
		}
		return createSummary(diff);
	}
	
	@Override
	public String createSummary(RevCommit commit) {
		Map<DiffEntry, EditList> diff = gitClient.getDiff(commit);
		if (diff == null) {
			return "";
		}
		return createSummary(diff);
	}

	@Override
	public String createSummary(Map<DiffEntry, EditList> diff) {
		String summary = "The following classes were changed: ";
		if (diff == null) {
			return "";
		}
		for (Map.Entry<DiffEntry, EditList> entry : diff.entrySet()) {
			summary += createSummaryOfDiffEntry(entry.getKey(), entry.getValue());
		}
		return summary;
	}

	private String createSummaryOfDiffEntry(DiffEntry diffEntry, EditList editList) {
		if (diffEntry == null) {
			return "";
		}

		String newPath = diffEntry.getNewPath();
		if (!newPath.contains(".java")) {
			return "";
		}

		File file = new File(gitClient.getDirectory() + File.separator + newPath);

		String className = FilenameUtils.removeExtension(file.getName());
		String summary = makeBold(className) + lineBreak();

		// @issue How can we parse methods from diffs?
		// @decision Use parser on existing files in file system.
		// @con Files might be deleted in the current version.
		// @con All methods are included, also the methods not in the diff.
		if (!file.exists()) {
			return "";
		}

		CompilationUnit compilationUnit = null;
		try {
			FileInputStream fileInputStream = new FileInputStream(file.toString());
			compilationUnit = JavaParser.parse(fileInputStream); // produces real readable code
			fileInputStream.close();
		} catch (ParseProblemException | IOException e) {
			LOGGER.debug("Parsing error in class " + className);
		}

		MethodVisitor methodVisitor = new MethodVisitor();
		compilationUnit.accept(methodVisitor, null);
		Set<MethodDeclaration> methodDeclarations = new LinkedHashSet<MethodDeclaration>();
		methodDeclarations = methodVisitor.getMethodDeclarations();

		summary += summarizeChangedMethods(methodDeclarations, editList);

		return summary;
	}

	private String summarizeChangedMethods(Set<MethodDeclaration> methodDeclarations, EditList editList) {
		String summary = "The following methods were changed: " + lineBreak();

		for (Edit edit : editList) {
			for (MethodDeclaration methodDeclaration : methodDeclarations) {
				if (edit.getEndB() >= methodDeclaration.getBegin().get().line
						&& edit.getBeginB() <= methodDeclaration.getEnd().get().line) {
					// Insert happended
					String method = methodDeclaration.getNameAsString();
					if (!summary.contains(method)) {
						summary += method + lineBreak();
					}
				}
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