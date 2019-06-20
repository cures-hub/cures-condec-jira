package de.uhd.ifi.se.decision.management.jira.extraction.impl;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import de.uhd.ifi.se.decision.management.jira.extraction.ChangedFile;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class ChangedFileImpl implements ChangedFile{

    public String className;
    private ArrayList<String> methodDeclarations;
    private float probabilityOfTangledness;
    public boolean isCorrect;
    @JsonIgnore
    private File file;
    @JsonIgnore
    private int packageDistance;
    @JsonIgnore
    private CompilationUnit compilationUnit;


    public ChangedFileImpl(File file) {
        this.file = file;
        this.packageDistance = 0;
        this.methodDeclarations = new ArrayList<String>();
        this.compilationUnit = parseCompilationUnit(file);
        this.isCorrect = true;
        this.className = this.file.getName();
    }

    public float getProbabilityOfTangledness() {
        return probabilityOfTangledness;
    }

    public void setProbabilityOfTangledness(float percentage) {
        this.probabilityOfTangledness = percentage;
    }

    public int getPackageDistance() {
        return packageDistance;
    }

    public void setPackageDistance(int packageDistance) {
        this.packageDistance = packageDistance;
    }

    public ArrayList<String> getMethodDeclarations() {
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

    public File getFile() {
        return file;
    }

    public void addMethodDeclaration(String m) {
        this.methodDeclarations.add(m);
    }
}
