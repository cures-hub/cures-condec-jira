package de.uhd.ifi.se.decision.management.jira.consistency.duplicatedetection;

import com.atlassian.jira.issue.Issue;
import de.uhd.ifi.se.decision.management.jira.consistency.suggestions.DuplicateSuggestion;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.LinkType;
import de.uhd.ifi.se.decision.management.jira.persistence.ConsistencyPersistenceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DuplicateDetectionManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(DuplicateDetectionManager.class);


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
			elementsToCheck.removeAll(ConsistencyPersistenceHelper.getDiscardedDuplicates(this.knowledgeElement));// remove discareded suggestions;
			elementsToCheck.removeAll(this.alreadyLinkedAsDuplicates());// remove linked elements;


			elementsToCheck.parallelStream().forEach((issueToCheck) -> {
				try {
					List<DuplicateSuggestion> foundDuplicateFragmentsForIssue = duplicateDetectionStrategy.detectDuplicates(this.knowledgeElement, issueToCheck);
					DuplicateSuggestion mostLikelyDuplicate = findLongestDuplicate(foundDuplicateFragmentsForIssue);
					if (mostLikelyDuplicate != null) {
						foundDuplicateSuggestions.add(mostLikelyDuplicate);

					}
				} catch (Exception e) {
					LOGGER.error(e.getMessage());
				}
			});
		}

		return foundDuplicateSuggestions;
	}

	private Collection<? extends KnowledgeElement> alreadyLinkedAsDuplicates() {
		Set<KnowledgeElement> elements = new HashSet<>();
		List<Link> links = this.knowledgeElement.getLinks();
		for (Link link : links){
			if(link.getLinkType().equals(LinkType.DUPLICATE)){
				elements.add(link.getTarget());
				elements.add(link.getSource());
			}
		}
		return elements;
	}


	public void setDuplicateDetectionStrategy(DuplicateDetectionStrategy duplicateDetectionStrategy) {
		this.duplicateDetectionStrategy = duplicateDetectionStrategy;
	}

	public KnowledgeElement getKnowledgeElement() {
		return this.knowledgeElement;
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


}
