package de.uhd.ifi.se.decision.management.jira.extraction.git;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.EditList;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;

public class TaskCodeSummarizer {

	private GitClient gitClient;
	private boolean useHtml;

	public TaskCodeSummarizer(GitClient gitClient, boolean useHtml) {
		this.gitClient = gitClient;
		this.useHtml = useHtml;
	}

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
		String summary = "";
		if (diffEntry == null) {
			return "";
		}

		String newPath = diffEntry.getNewPath();
		if (!newPath.contains(".java")) {
			return "";
		}

		File file = new File(gitClient.getDirectory() + File.separator + newPath);
		System.out.println(file.getPath());

		String className = FilenameUtils.removeExtension(file.getName());
		summary += makeBold(className, useHtml) + lineBreak(useHtml);

		Set<MethodDeclaration> methodDeclarations = new LinkedHashSet<MethodDeclaration>();

		FileInputStream fileInputStream;
		CompilationUnit compilationUnit = null;
		try {
			fileInputStream = new FileInputStream(file.toString());
			compilationUnit = JavaParser.parse(fileInputStream); // produces real readable code
			fileInputStream.close();
		} catch (ParseProblemException | IOException e) {
			System.err.println("Parsing error");
		}

		MethodVisitor methodVisitor = new MethodVisitor();
		compilationUnit.accept(methodVisitor, null);
		methodDeclarations = methodVisitor.getMethodDeclarations();

		if (!useHtml) {
			summary += methodsInComment(methodDeclarations, className);
		} else {
			summary += methodsInDialog(methodDeclarations, className);
		}

		return summary;
	}

	private String makeBold(String text, boolean useHtml) {
		if (useHtml) {
			return "<b>" + text + "</b>";
		}
		return "*" + text + "*";
	}

	private String lineBreak(boolean useHtml) {
		if (useHtml) {
			return "<br/>";
		}
		return "\n";
	}

	private String methodsInComment(Set<MethodDeclaration> methodDeclarations, String className) {
		String methodsToString = "In class *" + className + "* the following methods has been changed: \n";
		String methodsInClass = "";
		String method = "";

		for (MethodDeclaration methodDeclaration : methodDeclarations) {
			method = methodDeclaration.getNameAsString();
			if (!methodsInClass.contains(method)) {
				methodsInClass += method + "\n";
			}
		}
		return methodsToString += methodsInClass;
	}

	private String methodsInDialog(Set<MethodDeclaration> methodDeclarations, String className) {
		String methodsToString = "In class <b>" + className + "</b> the following methods has been changed: <br>";
		String methodsInClass = "";
		String method = "";

		for (MethodDeclaration methodDeclaration : methodDeclarations) {
			method = methodDeclaration.getNameAsString();
			if (!methodsInClass.contains(method)) {
				methodsInClass += method + "<br>";
			}
		}
		return methodsToString += methodsInClass;
	}
}