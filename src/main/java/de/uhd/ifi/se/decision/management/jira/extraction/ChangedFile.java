package de.uhd.ifi.se.decision.management.jira.extraction;

import java.io.File;
import java.util.ArrayList;

import com.github.javaparser.ast.CompilationUnit;

/**
 * Interface for a changed file as part of a diff.
 * @see Diff
 */
public interface ChangedFile {

	/**
	 * @return the probability of tangledness from a ChangedFile as Float.
	 */
	float getProbabilityOfTangledness();

	/**
	 *
	 * @param probabilityOfTangledness
	 *  set the a Float number as probability of tangledness for a ChangedFile.
	 *
	 */
	void setProbabilityOfTangledness(float probabilityOfTangledness);

	/**
	 *
	 @return An ArrayList of Strings, which contains methodDeclarations.
	 */
	ArrayList<String> getMethodDeclarations();

	/**
	 *
	 * @param file
	 *	Parse a Java file into AST
	 *  @return CompilationUnit, Java compilation unit AST node type.
	 *  This is the type of the root of an AST.
	 */
	CompilationUnit parseCompilationUnit(File file);

	/**
	 *
	 @return CompilationUnit, Java compilation unit AST node type.
	 This is the type of the root of an AST.
	 */
	CompilationUnit getCompilationUnit();

	/**
	 *
	 @return File, an abstract representation of a file and is also a directory path-name.
	 */
	File getFile();

	/**
	 *
	 *@return Integer, which describes the total packageDistance to other ChangedFiles.
	 *
	 */
	int getPackageDistance();

	/**
	 *
	 * @param packageDistance
	 *   Set the a Integer number for a ChangedFile.
	 *
	 */
	void setPackageDistance(int packageDistance);

	/**
	 *
	 * @param methodDeclaration
	 *  Add a methodDeclaration as String into the attribute methodDeclarations.
	 *
	 */
	void addMethodDeclaration(String methodDeclaration);

}
