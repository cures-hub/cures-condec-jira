package de.uhd.ifi.se.decision.management.jira.consistency.duplicatedetection;

import com.atlassian.jira.issue.Issue;
import de.uhd.ifi.se.decision.management.jira.consistency.suggestions.DuplicateSuggestion;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.LinkType;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.ConsistencyPersistenceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class DuplicateDetectionManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(DuplicateDetectionManager.class);

	private KnowledgeElement knowledgeElement;

	private volatile List<DuplicateSuggestion> foundDuplicateSuggestions;

	private int minDuplicateLength;

	private DuplicateDetectionStrategy duplicateDetectionStrategy;

	public DuplicateDetectionManager(Issue knowledgeElement, DuplicateDetectionStrategy duplicateDetectionStrategy) {
		this(new KnowledgeElement(knowledgeElement), duplicateDetectionStrategy);
	}

	public DuplicateDetectionManager(KnowledgeElement knowledgeElement, DuplicateDetectionStrategy duplicateDetectionStrategy) {
		this.knowledgeElement = knowledgeElement;
		this.duplicateDetectionStrategy = duplicateDetectionStrategy;
		this.minDuplicateLength = ConfigPersistenceManager.getMinDuplicateLength(this.knowledgeElement.getProject().getProjectKey());
	}

	public List<DuplicateSuggestion> findAllDuplicates(Collection<? extends KnowledgeElement> elementsToCheck) {

		foundDuplicateSuggestions = new ArrayList<>();

		if (this.knowledgeElement != null) {
			elementsToCheck = elementsToCheck
				.stream()
				.filter((element -> {
					if (element.getJiraIssue() == null || this.knowledgeElement.getJiraIssue() == null) {
						return true;
					}
					return !element.getJiraIssue().getKey().equals(this.knowledgeElement.getJiraIssue().getKey());
				}))
				.collect(Collectors.toList());


			elementsToCheck.remove(this.knowledgeElement);
			elementsToCheck.removeAll(ConsistencyPersistenceHelper.getDiscardedDuplicates(this.knowledgeElement));// remove discareded suggestions;
			elementsToCheck.removeAll(this.alreadyLinkedAsDuplicates());// remove linked elements;


			foundDuplicateSuggestions = elementsToCheck
				.parallelStream()
				.map((element) -> {
					//System.out.println("Thread : " + Thread.currentThread().getName() + ", value: " + element.getKey());
					DuplicateSuggestion mostLikelyDuplicate = null;
					try {
						List<DuplicateSuggestion> foundDuplicateFragmentsForIssue = new BasicDuplicateTextDetector(minDuplicateLength)
							.detectDuplicates(this.knowledgeElement, element);
						mostLikelyDuplicate = findLongestDuplicate(foundDuplicateFragmentsForIssue);
					} catch (Exception e) {
						LOGGER.error(e.getMessage());
					}
					return mostLikelyDuplicate;
				}).collect(Collectors.toList());
		}

		return foundDuplicateSuggestions
			.parallelStream()
			.filter(Objects::nonNull)
			.collect(Collectors.toList());
	}

	private Collection<? extends KnowledgeElement> alreadyLinkedAsDuplicates() {
		Set<KnowledgeElement> elements = Collections.synchronizedSet(new HashSet<>());
		List<Link> links = this.knowledgeElement.getLinks();
		links
			.parallelStream()
			.forEach(link -> {
				if (link.getLinkType().equals(LinkType.DUPLICATE)) {
					elements.add(link.getTarget());
					elements.add(link.getSource());
				}
			});
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
