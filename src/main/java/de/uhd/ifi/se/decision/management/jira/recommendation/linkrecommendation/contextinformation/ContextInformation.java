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
public class ContextInformation extends ContextInformationProvider {

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

	@Override
	public List<Recommendation> getLinkSuggestions() {
		List<KnowledgeElement> projectKnowledgeElements = KnowledgePersistenceManager
				.getOrCreate(element.getProject().getProjectKey()).getKnowledgeElements();

		projectKnowledgeElements.remove(this.element);
		this.assessRelations(element, projectKnowledgeElements);
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
	public RecommendationScore assessRelation(KnowledgeElement baseElement, KnowledgeElement elementToTest) {
		LinkRecommendation linkSuggestion = new LinkRecommendation(baseElement, elementToTest);
		RecommendationScore score = new RecommendationScore(0, getName());
		for (ContextInformationProvider cip : contextInformationProviders) {
			RecommendationScore scoreValue = cip.assessRelation(baseElement, elementToTest);
			score.addSubScore(scoreValue);
		}
		linkSuggestion.setScore(score);
		linkSuggestions.add(linkSuggestion);
		return score;
	}
}
