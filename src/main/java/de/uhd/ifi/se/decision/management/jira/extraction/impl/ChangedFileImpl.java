package de.uhd.ifi.se.decision.management.jira.extraction.impl;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import de.uhd.ifi.se.decision.management.jira.extraction.ChangedFile;
import org.eclipse.jgit.diff.EditList;

import java.io.File;
import java.io.IOException;
import java.util.Vector;


public class ChangedFileImpl implements ChangedFile, Comparable<ChangedFileImpl> {
    private EditList editList;
    private File file;
    private int packageDistance;
    private float percentage;
    private Vector<Double> lineDistances;
    private Vector<Double> pathDistances;
    private CompilationUnit compilationUnit;
    private Vector<MethodDeclaration> methodDeclarations;
    private Vector<Double> methodDistances;

    public ChangedFileImpl(EditList editList, File file) {
        this.editList = editList;
        this.file = file;
        this.packageDistance = 0;
        this.pathDistances = new Vector<>();
        this.lineDistances = new Vector<>();
        this.methodDistances = new Vector<>();
        this.methodDeclarations = new Vector<>();
        this.compilationUnit = parseCompilationUnit(file);
    }

    public float getPercentage() {
        return percentage;
    }

    public void setPercentage(float percentage) {
        this.percentage = percentage;
    }

    public int getPackageDistance() {
        return packageDistance;
    }

    public void setPackageDistance(int packageDistance) {
        this.packageDistance = packageDistance;
    }

    public Vector<MethodDeclaration> getMethodDeclarations() {
        return methodDeclarations;
    }

    public CompilationUnit getCompilationUnit() {
        return compilationUnit;
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

    public File getFile() {
        return file;
    }

    public void addLineDistance(double distance) {
        this.lineDistances.add(distance);
    }

    public void addPathDistance(double distance) {
        this.pathDistances.add(distance);
    }

    public void setMethodDeclarations(MethodDeclaration m) {
        this.methodDeclarations.add(m);
    }

    public void addMethodDistance(double distance){
        this.methodDistances.add(distance);
    }

    @Override
    public int compareTo(ChangedFileImpl o) {
        return (o.getPackageDistance()-this.getPackageDistance());
    }
/*
    @Override
    public int compareTo(ChangedFileImpl o) {
        return (this.getPackageDistance()-o.getPackageDistance());
    }
    */
}
