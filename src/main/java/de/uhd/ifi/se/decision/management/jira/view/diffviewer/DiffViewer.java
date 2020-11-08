package de.uhd.ifi.se.decision.management.jira.view.diffviewer;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.jgit.lib.Ref;

import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;
import de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.GitDecXtract;

/**
 * Creates diff viewer content for a list of git repository branches
 */
@XmlRootElement(name = "DiffViewer")
@XmlAccessorType(XmlAccessType.FIELD)
public class DiffViewer {

	@XmlElement(name = "branches")
	private List<BranchDiff> branchDiffs;

	public DiffViewer(String projectKey) {
		this(projectKey, GitClient.getOrCreate(projectKey).getBranches(projectKey));
	}

	public DiffViewer(String projectKey, String jiraIssueKey) {
		this(projectKey, GitClient.getOrCreate(projectKey).getBranches(jiraIssueKey));
	}

	public DiffViewer(String projectKey, List<Ref> branches) {
		branchDiffs = new ArrayList<>();

		GitDecXtract extractor = new GitDecXtract(projectKey);
		for (Ref branch : branches) {
			branchDiffs.add(new BranchDiff(branch.getName(), extractor.getElements(branch)));
		}
	}

	public List<BranchDiff> getBranches() {
		return branchDiffs;
	}
}
