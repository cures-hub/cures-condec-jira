package de.uhd.ifi.se.decision.management.jira.extraction;

import java.util.Optional;
import java.util.Vector;

import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import de.uhd.ifi.se.decision.management.jira.extraction.impl.ChangedFileImpl;
import de.uhd.ifi.se.decision.management.jira.extraction.impl.DiffImpl;

public interface TangledCommitDetection {

	void calculatePredication(DiffImpl diffImpl);

	Vector<String> parsePackage(Optional<PackageDeclaration> op);

	void calculateLineDistances(DiffImpl diffImpl);

	void calculatePackageDistances(DiffImpl diffImpl);

	void standardization(DiffImpl diffImpl);

	void calculatePathDistances(DiffImpl diffImpl);

	void calculateMethodDistances(DiffImpl diffImpl);

	Boolean isAllChangesInMethods(DiffImpl diffImpl);

	Boolean isAllChangesInOnePackage(DiffImpl diffImpl);

	static void getMethods(DiffImpl diffImpl) {
		for (ChangedFileImpl changedFileImpl : diffImpl.getChangedFileImpls()) {
			try {
				new MethodVisitor().visit(changedFileImpl.getCompilationUnit(), changedFileImpl);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	class MethodVisitor extends VoidVisitorAdapter {
		@Override
		public void visit(MethodDeclaration m, Object arg) {
			ChangedFileImpl changedFileImpl = (ChangedFileImpl) arg;
			changedFileImpl.setMethodDeclarations(m);
		}
	}
}
