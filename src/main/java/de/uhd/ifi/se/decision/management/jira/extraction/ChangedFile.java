package de.uhd.ifi.se.decision.management.jira.extraction;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.EditList;

import com.github.javaparser.ast.CompilationUnit;

/**
 * Interface for a changed file as part of a {@link Diff}.
 */
public interface ChangedFile {

	/**
	 * Returns the probability that the link between a {@link ChangedFile} and a
	 * requirement/work item is correct. If the link is wrong, the {@link Diff} is
	 * tangled.
	 * 
	 * @return the probability of correctness as a floating-point number.
	 */
	float getProbabilityOfCorrectness();

	/**
	 * Sets the probability that the link between a {@link ChangedFile} and a
	 * requirement/work item is correct.
	 * 
	 * @param probabilityOfCorrectness
	 *            probability of correctness as a floating-point number.
	 */
	void setProbabilityOfCorrectness(float probabilityOfCorrectness);

	/**
	 * Returns a set of method declarations if the changed file is a Java class.
	 * 
	 * @return a list of Strings, which contains methodDeclarations.
	 */
	Set<String> getMethodDeclarations();

	/**
	 * Returns the compilation unit if the changed file is a Java class.
	 * 
	 * @return CompilationUnit, Java compilation unit AST node type. This is the
	 *         type of the root of an AST.
	 */
	CompilationUnit getCompilationUnit();

	/**
	 * Returns the file that is
	 * 
	 * @see File
	 * @return File, an abstract representation of a file and is also a directory
	 *         path-name.
	 */
	File getFile();

	/**
	 *
	 * @return a which describes the total packageDistance to other ChangedFiles.
	 *
	 */
	int getPackageDistance();

	/**
	 *
	 * @param packageDistance
	 *            Set the a Integer number for a ChangedFile.
	 *
	 */
	void setPackageDistance(int packageDistance);

	/**
	 *
	 * @param methodDeclaration
	 *            Add a methodDeclaration as String into the attribute
	 *            methodDeclarations.
	 *
	 */
	void addMethodDeclaration(String methodDeclaration);

	/**
	 * Returns the name of the file as a String.
	 * 
	 * @return name of the file as a String.
	 */
	String getName();

	/**
	 * Returns true if the file exists in currently checked out version of the git
	 * repository. False means that the file could have been deleted or that its
	 * name has been changed or it has been moved.
	 * 
	 * @return true if the file exists in currently checked out version of the git
	 *         repository.
	 */
	boolean exists();

	/**
	 * Returns true if the file is a Java class.
	 * 
	 * @return true if the file is a Java class.
	 */
	boolean isJavaClass();

	/**
	 * Returns true if the file is a Java class and exists in currently checked out
	 * version of the git repository. False means that the file could have been
	 * deleted or that its name has been changed or it has been moved.
	 * 
	 * @return true if the file is a Java class and exists in currently checked out
	 *         version of the git repository.
	 */
	boolean isExistingJavaClass();

	/**
	 * Returns a package declaration split into a list of Strings.
	 *
	 * @return package declaration as a list of Strings.
	 */
	List<String> getPackageName();

	DiffEntry getDiffEntry();

	void setDiffEntry(DiffEntry diffEntry);

	EditList getEditList();

	void setEditList(EditList editList);
}
