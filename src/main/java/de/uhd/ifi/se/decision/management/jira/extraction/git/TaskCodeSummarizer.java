package de.uhd.ifi.se.decision.management.jira.extraction.git;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand.ResetType;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.lib.Repository;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class TaskCodeSummarizer {

	public final static String DEFAULT_DIR = System.getProperty("user.home") + File.separator + "repository"
			+ File.separator;

	private static File directory;

	private static Repository repository;

	private static String className;

	public static String summarizer(Map<DiffEntry, EditList> diff, String projectKey, boolean isDialog)
			throws IOException, CheckoutConflictException, GitAPIException {
		directory = new File(DEFAULT_DIR + projectKey);
		Git git = Git.open(directory);
		repository = git.getRepository();
		String methodsToString = "";
		if (diff != null) {
			for (Map.Entry<DiffEntry, EditList> entry : diff.entrySet()) {
				DiffEntry diffEntry = entry.getKey();

				if (diffEntry == null) {
					continue;
				}

				String newPath = diffEntry.getNewPath();
				if (!newPath.contains(".java")) {
					continue;
				}

				IPath filePath = new Path(repository.getDirectory().toPath().toString()).removeLastSegments(1)
						.append(newPath);
				File file = filePath.toFile();

				if (!file.isFile()) {
					continue;
				}

				Set<MethodDeclaration> methodDeclarations = new LinkedHashSet<MethodDeclaration>();

				FileInputStream fileInputStream;
				CompilationUnit compilationUnit = null;
				try {
					fileInputStream = new FileInputStream(filePath.toString());
					compilationUnit = JavaParser.parse(fileInputStream); // produces real readable code
					fileInputStream.close();
				} catch (ParseProblemException e) {
					System.out.println(e);
					continue;
				}
				className = "";
				new VoidVisitorAdapter<Object>() {
					@Override
					public void visit(ClassOrInterfaceDeclaration n, Object arg) {
						super.visit(n, arg);
						className = n.getNameAsString();
					}
				}.visit(compilationUnit, null);

				MethodVisitor methodVisitor = new MethodVisitor();
				compilationUnit.accept(methodVisitor, null);
				methodDeclarations = methodVisitor.getMethodDeclarations();

				if (!isDialog) {
					methodsToString += methodsInComment(methodDeclarations);

				} else {
					methodsToString += methodsInDialog(methodDeclarations);
				}
			}
		}
		git.reset().setMode(ResetType.HARD).call();

		return methodsToString;
	}

	private static String methodsInComment(Set<MethodDeclaration> methodDeclarations) {
		String methodsToString = "In class " + className + " the following methods has been changed: \n";
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

	private static String methodsInDialog(Set<MethodDeclaration> methodDeclarations) {
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