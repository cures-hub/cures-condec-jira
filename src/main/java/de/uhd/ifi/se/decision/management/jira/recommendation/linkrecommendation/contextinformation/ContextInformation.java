package de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.contextinformation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.persistence.ConsistencyPersistenceHelper;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.recommendation.Recommendation;
import de.uhd.ifi.se.decision.management.jira.recommendation.RecommendationScore;
import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.LinkRecommendation;

/**
 * Component in decorator pattern.
 *
 */
public class ContextInformation implements ContextInformationProvider {

	private KnowledgeElement element;
	private List<ContextInformationProvider> contextInformationProviders;

	public ContextInformation(KnowledgeElement element) {
		this.element = element;
		// Add context information providers as concrete decorators
		this.contextInformationProviders = new ArrayList<>();
		this.contextInformationProviders.add(new TextualSimilarityContextInformationProvider());
		this.contextInformationProviders.add(new TracingContextInformationProvider());
		this.contextInformationProviders.add(new TimeContextInformationProvider());
		this.contextInformationProviders.add(new UserContextInformationProvider());
		// this.cips.add(new ActiveCIP());
	}

	public Collection<KnowledgeElement> getLinkedKnowledgeElements() {
		Set<KnowledgeElement> linkedKnowledgeElements = new HashSet<>();
		Set<Link> linkCollection = this.element.getLinks();
		if (linkCollection != null) {
			for (Link link : linkCollection) {
				linkedKnowledgeElements.addAll(link.getBothElements());
			}
		}
		return linkedKnowledgeElements;
	}

	public List<Recommendation> getLinkSuggestions() {
		List<KnowledgeElement> projectKnowledgeElements = KnowledgePersistenceManager
				.getOrCreate(element.getProject().getProjectKey()).getKnowledgeElements();

		projectKnowledgeElements.remove(this.element);
		List<Recommendation> linkSuggestions = assessRelations(element, projectKnowledgeElements);
		// calculate context score

		// get filtered issues
		Set<KnowledgeElement> elementsToKeep = this.filterKnowledgeElements(projectKnowledgeElements);
		float maxScoreValue = Recommendation.getMaxScoreValue(linkSuggestions);
		for (Recommendation suggestion : linkSuggestions) {
			suggestion.getScore().normalizeTo(maxScoreValue);
		}
		// retain scores of filtered issues
		return linkSuggestions;
	}

	private Set<KnowledgeElement> filterKnowledgeElements(List<KnowledgeElement> projectKnowledgeElements) {
		// Create union of all issues to be filtered out.
		Set<KnowledgeElement> filteredKnowledgeElements = new HashSet<>(projectKnowledgeElements);
		Set<KnowledgeElement> filterOutElements = new HashSet<>(this.getLinkedKnowledgeElements());
		filterOutElements.addAll(ConsistencyPersistenceHelper.getDiscardedLinkSuggestions(element));
		filterOutElements.add(element);
		filterOutElements.addAll(filteredKnowledgeElements.stream()
				.filter(e -> e.getJiraIssue() != null && e.getJiraIssue().equals(element.getJiraIssue()))
				.collect(Collectors.toList()));

		// Calculate difference between all issues of project and the issues that need
		// to be filtered out.
		filteredKnowledgeElements.removeAll(filterOutElements);

		return filteredKnowledgeElements;
	}

	@Override
	public RecommendationScore assessRelation(KnowledgeElement baseElement, KnowledgeElement otherElement) {
		RecommendationScore score = new RecommendationScore(0, getName());
		for (ContextInformationProvider contextInformationProvider : contextInformationProviders) {
			RecommendationScore scoreValue = contextInformationProvider.assessRelation(baseElement, otherElement);
			score.addSubScore(scoreValue);
		}
		return score;
	}

	/**
	 * Calculates the relationship between one {@link KnowledgeElement} to a list of
	 * other {@link KnowledgeElement}s. Higher values indicate a higher similarity.
	 * The value is called Context Relationship Indicator in the paper.
	 *
	 * @param baseElement
	 * @param knowledgeElements
	 * @return value of relationship in [0, inf]
	 */
	public List<Recommendation> assessRelations(KnowledgeElement baseElement,
			List<KnowledgeElement> knowledgeElements) {
		List<Recommendation> linkRecommendations = new ArrayList<>();
		for (KnowledgeElement elementToTest : knowledgeElements) {
			LinkRecommendation linkSuggestion = new LinkRecommendation(baseElement, elementToTest);
			linkSuggestion.setScore(assessRelation(baseElement, elementToTest));
			linkRecommendations.add(linkSuggestion);
		}
		return linkRecommendations;
	}
}
