package de.uhd.ifi.se.decision.management.jira.extraction;

import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import de.uhd.ifi.se.decision.management.jira.extraction.impl.ChangedFile;
import de.uhd.ifi.se.decision.management.jira.extraction.impl.Diff;
import org.eclipse.jgit.diff.EditList;

import java.util.*;

public interface TangledCommitDetection {

    Vector<String> parsePackage(Optional<PackageDeclaration> op);

    Diff  getLineDistances(Diff diff);

    Diff getPackageDistances(Diff diff);

    Diff getPathDistance(Diff diff);

    Boolean isInMethod(EditList editList);


    static void getMethods(Diff diffs) {
     for (ChangedFile diff: diffs.getChangedFiles()){
         try {
             new MethodVisitor().visit(diff.getCompilationUnit(), diff);
         } catch (Exception e) {
             e.printStackTrace();
         }
     }

    }

    class MethodVisitor extends VoidVisitorAdapter {
        @Override
        public void visit(MethodDeclaration m, Object arg) {
            ChangedFile changedFile = (ChangedFile) arg;
            System.out.print("call get visit");
            System.out.println("From [" + m.getBegin() + "," + "] to [" + m.getEnd() + ","
                    + "] is method:" + m.getDeclarationAsString() + '\n');
            System.out.println(m);
            changedFile.setMethodDeclarations(m);
        }
    }



}
