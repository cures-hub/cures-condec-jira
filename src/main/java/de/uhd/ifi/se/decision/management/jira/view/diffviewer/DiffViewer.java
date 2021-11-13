package de.uhd.ifi.se.decision.management.jira.view.diffviewer;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import org.eclipse.jgit.lib.Ref;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uhd.ifi.se.decision.management.jira.git.GitClient;
import de.uhd.ifi.se.decision.management.jira.git.model.Branch;

/**
 * Creates diff viewer content for a list of git repository branches
 */
public class DiffViewer {

	@XmlElement
	private List<Branch> branches;

	private static final Logger LOGGER = LoggerFactory.getLogger(DiffViewer.class);

	public DiffViewer(String projectKey) {
		this(projectKey, GitClient.getInstance(projectKey).getBranches(projectKey));
	}

	public DiffViewer(String projectKey, String jiraIssueKey) {
		this(projectKey, GitClient.getInstance(projectKey).getBranches(jiraIssueKey));
		LOGGER.info("projectKey:" + projectKey + ",jiraIssueKey:" + jiraIssueKey);
	}

	public DiffViewer(String projectKey, List<Ref> refs) {
		branches = new ArrayList<>();

		GitClient extractor = GitClient.getInstance(projectKey);
		for (Ref ref : refs) {
			branches.add(new Branch(ref, extractor.getRationaleElementsFromCodeComments(ref),
					extractor.getRationaleElementsFromCommitMessages(ref)));
		}
	}

	public List<Branch> getBranches() {
		return branches;
	}
}
