package de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.duplicatedetection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.LinkType;
import de.uhd.ifi.se.decision.management.jira.persistence.ConsistencyPersistenceHelper;
import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.DuplicateRecommendation;

public class DuplicateDetectionManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(DuplicateDetectionManager.class);

	private KnowledgeElement knowledgeElement;

	private volatile List<DuplicateRecommendation> duplicateRecommendations;

	private DuplicateTextDetector duplicateDetectionStrategy;

	public DuplicateDetectionManager(KnowledgeElement knowledgeElement,
			DuplicateTextDetector duplicateDetectionStrategy) {
		this.knowledgeElement = knowledgeElement;
		this.duplicateDetectionStrategy = duplicateDetectionStrategy;
	}

	public List<DuplicateRecommendation> findAllDuplicates(Collection<? extends KnowledgeElement> elementsToCheck) {
		duplicateRecommendations = new ArrayList<>();
		elementsToCheck = elementsToCheck.stream().filter((element -> {
			if (element.getJiraIssue() == null || this.knowledgeElement.getJiraIssue() == null) {
				return true;
			}
			return !element.getJiraIssue().getKey().equals(this.knowledgeElement.getJiraIssue().getKey());
		})).collect(Collectors.toList());

		elementsToCheck.remove(this.knowledgeElement);
		elementsToCheck.removeAll(ConsistencyPersistenceHelper.getDiscardedDuplicates(this.knowledgeElement));// remove
																												// discareded
																												// suggestions;
		elementsToCheck.removeAll(this.alreadyLinkedAsDuplicates());// remove linked elements;

		duplicateRecommendations = elementsToCheck.parallelStream().map((element) -> {
			DuplicateRecommendation mostLikelyDuplicate = null;
			try {
				List<DuplicateRecommendation> foundDuplicateFragmentsForIssue = duplicateDetectionStrategy
						.detectDuplicates(knowledgeElement, element);
				mostLikelyDuplicate = findLongestDuplicate(foundDuplicateFragmentsForIssue);
			} catch (Exception e) {
				LOGGER.error(e.getMessage());
			}
			return mostLikelyDuplicate;
		}).collect(Collectors.toList());

		return duplicateRecommendations.parallelStream().filter(Objects::nonNull).collect(Collectors.toList());
	}

	private Collection<? extends KnowledgeElement> alreadyLinkedAsDuplicates() {
		Set<KnowledgeElement> elements = Collections.synchronizedSet(new HashSet<>());
		Set<Link> links = knowledgeElement.getLinks();
		links.parallelStream().forEach(link -> {
			if (link.getType() == LinkType.DUPLICATE) {
				elements.add(link.getTarget());
				elements.add(link.getSource());
			}
		});
		return elements;
	}

	public KnowledgeElement getKnowledgeElement() {
		return knowledgeElement;
	}

	private DuplicateRecommendation findLongestDuplicate(
			List<DuplicateRecommendation> foundDuplicateFragmentsForIssue) {
		DuplicateRecommendation mostLikelyDuplicate = null;

		// if (foundDuplicateFragmentsForIssue != null &&
		// foundDuplicateFragmentsForIssue.size() > 0) {
		for (DuplicateRecommendation fragment : foundDuplicateFragmentsForIssue) {
			if (mostLikelyDuplicate == null || fragment.getLength() > mostLikelyDuplicate.getLength()) {
				mostLikelyDuplicate = fragment;
			}
		}
		// }
		return mostLikelyDuplicate;

	}

}
