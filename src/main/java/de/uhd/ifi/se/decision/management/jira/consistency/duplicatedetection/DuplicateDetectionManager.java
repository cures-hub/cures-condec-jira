package de.uhd.ifi.se.decision.management.jira.consistency.duplicatedetection;

import com.atlassian.jira.issue.Issue;
import de.uhd.ifi.se.decision.management.jira.consistency.contextinformation.ContextInformation;
import de.uhd.ifi.se.decision.management.jira.consistency.suggestions.DuplicateSuggestion;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.ConsistencyPersistenceHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class DuplicateDetectionManager {

	private KnowledgeElement knowledgeElement;
	private DuplicateDetectionStrategy duplicateDetectionStrategy;

	public DuplicateDetectionManager(Issue knowledgeElement, DuplicateDetectionStrategy duplicateDetectionStrategy) {
		this(new KnowledgeElement(knowledgeElement), duplicateDetectionStrategy);
	}

	public DuplicateDetectionManager(KnowledgeElement knowledgeElement, DuplicateDetectionStrategy duplicateDetectionStrategy) {
		this.knowledgeElement = knowledgeElement;
		this.duplicateDetectionStrategy = duplicateDetectionStrategy;
	}

	public List<DuplicateSuggestion> findAllDuplicates(Collection<? extends KnowledgeElement> elementsToCheck) {
		List<DuplicateSuggestion> foundDuplicateSuggestions = Collections.synchronizedList(new ArrayList<>());

		if (this.knowledgeElement != null) {
			elementsToCheck.remove(this.knowledgeElement);
			elementsToCheck.removeAll(ConsistencyPersistenceHelper.getDiscardedDuplicates(this.knowledgeElement.getKey()));// remove discareded issues;
			elementsToCheck.removeAll(new ContextInformation(this.knowledgeElement).getLinkedIssues());// remove discareded issues;


			elementsToCheck.parallelStream().forEach((issueToCheck) -> {
				try {
					List<DuplicateSuggestion> foundDuplicateFragmentsForIssue = duplicateDetectionStrategy.detectDuplicateTextFragments(this.knowledgeElement, issueToCheck);
					DuplicateSuggestion mostLikelyDuplicate = findLongestDuplicate(foundDuplicateFragmentsForIssue);
					if (mostLikelyDuplicate != null) {
						foundDuplicateSuggestions.add(mostLikelyDuplicate);

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			});

			/*
			for (Issue issueToCheck : elementsToCheck) {
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

		return foundDuplicateSuggestions;
	}

	private DuplicateSuggestion findLongestDuplicate(List<DuplicateSuggestion> foundDuplicateFragmentsForIssue) {
		DuplicateSuggestion mostLikelyDuplicate = null;

		//if (foundDuplicateFragmentsForIssue != null && foundDuplicateFragmentsForIssue.size() > 0) {
		for (DuplicateSuggestion fragment : foundDuplicateFragmentsForIssue) {
			if (mostLikelyDuplicate == null || fragment.getLength() > mostLikelyDuplicate.getLength()) {
				mostLikelyDuplicate = fragment;
			}
		}
		//}
		return mostLikelyDuplicate;

	}

	public KnowledgeElement getKnowledgeElement() {
		return this.knowledgeElement;
	}

}
