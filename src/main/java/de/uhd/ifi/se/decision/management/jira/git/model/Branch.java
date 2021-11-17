package de.uhd.ifi.se.decision.management.jira.git.model;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlElement;

import org.eclipse.jgit.lib.Ref;

import de.uhd.ifi.se.decision.management.jira.quality.completeness.QualityProblem;

/**
 * Represents a branch in git with commits, changed code files, and decision
 * knowledge in code comments and commit messages.
 */
public class Branch {

	private Ref ref;
	private List<DecisionKnowledgeElementInCodeComment> codeElements;
	private List<DecisionKnowledgeElementInCommitMessage> commitElements;
	private String repoUri;

	public Branch(Ref ref, List<DecisionKnowledgeElementInCodeComment> codeCommentElements,
			List<DecisionKnowledgeElementInCommitMessage> commitMessageElements) {
		this.ref = ref;
		this.commitElements = commitMessageElements;
		this.codeElements = codeCommentElements;
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
		return repoUri;
	}

	public void setRepoUri(String repoUri) {
		this.repoUri = repoUri;
	}

	@XmlElement
	public Set<QualityProblem> getQualityProblems() {
		return codeElements.stream().flatMap(element -> element.getQualityProblems().stream())
				.collect(Collectors.toSet());
	}
}