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
	// TODO warum public? wofuer ist das?
	private boolean isCorrect;
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
	@Override
	@JsonProperty("className")
	public String getName() {
		return this.file.getName();
	}

	@Override
	public float getProbabilityOfCorrectness() {
		return probabilityOfCorrectness;
	}

	@Override
	public void setProbabilityOfCorrectness(float probabilityOfCorrectness) {
		this.probabilityOfCorrectness = probabilityOfCorrectness;
	}

	@Override
	public int getPackageDistance() {
		return packageDistance;
	}

	@Override
	public void setPackageDistance(int packageDistance) {
		this.packageDistance = packageDistance;
	}

	@Override
	public List<String> getMethodDeclarations() {
		return methodDeclarations;
	}

	@Override
	public CompilationUnit getCompilationUnit() {
		return compilationUnit;
	}

	@Override
	public CompilationUnit parseCompilationUnit(File file) {
		try {
			return JavaParser.parse(file);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public File getFile() {
		return file;
	}

	@Override
	public void addMethodDeclaration(String methodDeclaration) {
		this.methodDeclarations.add(methodDeclaration);
	}
}
