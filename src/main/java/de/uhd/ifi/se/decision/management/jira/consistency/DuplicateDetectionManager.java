package de.uhd.ifi.se.decision.management.jira.consistency;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import de.uhd.ifi.se.decision.management.jira.consistency.implementation.BasicDuplicateTextDetector;
import de.uhd.ifi.se.decision.management.jira.consistency.implementation.DuplicateFragment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DuplicateDetectionManager {

	private Issue baseIssue;
	private DuplicateDetectionStrategy duplicateDetectionStrategy;

	public DuplicateDetectionManager(Issue baseIssue, DuplicateDetectionStrategy duplicateDetectionStrategy) {
		this.baseIssue = baseIssue;
		this.duplicateDetectionStrategy = duplicateDetectionStrategy;
	}

	public List<DuplicateFragment> findAllDuplicates(Collection<Issue> issuesToCheck) {
		issuesToCheck.remove(this.baseIssue);
		//issuesToCheck.removeAll();// remove discareded issues;
		List foundDuplicateFragments = new ArrayList();
		for (Issue issueToCheck : issuesToCheck) {
			try {
				List foundDuplicateFragmentsForIssue = duplicateDetectionStrategy.detectDuplicateTextFragments(this.baseIssue, issueToCheck);
				foundDuplicateFragments.addAll(foundDuplicateFragmentsForIssue);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return foundDuplicateFragments;
	}

	public Issue getBaseIssue() {
		return this.baseIssue;
	}
}
