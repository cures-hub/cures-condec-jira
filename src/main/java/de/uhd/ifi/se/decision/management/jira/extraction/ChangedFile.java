package de.uhd.ifi.se.decision.management.jira.extraction;

import java.io.File;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;

/**
 * Interface for a changed file as part of a diff.
 * 
 * @see Diff
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
	 * Returns a list of method declrations if the changed file is a Java class.
	 * 
	 * @return a list of Strings, which contains methodDeclarations.
	 */
	List<String> getMethodDeclarations();

	/**
	 * Returns the compilation unit if the changed file is a Java class. Each java
	 * file denotes a compilation unit. A compilation unit starts with an optional
	 * package declaration, followed by zero or more import declarations, followed
	 * by zero or more type declarations.
	 * 
	 * @param file
	 *            to be parsed to an abstract syntax tree (AST). Needs to be a Java file.
	 * @see CompilationUnit
	 * 
	 * TODO
	 * @return CompilationUnit, Java compilation unit AST node type. This is the
	 *         type of the root of an AST.
	 */
	CompilationUnit parseCompilationUnit(File file);

	/**
	 * Returns the compilation unit if the changed file is a Java class. 
	 * @return CompilationUnit, Java compilation unit AST node type. This is the
	 *         type of the root of an AST.
	 */
	CompilationUnit getCompilationUnit();

	/**
	 * Returns the file that is 
	 * @see File
	 * @return File, an abstract representation of a file and is also a directory
	 *         path-name.
	 */
	File getFile();

	/**
	 *
	 * @return a  which describes the total packageDistance to other
	 *         ChangedFiles.
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
}
