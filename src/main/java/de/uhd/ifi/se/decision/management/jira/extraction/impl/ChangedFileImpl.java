package de.uhd.ifi.se.decision.management.jira.extraction.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

import de.uhd.ifi.se.decision.management.jira.extraction.ChangedFile;

public class ChangedFileImpl implements ChangedFile {

	private List<String> methodDeclarations;
	private float probabilityOfCorrectness;
	// TODO warum public?
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
	}
	
	// TODO derived method
	@JsonProperty("className")
	public String getName() {
		return this.file.getName();
	}

	public float getProbabilityOfCorrectness() {
		return probabilityOfCorrectness;
	}

	public void setProbabilityOfCorrectness(float probabilityOfCorrectness) {
		this.probabilityOfCorrectness = probabilityOfCorrectness;
	}

	public int getPackageDistance() {
		return packageDistance;
	}

	public void setPackageDistance(int packageDistance) {
		this.packageDistance = packageDistance;
	}

	public List<String> getMethodDeclarations() {
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

	public void addMethodDeclaration(String methodDeclaration) {
		this.methodDeclarations.add(methodDeclaration);
	}
}
