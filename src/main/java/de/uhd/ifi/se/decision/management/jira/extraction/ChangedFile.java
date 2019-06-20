package de.uhd.ifi.se.decision.management.jira.extraction;

import java.io.File;
import java.util.ArrayList;

import com.github.javaparser.ast.CompilationUnit;

/**
 * Interface for a changed file as part of a diff.
 * @see Diff
 */
public interface ChangedFile {
	float getProbabilityOfTangledness();

	void setProbabilityOfTangledness(float percentage);

	ArrayList<String> getMethodDeclarations();

	CompilationUnit parseCompilationUnit(File file);

	CompilationUnit getCompilationUnit();

	File getFile();

	int getPackageDistance();

	void setPackageDistance(int packageDistance);

	void setMethodDeclarations(String m);

}
