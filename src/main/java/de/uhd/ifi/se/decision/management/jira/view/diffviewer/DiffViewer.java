package de.uhd.ifi.se.decision.management.jira.view.diffviewer;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.jgit.lib.Ref;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uhd.ifi.se.decision.management.jira.git.GitClient;

/**
 * Creates diff viewer content for a list of git repository branches
 */
@XmlRootElement(name = "DiffViewer")
@XmlAccessorType(XmlAccessType.FIELD)
public class DiffViewer {

	@XmlElement(name = "branches")
	private List<BranchDiff> branchDiffs;

	private static final Logger LOGGER = LoggerFactory.getLogger(DiffViewer.class);

	public DiffViewer(String projectKey) {
		this(projectKey, GitClient.getInstance(projectKey).getBranches(projectKey));
	}

	public DiffViewer(String projectKey, String jiraIssueKey) {
		this(projectKey, GitClient.getInstance(projectKey).getBranches(jiraIssueKey));
		LOGGER.info("projectKey:" + projectKey + ",jiraIssueKey:" + jiraIssueKey);
	}

	public DiffViewer(String projectKey, List<Ref> branches) {
		branchDiffs = new ArrayList<>();

		GitClient extractor = GitClient.getInstance(projectKey);
		for (Ref branch : branches) {
			branchDiffs.add(new BranchDiff(branch.getName(), extractor.getRationaleElements(branch)));
		}
	}

	public List<BranchDiff> getBranches() {
		return branchDiffs;
	}
}
