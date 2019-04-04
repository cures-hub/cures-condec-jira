package de.uhd.ifi.se.decision.management.jira.extraction;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import de.uhd.ifi.se.decision.management.jira.extraction.impl.DiffObject;
import org.eclipse.jgit.diff.EditList;

import java.io.File;
import java.io.IOException;
import java.util.*;

public interface TangledCommitDetection {

    static File getFile(String DEFAULT_DIR, String projectKey, String path) {
        return new File(DEFAULT_DIR + projectKey + "/" + path);
    }

    Vector<String> parsePackage(Optional<PackageDeclaration> op);

    Vector<DiffObject> getLineDistances(Vector<DiffObject> diffObjects);

    Vector<DiffObject> getPackageDistances(Vector<DiffObject> diffObjects);

    Vector<DiffObject> getPathDistance(Vector<DiffObject> diffObjects);

    Vector<DiffObject> parseMethod(Vector<DiffObject> diffObjects);

    Boolean isInMethod(EditList editList);

    // Vector<DiffObject>  setAllPackageName(Vector<DiffObject> diffObjects);

    // Vector<Float> getPackageDistanceList(Vector<Vector<String>> allDeclarations);

    // Optional<PackageDeclaration> getPackageDeclaration(File src);

    // float getPrognosis();


static void getMethods(File src, DiffObject diff) {
        try {
            CompilationUnit cu = JavaParser.parse(src);
            System.out.print("call get Method");
            new MethodVisitor().visit(cu, diff);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void getMethodV(Vector<DiffObject> diffObjects) {
     for (DiffObject dif: diffObjects){
         try {
             new MethodVisitor().visit(dif.getCompilationUnit(), dif);
         } catch (Exception e) {
             e.printStackTrace();
         }
     }

    }

    class MethodVisitor extends VoidVisitorAdapter {
        @Override
        public void visit(MethodDeclaration m, Object arg) {
            DiffObject diffi = (DiffObject) arg;
            System.out.print("call get visit");
            System.out.println("From [" + m.getBegin() + "," + "] to [" + m.getEnd() + ","
                    + "] is method:" + m.getDeclarationAsString() + '\n');
            System.out.println(m);
            diffi.setMethodDeclarations(m);
        }
    }



}
