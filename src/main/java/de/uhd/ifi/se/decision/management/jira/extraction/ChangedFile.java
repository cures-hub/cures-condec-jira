package de.uhd.ifi.se.decision.management.jira.extraction;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.eclipse.jgit.diff.EditList;

import java.io.File;
import java.util.Vector;

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

}
