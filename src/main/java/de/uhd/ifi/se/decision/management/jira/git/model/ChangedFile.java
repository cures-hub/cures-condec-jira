package de.uhd.ifi.se.decision.management.jira.git.model;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
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
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import de.uhd.ifi.se.decision.management.jira.git.config.GitConfiguration;
import de.uhd.ifi.se.decision.management.jira.git.parser.CodeCommentParser;
import de.uhd.ifi.se.decision.management.jira.git.parser.JiraIssueKeyFromCommitMessageParser;
import de.uhd.ifi.se.decision.management.jira.git.parser.MethodVisitor;
import de.uhd.ifi.se.decision.management.jira.git.parser.RationaleFromCodeCommentParser;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.CodeClassInDatabase;

/**
 * Models a changed file as part of a {@link DiffForSingleRef}.
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
public class ChangedFile extends KnowledgeElement {

	private static final Logger LOGGER = LoggerFactory.getLogger(ChangedFile.class);

	/**
	 * @issue How can we access the git repository that the code file belongs to?
	 * @decision We use a simple String to codify the URI of the git repository that
	 *           the code file belongs to!
	 * @pro A String is easy to store and retrieve from the GitConfiguration
	 *      classes.
	 * @alternative We could use the repository object to access the git repository.
	 * @pro More powerful: For example, the file content might be easier to retrieve
	 *      when having such an object.
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
	@JsonIgnore
	private List<RevCommit> commits;

	/**
	 * @issue Where shall we store the line count of a code file knowledge element?
	 * @decision In the ChangedFile class!
	 * @pro Only files have a line count, not other knowledge elements.
	 * @con The line count needs to be handled by the CodeClassPersistenceManager,
	 *      which uses KnowledgeElement instead of ChangedFile in many cases.
	 * @con The CodeCompletenessCheck class (using the lineCount) implements the
	 *      CompletenessCheck interface, which works with KnowledgeElements, not
	 *      ChangedFiles.
	 * @con Converting a KnowledgeElement into a ChangedFile performs very badly.
	 * @alternative In the KnowledgeElement class!
	 * @con Not all knowledge elements have a line count.
	 * @pro Many functions using the lineCount already work with KnowledgeElements,
	 *      not ChangedFiles.
	 */
	private int lineCount;

	public ChangedFile() {
		super();
		packageDistance = 0;
		setCorrect(true);
		documentationLocation = DocumentationLocation.CODE;
		type = KnowledgeType.CODE;
		commits = new ArrayList<>();
	}

	public ChangedFile(String fileContent) {
		this();
		this.fileContent = fileContent;
		this.setLineCount(countNumberOfNonEmptyLines(fileContent));
		this.methodDeclarations = parseMethods();
	}

	public ChangedFile(CodeClassInDatabase databaseEntry) {
		this();
		this.id = databaseEntry.getId();
		this.project = new DecisionKnowledgeProject(databaseEntry.getProjectKey());
		this.treeWalkPath = databaseEntry.getFileName();
		this.setLineCount(databaseEntry.getLineCount());
		this.setSummary(getName());
	}

	public ChangedFile(DiffEntry diffEntry, EditList editList, ObjectId treeId, Repository repository) {
		this();
		this.fileContent = readFileContentFromDiffEntry(diffEntry, treeId, repository);
		this.setLineCount(countNumberOfNonEmptyLines(fileContent));
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
		} catch (Exception e) {
			LOGGER.error("Changed file could not be created. " + e.getMessage());
		}
		return fileContent;
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
		} catch (Exception e) {
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
		if (name.isEmpty()) {
			name = super.getSummary();
		}
		return name;
	}

	public String getFileEnding() {
		return GitConfiguration.getFileEnding(getName());
	}

	@Override
	public String getSummary() {
		return getName();
	}

	/**
	 * @return key of a {@link ChangedFile} object, e.g. "CONDEC:code:1".
	 */
	@Override
	public String getKey() {
		return getProject().getProjectKey() + ":code:" + getId();
	}

	/**
	 * @param key
	 *            of a {@link ChangedFile} object, e.g. "CONDEC:code:1".
	 * @return id parsed from the key of a changed file.
	 */
	public static long parseIdFromKey(String key) {
		String[] split = key.split(":");
		String id = "0";
		if (split.length > 1) {
			id = split[2];
		}
		return Long.parseLong(id);
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
	 *         {@link DiffForSingleRef} is tangled.
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

		if (!isExistingJavaFile()) {
			return methodsInClass;
		}

		MethodVisitor methodVisitor = getMethodVisitor();
		for (MethodDeclaration methodDeclaration : methodVisitor.getMethodDeclarations()) {
			methodsInClass.add(methodDeclaration.getNameAsString());
		}

		return methodsInClass;
	}

	private ParseResult<CompilationUnit> parseJavaFile(String inspectedFileContent) {
		ParseResult<CompilationUnit> parseResult = null;
		try {
			JavaParser javaParser = new JavaParser();
			parseResult = javaParser.parse(inspectedFileContent);
		} catch (ParseProblemException | NullPointerException e) {
			LOGGER.error(e.getMessage());
		}
		return parseResult;
	}

	private MethodVisitor getMethodVisitor() {
		if (compilationUnit == null) {
			ParseResult<CompilationUnit> parseResult = null;
			parseResult = parseJavaFile(fileContent);
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
	public boolean isExistingJavaFile() {
		return exists() && getName().endsWith("java");
	}

	public boolean exists() {
		if (diffEntry == null) {
			return true;
		}
		return diffEntry.getChangeType() != ChangeType.DELETE;
	}

	/**
	 * @issue How to count the lines of code (LOC) of a code file?
	 * @alternative Split the file content by their line endings to calculate the
	 *              LOC: fileContent.split("\n").length;
	 * @pro Simple to implement.
	 * @con Does count empty lines without code.
	 * @decision Use a method of the MetricsReloaded IDE plug-in to calculate the
	 *           LOC!
	 * @pro Does not count empty lines without code.
	 * 
	 * @param fileContent
	 *            text as a String.
	 * @return number of lines of code (LOC) including comments and without empty
	 *         lines.
	 */
	public static int countNumberOfNonEmptyLines(String fileContent) {
		if (fileContent == null) {
			return 0;
		}
		int lines = 0;
		boolean onEmptyLine = true;
		final char[] chars = fileContent.toCharArray();
		for (char aChar : chars) {
			if (aChar == '\n' || aChar == '\r') {
				if (!onEmptyLine) {
					lines++;
					onEmptyLine = true;
				}
			} else if (aChar != ' ' && aChar != '\t') {
				onEmptyLine = false;
			}
		}
		if (!onEmptyLine) {
			lines++;
		}
		return lines;
	}

	/**
	 * @return true if the file should be included as a node/vertex in the
	 *         {@link KnowledgeGraph}.
	 */
	public boolean isCodeFileToExtract() {
		FileType fileType = getFileType();
		return fileType != null && ConfigPersistenceManager.getGitConfiguration(getProject().getProjectKey())
				.shouldFileTypeBeExtracted(fileType);
	}

	/**
	 * @return true if this file is a test class (e.g. for unit testing).
	 */
	public boolean isTestCodeFile() {
		return isTestCodeFile(getDescription());
	}

	/**
	 * @param path
	 *            file path.
	 * @return true if the file path indicates that the file is a test class (e.g.
	 *         for unit testing).
	 */
	public static boolean isTestCodeFile(String path) {
		return path.matches("(\\S+)?((Test)|(test\\.)|(test\\/))(\\S+)?");
	}

	/**
	 * @return {@link FileType} including file ending and {@link CommentStyleType}
	 *         necessary to identify decision knowledge in the comments of this code
	 *         file.
	 */
	public FileType getFileType() {
		if (getProject() == null) {
			return null;
		}
		String fileEnding = getFileEnding();
		return ConfigPersistenceManager.getGitConfiguration(getProject().getProjectKey())
				.getFileTypeForEnding(fileEnding);
	}

	/**
	 * @return {@link CommentStyleType} that defines how the comments in the
	 *         programming language look like. The exact {@link FileType} is
	 *         returned by this{@link #getFileType()}.
	 */
	public CommentStyleType getCommentStyleType() {
		FileType fileType = getFileType();
		return fileType != null ? fileType.getCommentStyleType() : CommentStyleType.UNKNOWN;
	}

	/**
	 * @return all {@link CodeComment}s of this code file.
	 */
	public List<CodeComment> getCodeComments() {
		if (fileContent != null) {
			CodeCommentParser commentParser = new CodeCommentParser();
			return commentParser.getComments(this);
		}
		return new ArrayList<>();
	}

	/**
	 * @return all decision knowledge elements within the comments of this code
	 *         file.
	 */
	public List<DecisionKnowledgeElementInCodeComment> getRationaleElementsFromCodeComments() {
		return new RationaleFromCodeCommentParser().getRationaleElementsFromCode(this);
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
		if (getDiffEntry() == null) {
			return getName();
		}
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

	@Override
	public String getDescription() {
		return getTreeWalkPath() != null ? getTreeWalkPath() : getSummary();
	}

	public String getFileContent() {
		return fileContent;
	}

	/**
	 * @issue To which Jira issues should a code file be linked to?
	 * @decision We link a code file to all Jira issues that it was committed to
	 *           (i.e. where the Jira issue key was mentioned in the messages of the
	 *           commits that changed the file)!
	 * @con Might contain wrong links introduced through tangled changes.
	 * @alternative We could add some wrong link detection methods and link a code
	 *              file only to those Jira issues that seem to be very related.
	 * @con Hard to implement.
	 * @con A wrong link removal technique might have false positives which leads to
	 *      the removal of correct links.
	 * 
	 * @return keys of the Jira issues that the file was linked to via commit.
	 */
	public Set<String> getJiraIssueKeys() {
		Set<String> jiraIssueKeys = new LinkedHashSet<>();
		for (RevCommit commit : commits) {
			jiraIssueKeys.addAll(JiraIssueKeyFromCommitMessageParser.getJiraIssueKeys(commit.getFullMessage()));
		}
		return jiraIssueKeys;
	}

	public List<RevCommit> getCommits() {
		return commits;
	}

	/**
	 * @param commits
	 *            all commits that the code file was changed in (as a list of
	 *            {@link RevCommit}s).
	 */
	public void setCommits(List<RevCommit> commits) {
		for (RevCommit commit : commits) {
			addCommit(commit);
		}
	}

	/**
	 * @issue How can we get the creation/update time and author of a code file?
	 * @decision We use the method RevCommit::getCommitTime() and
	 *           RevCommit::getAuthorIdent() of each commit to get the commit times
	 *           and authors of a code file!
	 * 
	 * @param revCommit
	 *            commits that the code file was changed in as a {@link RevCommit}).
	 * @return true if the commit was successfully added to the list of commits.
	 */
	public boolean addCommit(RevCommit revCommit) {
		String author = revCommit.getAuthorIdent().getName();
		Date date = new Date(revCommit.getCommitTime() * 1000L);
		updateDateAndAuthor.put(date, author);
		return commits.add(revCommit);
	}

	/**
	 * @return number of lines of code (LOC) calculated without empty lines and with
	 *         comments.
	 */
	public int getLineCount() {
		return lineCount;
	}

	/**
	 * @param lineCount
	 *            number of lines of code (LOC) calculated without empty lines and
	 *            with comments.
	 */
	public void setLineCount(int lineCount) {
		this.lineCount = lineCount;
	}

	@Override
	public boolean equals(Object object) {
		if (object == null) {
			return false;
		}
		if (object == this) {
			return true;
		}
		if (!(object instanceof ChangedFile)) {
			if (object instanceof KnowledgeElement) {
				return super.equals(object);
			}
			return false;
		}
		ChangedFile changedFile = (ChangedFile) object;
		return getName().equals(changedFile.getName());
	}
}
