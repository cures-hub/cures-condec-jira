package de.uhd.ifi.se.decision.management.jira.extraction;

import java.util.Optional;
import java.util.Vector;

import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public interface TangledCommitDetection {

	void calculatePredication(Diff diff);

	// TODO: change return value to list
	Vector<String> parsePackage(Optional<PackageDeclaration> op);

	// TODO: Remove method
	void calculateLineDistances(Diff diff);

	void calculatePackageDistances(Diff diff);

	void standardization(Diff diff);

	void calculatePathDistances(Diff diff);

	// TODO: Remove method
	void calculateMethodDistances(Diff diff);

	// TODO change to boolean
	Boolean isAllChangesInMethods(Diff diff);

	Boolean isAllChangesInOnePackage(Diff diff);

	static void getMethods(Diff diff) {
		for (ChangedFile changedFile : diff.getChangedFiles()) {
			try {
				new MethodVisitor().visit(changedFile.getCompilationUnit(), changedFile);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	class MethodVisitor extends VoidVisitorAdapter {
		@Override
		public void visit(MethodDeclaration m, Object arg) {
			ChangedFile changedFile = (ChangedFile) arg;
			changedFile.setMethodDeclarations(m);
		}
	}
}
