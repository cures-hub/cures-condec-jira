package de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.duplicatedetection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.LinkType;
import de.uhd.ifi.se.decision.management.jira.persistence.recommendation.DiscardedRecommendationPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.recommendation.Recommendation;
import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.DuplicateRecommendation;
import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.contextinformation.ContextInformation;

public class DuplicateDetectionManager {

	private KnowledgeElement knowledgeElement;
	private DuplicateTextDetector duplicateDetector;

	public DuplicateDetectionManager(KnowledgeElement knowledgeElement,
			DuplicateTextDetector duplicateDetectionStrategy) {
		this.knowledgeElement = knowledgeElement;
		this.duplicateDetector = duplicateDetectionStrategy;
	}

	public List<Recommendation> findAllDuplicates(List<KnowledgeElement> elementsToCheck) {
		List<Recommendation> duplicateRecommendations = new ArrayList<>();

		elementsToCheck.remove(knowledgeElement);
		elementsToCheck.removeAll(alreadyLinkedAsDuplicates());// remove linked elements;

		duplicateRecommendations = elementsToCheck.parallelStream().map((element) -> {
			DuplicateRecommendation mostLikelyDuplicate = null;
			List<DuplicateRecommendation> foundDuplicateFragmentsForIssue = duplicateDetector
					.detectDuplicates(knowledgeElement, element);
			mostLikelyDuplicate = findLongestDuplicate(foundDuplicateFragmentsForIssue);
			return mostLikelyDuplicate;
		}).filter(recommendation -> recommendation != null).collect(Collectors.toList());

		ContextInformation.markDiscardedRecommendations(duplicateRecommendations,
				DiscardedRecommendationPersistenceManager.getDiscardedDuplicateRecommendations(knowledgeElement));
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
		for (DuplicateRecommendation fragment : foundDuplicateFragmentsForIssue) {
			if (mostLikelyDuplicate == null || fragment.getLength() > mostLikelyDuplicate.getLength()) {
				mostLikelyDuplicate = fragment;
			}
		}
		return mostLikelyDuplicate;
	}
}