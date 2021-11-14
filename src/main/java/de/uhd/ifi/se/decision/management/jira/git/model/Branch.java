package de.uhd.ifi.se.decision.management.jira.git.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import org.eclipse.jgit.lib.Ref;

/**
 * Represents a branch in git with commits, changed code files, and decision
 * knowledge in code comments and commit messages.
 */
public class Branch {

	private Ref ref;
	@XmlElement
	private List<DecisionKnowledgeElementInCodeComment> codeElements;
	@XmlElement
	private List<DecisionKnowledgeElementInCommitMessage> commitElements;

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

	public List<DecisionKnowledgeElementInCodeComment> getCodeElements() {
		return codeElements;
	}

	public List<DecisionKnowledgeElementInCommitMessage> getCommitElements() {
		return commitElements;
	}
}