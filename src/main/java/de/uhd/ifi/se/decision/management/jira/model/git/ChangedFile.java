package de.uhd.ifi.se.decision.management.jira.model.git;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import de.uhd.ifi.se.decision.management.jira.extraction.parser.JavaCodeCommentParser;
import de.uhd.ifi.se.decision.management.jira.extraction.parser.MethodVisitor;

/**
 * Models a changed file as part of a {@link Diff}.
 * 
 * @issue How can we get the creation time and the updating time of the file?
 * @alternative Pass RevCommit::getCommitTime() to this class to store the
 *              updating time of the file.
 * 
 * @issue How to access the file content of a git file?
 * @decision Retrieve the file content from the git blob object and store it as
 *           a class attribute.
 * @pro Branch/commit must not be checked out to access the file content.
 * @alternative Checkout branch/commit to access the file content. Make
 *              ChangedFile class extend the File class.
 * @con Checking out feature branches is not applicable for multiple users or
 *      analyzing multiple branches at once.
 */
public class ChangedFile {

	private static final Logger LOGGER = LoggerFactory.getLogger(ChangedFile.class);

	/**
	 * @issue Can we use the repository object instead of a simple String to codify
	 *        URI?
	 */
	@JsonIgnore
	private String repoUri;
	@JsonIgnore
	private DiffEntry diffEntry;
	@JsonIgnore
	private EditList editList;
	@JsonIgnore
	private String treeWalkPath;

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
	@JsonIgnore
	private String fileContent;

	public ChangedFile() {
		this.packageDistance = 0;
		this.setCorrect(true);
	}

	public ChangedFile(String fileContent) {
		this();
		this.fileContent = fileContent;
		this.methodDeclarations = parseMethods();
	}

	public ChangedFile(String fileContent, String uri) {
		this(fileContent);
		this.repoUri = uri;
	}

	public ChangedFile(DiffEntry diffEntry, EditList editList, ObjectId treeId, Repository repository) {
		this();
		this.fileContent = readFileContentFromDiffEntry(diffEntry, treeId, repository);
		this.diffEntry = diffEntry;
		this.editList = editList;
		this.methodDeclarations = parseMethods();
	}

	private String readFileContentFromDiffEntry(DiffEntry diffEntry, ObjectId treeId, Repository repository) {
		String fileContent = "";
		try {
			TreeWalk treeWalk = TreeWalk.forPath(repository, diffEntry.getNewPath(), treeId);
			fileContent = readFileContentFromGitObject(treeWalk, repository);
			setTreeWalkPath(treeWalk.getPathString());
			treeWalk.close();
		} catch (IOException | NullPointerException e) {
			LOGGER.error("Changed file could not be created. " + e.getMessage());
		}
		return fileContent;
	}

	public ChangedFile(Repository repository, TreeWalk treeWalk, String remoteUri) {
		this(readFileContentFromGitObject(treeWalk, repository), remoteUri);
		setTreeWalkPath(treeWalk.getPathString());
	}

	public static String readFileContentFromGitObject(TreeWalk treeWalk, Repository repository) {
		String fileContent = "";
		try {
			ObjectId blobId = treeWalk.getObjectId(0);
			ObjectReader objectReader = repository.newObjectReader();
			ObjectLoader objectLoader = objectReader.open(blobId);
			byte[] bytes = objectLoader.getBytes();
			fileContent = new String(bytes, StandardCharsets.UTF_8);
			objectReader.close();
		} catch (IOException | NullPointerException e) {
			LOGGER.error("Changed file could not be created. " + e.getMessage());
		}
		return fileContent;
	}

	/**
	 * @return the {@link DiffEntry} object.
	 */
	public DiffEntry getDiffEntry() {
		return diffEntry;
	}

	/**
	 * @return the {@link EditList} object.
	 */
	public EditList getEditList() {
		return editList;
	}

	/**
	 * @return name of the file as a String.
	 */
	@JsonProperty("className")
	public String getName() {
		String name = getNewFileNameFromDiffEntry();
		if (name.isEmpty()) {
			name = getNewFileNameFromTreeWalkPath();
		}
		return name;
	}

	private String getNewFileNameFromDiffEntry() {
		if (diffEntry == null) {
			return "";
		}
		return getFileNameFromPath(diffEntry.getNewPath());
	}

	private String getNewFileNameFromTreeWalkPath() {
		if (treeWalkPath == null) {
			return "";
		}
		return getFileNameFromPath(treeWalkPath);
	}

	private String getFileNameFromPath(String path) {
		String[] segments = path.split("/");
		return segments[segments.length - 1];
	}

	/**
	 * @return the probability that the link between a {@link ChangedFile} and a
	 *         requirement/work item is correct. If the link is wrong, the
	 *         {@link Diff} is tangled.
	 */
	public float getProbabilityOfCorrectness() {
		return probabilityOfCorrectness;
	}

	/**
	 * Sets the probability that the link between a {@link ChangedFile} and a
	 * requirement/work item is correct.
	 * 
	 * @param probabilityOfCorrectness
	 *            probability of correctness as a floating-point number.
	 */
	public void setProbabilityOfCorrectness(float probabilityOfCorrectness) {
		this.probabilityOfCorrectness = probabilityOfCorrectness;
	}

	/**
	 * @return a which describes the total packageDistance to other ChangedFiles.
	 */
	public int getPackageDistance() {
		return packageDistance;
	}

	/**
	 * @param packageDistance
	 *            Set the a Integer number for a ChangedFile.
	 */
	public void setPackageDistance(int packageDistance) {
		this.packageDistance = packageDistance;
	}

	/**
	 * @return set of method declarations if the changed file is a Java class.
	 */
	public Set<String> getMethodDeclarations() {
		return methodDeclarations;
	}

	private Set<String> parseMethods() {
		Set<String> methodsInClass = new LinkedHashSet<String>();

		if (!isExistingJavaClass()) {
			return methodsInClass;
		}

		MethodVisitor methodVisitor = getMethodVisitor();
		for (MethodDeclaration methodDeclaration : methodVisitor.getMethodDeclarations()) {
			methodsInClass.add(methodDeclaration.getNameAsString());
		}

		return methodsInClass;
	}

	private MethodVisitor getMethodVisitor() {
		if (compilationUnit == null) {
			ParseResult<CompilationUnit> parseResult = null;
			parseResult = JavaCodeCommentParser.parseJavaFile(fileContent);
			compilationUnit = parseResult.getResult().get();
		}
		MethodVisitor methodVistor = new MethodVisitor();
		compilationUnit.accept(methodVistor, null);
		return methodVistor;
	}

	/**
	 * @param methodDeclaration
	 *            Add a methodDeclaration as String into the attribute
	 *            methodDeclarations.
	 */
	public void addMethodDeclaration(String methodDeclaration) {
		this.methodDeclarations.add(methodDeclaration);
	}

	/**
	 * @return true if the file is a Java class and exists in currently checked out
	 *         version of the git repository. False means that the file could have
	 *         been deleted or that its name has been changed or it has been moved.
	 */
	public boolean isExistingJavaClass() {
		return exists() && isJavaClass();
	}

	public boolean exists() {
		if (diffEntry == null) {
			return true;
		}
		return diffEntry.getChangeType() != ChangeType.DELETE;
	}

	public int getNumberOfLines() {
		return fileContent.split("\n").length;
	}

	/**
	 * @return true if the file is a Java class.
	 */
	public boolean isJavaClass() {
		return getName().endsWith("java");
	}

	public boolean isCorrect() {
		return isCorrect;
	}

	public void setCorrect(boolean isCorrect) {
		this.isCorrect = isCorrect;
	}

	/**
	 * @return compilation unit if the changed file is a Java class.
	 */
	public CompilationUnit getCompilationUnit() {
		return compilationUnit;
	}

	/**
	 * @return package declaration as a list of Strings.
	 */
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

	public String getRepoUri() {
		return repoUri;
	}

	public void setRepoUri(String repoUri) {
		this.repoUri = repoUri;
	}

	/**
	 * @return name of the file before it was changed.
	 */
	public String getOldName() {
		Path oldPath = Paths.get(getDiffEntry().getOldPath());
		return oldPath.getFileName().toString();
	}

	/**
	 * @issue How can we get a path String that can be understood by git.blame()
	 *        method?
	 * @decision Save treeWalk path for now!
	 * 
	 * @return relative path starting from "src" folder. Is needed for git blame
	 *         call.
	 */
	public String getTreeWalkPath() {
		return treeWalkPath;
	}

	public void setTreeWalkPath(String treeWalkPath) {
		this.treeWalkPath = treeWalkPath;

		// absolute path:
		// new File(repository.getWorkTree(), treeWalk.getPathString());
	}

	public String getFileContent() {
		return fileContent;
	}
}
