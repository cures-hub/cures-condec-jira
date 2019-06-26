package de.uhd.ifi.se.decision.management.jira.extraction;

import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

/**
 * Interface for a estimation whether a {@link Diff} of {@ChangedFile}s contains
 * wrong links, i.e., is tangled.
 */
public interface TangledCommitDetection {

	/**
	 * TODO Was macht diese Methode?
	 * @param diff
	 *            The diff might be a single git commit, a whole feature branch
	 *            (with many commits), or all commits belonging to a JIRA issue.
	 */
	void calculatePredication(Diff diff);

	/**
	 * Split a package declaration into a list of Strings.
	 *
	 * @param packageDeclaration
	 *            packageDeclaration. Its an optional attribute of CompilationUnit.
	 *
	 * @return package declaration as a list of Strings.
	 */
	List<String> parsePackage(Optional<PackageDeclaration> packageDeclaration);

	/**
	 * Calculate for each ChangedFile a distance, which is depends on the package
	 * distances between other ChangedFiles.
	 *
	 * @param diff
	 *            The diff might be a single git commit, a whole feature branch
	 *            (with many commits), or all commits belonging to a JIRA issue.
	 *
	 */
	void calculatePackageDistances(Diff diff);

	/**
	 * TODO Passt 100%? 
	 * Normalize the result of calculatePackageDistances, from floating-point number into
	 * percentage. This function takes the biggest distance as 100%
	 *
	 * @param diff
	 *            The diff might be a single git commit, a whole feature branch
	 *            (with many commits), or all commits belonging to a JIRA issue.
	 *
	 */
	void standardization(Diff diff);

	/**
	 * Set for each ChangedFile the name of methods, which is used to summarize code
	 * changes.
	 *
	 * @param diff
	 *            The diff might be a single git commit, a whole feature branch
	 *            (with many commits), or all commits belonging to a JIRA issue.
	 *
	 */
	static void getMethods(Diff diff) {
		for (ChangedFile changedFile : diff.getChangedFiles()) {
			try {
				new MethodVisitor().visit(changedFile.getCompilationUnit(), changedFile);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * TODO Brauchen wir MethodVisitor Klasse noch?
	 * Helper class for getMethods, which use the visit() to get the method-name.
	 * Visit() takes MethodDeclaration and an Object, which in this case is a
	 * ChangedFile.
	 *
	 */
	class MethodVisitor extends VoidVisitorAdapter {
		@Override
		public void visit(MethodDeclaration m, Object arg) {
			ChangedFile changedFile = (ChangedFile) arg;
			changedFile.addMethodDeclaration(m.getDeclarationAsString());
		}
	}
}
