package de.uhd.ifi.se.decision.management.jira.extraction;

import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import de.uhd.ifi.se.decision.management.jira.extraction.impl.ChangedFile;
import de.uhd.ifi.se.decision.management.jira.extraction.impl.Diff;

import java.util.*;

public interface TangledCommitDetection {

    void calculatePredication(Diff diff);

    Vector<String> parsePackage(Optional<PackageDeclaration> op);

    void calculateLineDistances(Diff diff);

    void calculatePackageDistancesNew(Diff diff);

    void standardization(Diff diff);

    void calculatePathDistances(Diff diff);

    void calculateMethodDistances(Diff diff);

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
