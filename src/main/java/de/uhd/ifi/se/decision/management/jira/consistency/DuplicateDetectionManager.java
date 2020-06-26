package de.uhd.ifi.se.decision.management.jira.consistency;

import com.atlassian.jira.issue.Issue;
import de.uhd.ifi.se.decision.management.jira.consistency.implementation.DuplicateFragment;
import de.uhd.ifi.se.decision.management.jira.persistence.ConsistencyPersistenceHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class DuplicateDetectionManager {

	private Issue baseIssue;
	private DuplicateDetectionStrategy duplicateDetectionStrategy;

	public DuplicateDetectionManager(Issue baseIssue, DuplicateDetectionStrategy duplicateDetectionStrategy) {
		this.baseIssue = baseIssue;
		this.duplicateDetectionStrategy = duplicateDetectionStrategy;
	}

	public List<DuplicateFragment> findAllDuplicates(Collection<? extends Issue> issuesToCheck) {
		List<DuplicateFragment> foundDuplicateFragments = Collections.synchronizedList(new ArrayList<>());

		if (this.baseIssue != null) {
			issuesToCheck.remove(this.baseIssue);
			issuesToCheck.removeAll(ConsistencyPersistenceHelper.getDiscardedDuplicates(this.baseIssue.getKey()));// remove discareded issues;
			issuesToCheck.removeAll(new ContextInformation(this.baseIssue).getLinkedIssues());// remove discareded issues;


			issuesToCheck.parallelStream().forEach((issueToCheck) -> {
				try {
					List<DuplicateFragment> foundDuplicateFragmentsForIssue = duplicateDetectionStrategy.detectDuplicateTextFragments(this.baseIssue, issueToCheck);
					DuplicateFragment mostLikelyDuplicate = findLongestDuplicate(foundDuplicateFragmentsForIssue);
					if (mostLikelyDuplicate != null) {
						foundDuplicateFragments.add(mostLikelyDuplicate);

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			});

			/*
			for (Issue issueToCheck : issuesToCheck) {
				try {
					List<DuplicateFragment> foundDuplicateFragmentsForIssue = duplicateDetectionStrategy.detectDuplicateTextFragments(this.baseIssue, issueToCheck);
					if (foundDuplicateFragmentsForIssue.size() > 0) {
						DuplicateFragment mostLikelyDuplicate = null;
						for (DuplicateFragment fragment : foundDuplicateFragmentsForIssue) {
							if (mostLikelyDuplicate == null || fragment.getLength() > mostLikelyDuplicate.getLength()) {
								mostLikelyDuplicate = fragment;
							}
						}
						foundDuplicateFragments.add(mostLikelyDuplicate);
					}


				} catch (Exception e) {
					e.printStackTrace();
				}
			}*/
		}

		return foundDuplicateFragments;
	}

	private DuplicateFragment findLongestDuplicate(List<DuplicateFragment> foundDuplicateFragmentsForIssue) {
		DuplicateFragment mostLikelyDuplicate = null;

		//if (foundDuplicateFragmentsForIssue != null && foundDuplicateFragmentsForIssue.size() > 0) {
		for (DuplicateFragment fragment : foundDuplicateFragmentsForIssue) {
			if (mostLikelyDuplicate == null || fragment.getLength() > mostLikelyDuplicate.getLength()) {
				mostLikelyDuplicate = fragment;
			}
		}
		//}
		return mostLikelyDuplicate;

	}

	public Issue getBaseIssue() {
		return this.baseIssue;
	}
}
