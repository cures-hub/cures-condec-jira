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
	
	// @issue How to model whether a changed file is correctly linked to a requirement/work item/knowledge elements?
	// @decision Add the isCorrect boolean attribute to the changed file class.
	// @con Changed files might be correctly linked to one requirement but incorrectly linked to another requirement, so it should not be an attribute of the object.
	// @alternative Add class to represent a link between a changed file and a knowledge element.
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
		this.setCorrect(true);
	}
	
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

	public boolean isCorrect() {
		return isCorrect;
	}

	public void setCorrect(boolean isCorrect) {
		this.isCorrect = isCorrect;
	}
}
