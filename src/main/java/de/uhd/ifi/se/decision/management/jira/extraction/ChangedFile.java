package de.uhd.ifi.se.decision.management.jira.extraction;

import java.io.File;
import java.util.Vector;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.jgit.diff.EditList;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;

import de.uhd.ifi.se.decision.management.jira.extraction.impl.SimplifiedChangedFile;

public interface ChangedFile {
	float getPercentage();

	void setPercentage(float percentage);

	Vector<MethodDeclaration> getMethodDeclarations();

	CompilationUnit parseCompilationUnit(File file);

	CompilationUnit getCompilationUnit();

	EditList getEditList();

	File getFile();

	int getPackageDistance();

	void setPackageDistance(int packageDistance);

	void addLineDistance(double distance);

	void addPathDistance(double distance);

	void addMethodDistance(double distance);

	void setMethodDeclarations(MethodDeclaration m);

	static SimplifiedChangedFile getSimplified(ChangedFile changedFile) {
		Vector<String> changedMethods = new Vector<>();
		for (MethodDeclaration methodDeclaration : changedFile.getMethodDeclarations()) {
			changedMethods.add(methodDeclaration.getDeclarationAsString());
		}
		return new SimplifiedChangedFile(FilenameUtils.removeExtension(changedFile.getFile().getName()), changedMethods,
				changedFile.getPercentage());
	}

}
