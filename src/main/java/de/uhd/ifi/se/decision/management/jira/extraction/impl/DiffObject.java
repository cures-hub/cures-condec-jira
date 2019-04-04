package de.uhd.ifi.se.decision.management.jira.extraction.impl;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.EditList;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.Vector;

public class DiffObject {
    private DiffEntry diffEntry;
    private EditList editList;
    private File file;
    private Vector<Double> lineDistance;
    private Vector<Double> packageDistance;
    private Vector<Double> pathDistance;
    // private Optional<PackageDeclaration> packageDeclaration;
    private CompilationUnit compilationUnit;
    private Vector<MethodDeclaration> methodDeclarations;

    public DiffObject(DiffEntry diffEntry, EditList editList, File file) {
        this.diffEntry = diffEntry;
        this.editList = editList;
        this.file = file;
        this.packageDistance = new Vector<>();
        this.pathDistance = new Vector<>();
        this.methodDeclarations = new Vector<>();
        this.compilationUnit = parseCompilationUnit(file);
    }

    public CompilationUnit getCompilationUnit() {
        return compilationUnit;
    }

    public void setCompilationUnit(CompilationUnit compilationUnit) {
        this.compilationUnit = compilationUnit;
    }

    public CompilationUnit parseCompilationUnit(File file) {
        try {
            return JavaParser.parse(file);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public EditList getEditList() {
        return editList;
    }

    public DiffEntry getDiffEntry() {
        return diffEntry;
    }

    public File getFile() {
        return file;
    }

    public void setLineDistance(Vector<Double> lineDistance) {
        this.lineDistance = lineDistance;
    }

    public void addPackageDistance(double distance) {
        this.packageDistance.add(distance);
    }

    public void addPathDistance(double distance) {
        this.pathDistance.add(distance);
    }

    public void setMethodDeclarations(MethodDeclaration m){ this.methodDeclarations.add(m);}
}
