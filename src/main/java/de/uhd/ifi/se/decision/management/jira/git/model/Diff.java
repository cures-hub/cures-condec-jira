package de.uhd.ifi.se.decision.management.jira.git.model;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlElement;

import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;

import de.uhd.ifi.se.decision.management.jira.quality.completeness.QualityProblem;

/**
 * Models a list of {@link ChangedFile}s. The scope for the diff might be a
 * single git commit, a whole feature branch (with many commits), or all commits
 * belonging to a Jira issue.
 */
public class Diff {

	private List<ChangedFile> changedFiles;
	private List<RevCommit> commits;
	private Ref ref;
	private List<DecisionKnowledgeElementInCodeComment> codeElements;
	private List<DecisionKnowledgeElementInCommitMessage> commitElements;
	private String repoUri;

	public Diff() {
		changedFiles = new ArrayList<>();
		commits = new ArrayList<>();
	}

	public Diff(List<RevCommit> commits) {
		this();
		this.commits = commits;
	}

	public Diff(Ref ref, List<DecisionKnowledgeElementInCodeComment> codeCommentElements,
			List<DecisionKnowledgeElementInCommitMessage> commitMessageElements) {
		this.ref = ref;
		this.commitElements = commitMessageElements;
		this.codeElements = codeCommentElements;
	}

	/**
	 * @return files changed in the diff as a list of {@link ChangedFile} objects.
	 */
	public List<ChangedFile> getChangedFiles() {
		return changedFiles;
	}

	/**
	 * Adds a new {@link ChangedFile} to the diff.
	 * 
	 * @param changedFile
	 *            object of {@link ChangedFile} class.
	 */
	public void addChangedFile(ChangedFile changedFile) {
		changedFiles.add(changedFile);
	}

	public void add(Diff diff) {
		for (ChangedFile changedFile : diff.getChangedFiles()) {
			addChangedFile(changedFile);
		}
	}

	/**
	 * @return decision knowledge elements documented of the {@link ChangedFile}s
	 *         that are part of this diff.
	 * @see ChangedFile#getRationaleElementsFromCodeComments()
	 */
	public List<DecisionKnowledgeElementInCodeComment> getRationaleElementsFromCodeComments() {
		List<DecisionKnowledgeElementInCodeComment> elementsFromCode = new ArrayList<>();
		for (ChangedFile codeFile : getChangedFiles()) {
			elementsFromCode.addAll(codeFile.getRationaleElementsFromCodeComments());
		}
		return elementsFromCode;
	}

	public List<RevCommit> getCommits() {
		return commits;
	}

	public void setCommits(List<RevCommit> commits) {
		this.commits = commits;
	}

	@XmlElement
	public String getName() {
		return ref != null ? ref.getName() : null;
	}

	@XmlElement
	public String getId() {
		return ref != null ? ref.getObjectId().getName() : null;
	}

	public void setRef(Ref ref) {
		this.ref = ref;
	}

	@XmlElement
	public List<DecisionKnowledgeElementInCodeComment> getCodeElements() {
		return codeElements;
	}

	@XmlElement
	public List<DecisionKnowledgeElementInCommitMessage> getCommitElements() {
		return commitElements;
	}

	@XmlElement
	public String getRepoUri() {
		return URLEncoder.encode(repoUri, Charset.defaultCharset());
	}

	public void setRepoUri(String repoUri) {
		this.repoUri = repoUri;
	}

	@XmlElement
	public Set<QualityProblem> getQualityProblems() {
		return codeElements.stream().flatMap(element -> element.getQualityProblems().stream())
				.collect(Collectors.toSet());
	}

	@XmlElement(name = "hash")
	@Override
	public int hashCode() {
		return Objects.hash(getName(), getId());
	}
}
