package de.uhd.ifi.se.decision.management.jira.git.model;

import java.net.URLEncoder;
import java.nio.charset.Charset;

import javax.xml.bind.annotation.XmlElement;

import org.eclipse.jgit.revwalk.RevCommit;

import de.uhd.ifi.se.decision.management.jira.git.GitClient;
import de.uhd.ifi.se.decision.management.jira.git.parser.RationaleFromCommitMessageParser;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.Origin;

/**
 * Represents a decision knowledge element documented in a commit message.
 * Commit messages are transcribed into Jira issue comments so that they can be
 * annotated and improved there. However, this class models a decision knowledge
 * element in its original form captured in a commit message.
 * 
 * @see Origin#COMMIT
 * @see RationaleFromCommitMessageParser
 * @see GitClient#getRationaleElementsFromCommitMessages(org.eclipse.jgit.lib.Ref)
 */
public class DecisionKnowledgeElementInCommitMessage extends KnowledgeElement {

	private RevCommit commit;
	private String repoUri;

	public DecisionKnowledgeElementInCommitMessage() {
		this.origin = Origin.COMMIT;
	}

	@XmlElement
	public String getImage() {
		return URLEncoder.encode(getType().getIconUrl(), Charset.defaultCharset());
	}

	public void setCommit(RevCommit commit) {
		this.commit = commit;
		setKey(commit.getId() + getKey() + "commit");
	}

	@XmlElement(name = "source")
	public String getCommitName() {
		return commit.getName();
	}

	@XmlElement
	public String getUrl() {
		String urlAsString = repoUri.replace(".git", "") + "/commit/" + getCommitName();
		return URLEncoder.encode(urlAsString, Charset.defaultCharset());
	}

	public void setRepoUri(String repoUri) {
		this.repoUri = repoUri;
	}
}