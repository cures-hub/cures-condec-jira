package de.uhd.ifi.se.decision.management.jira.model.git.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.EditList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import de.uhd.ifi.se.decision.management.jira.extraction.impl.JavaCodeCommentParser;
import de.uhd.ifi.se.decision.management.jira.extraction.impl.MethodVisitor;
import de.uhd.ifi.se.decision.management.jira.model.git.ChangedFile;

public class ChangedFileImpl implements ChangedFile {

	private static final Logger LOGGER = LoggerFactory.getLogger(ChangedFile.class);

	@JsonIgnore
	private DiffEntry diffEntry;
	@JsonIgnore
	private EditList editList;
	@JsonIgnore
	private File file;

	private Set<String> methodDeclarations;
	private float probabilityOfCorrectness;

	/**
	 * @issue How to model whether a changed file is correctly linked to a
	 *        requirement/work item/knowledge element?
	 * @decision Add the isCorrect boolean attribute to the changed file class!
	 * @con Changed files might be correctly linked to one requirement but
	 *      incorrectly linked to another requirement, so it should not be an
	 *      attribute of the object.
	 * @alternative Add class to represent a link between a changed file and a
	 *              knowledge element!
	 */
	private boolean isCorrect;

	@JsonIgnore
	private int packageDistance;
	@JsonIgnore
	private CompilationUnit compilationUnit;

	public ChangedFileImpl() {
		this.packageDistance = 0;
		this.setCorrect(true);
	}

	public ChangedFileImpl(File file) {
		this();
		this.file = file;
		this.methodDeclarations = parseMethods();
	}

	public ChangedFileImpl(DiffEntry diffEntry, EditList editList, String baseDirectory) {
		this(new File(baseDirectory + diffEntry.getNewPath()));
		this.diffEntry = diffEntry;
		this.editList = editList;
	}

	@Override
	public DiffEntry getDiffEntry() {
		return diffEntry;
	}

	@Override
	public EditList getEditList() {
		return editList;
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
	public Set<String> getMethodDeclarations() {
		return methodDeclarations;
	}

	private Set<String> parseMethods() {
		Set<String> methodsInClass = new LinkedHashSet<String>();

		if (!isExistingJavaClass()) {
			return methodsInClass;
		}

		MethodVisitor methodVistor = getMethodVisitor();
		for (MethodDeclaration methodDeclaration : methodVistor.getMethodDeclarations()) {
			methodsInClass.add(methodDeclaration.getNameAsString());
		}

		return methodsInClass;
	}

	private MethodVisitor getMethodVisitor() {
		ParseResult<CompilationUnit> parseResult = JavaCodeCommentParser.parseJavaFile(file);
		this.compilationUnit = parseResult.getResult().get();
		MethodVisitor methodVistor = new MethodVisitor();
		compilationUnit.accept(methodVistor, null);
		return methodVistor;
	}

	@Override
	public File getFile() {
		return file;
	}

	@Override
	public void addMethodDeclaration(String methodDeclaration) {
		this.methodDeclarations.add(methodDeclaration);
	}

	@Override
	public boolean isExistingJavaClass() {
		return exists() && isJavaClass();
	}

	@Override
	public boolean exists() {
		return file.exists();
	}

	@Override
	public boolean isJavaClass() {
		return file.getName().endsWith("java");
	}

	public boolean isCorrect() {
		return isCorrect;
	}

	public void setCorrect(boolean isCorrect) {
		this.isCorrect = isCorrect;
	}

	@Override
	public CompilationUnit getCompilationUnit() {
		return this.compilationUnit;
	}

	@Override
	public List<String> getPartsOfPackageDeclaration() {
		List<String> partsOfPackageName = new ArrayList<String>();
		String packageDeclaration = getPackageDeclaration();

		for (String partOfPackageName : packageDeclaration.split("\\.")) {
			partsOfPackageName.add(partOfPackageName);
		}

		return partsOfPackageName;
	}

	private String getPackageDeclaration() {
		String packageDeclaration = "";
		if (getCompilationUnit() == null) {
			return "";
		}
		Optional<PackageDeclaration> optional = getCompilationUnit().getPackageDeclaration();

		try {
			packageDeclaration = optional.get().toString();
			packageDeclaration = packageDeclaration.replaceAll("\n", "").replaceAll(";", "").replaceAll("\r", "");
		} catch (NoSuchElementException e) {
			LOGGER.error(e.getMessage());
		}
		return packageDeclaration;
	}
}
