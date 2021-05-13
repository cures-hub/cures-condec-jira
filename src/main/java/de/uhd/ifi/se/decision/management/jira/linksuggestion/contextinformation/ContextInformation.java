package de.uhd.ifi.se.decision.management.jira.linksuggestion.contextinformation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import de.uhd.ifi.se.decision.management.jira.linksuggestion.suggestions.LinkSuggestion;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.persistence.ConsistencyPersistenceHelper;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;

/**
 * Component in decorator pattern.
 *
 */
public class ContextInformation extends ContextInformationProvider {

	private KnowledgeElement element;
	private List<ContextInformationProvider> cips;

	public ContextInformation(KnowledgeElement element) {
		this.element = element;
		// Add context information providers as concrete decorators
		this.cips = new ArrayList<>();
		this.cips.add(new TextualSimilarityContextInformationProvider());
		this.cips.add(new TracingContextInformationProvider());
		this.cips.add(new TimeContextInformationProvider());
		this.cips.add(new UserContextInformationProvider());
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
	public List<LinkSuggestion> getLinkSuggestions() {
		List<KnowledgeElement> projectKnowledgeElements = KnowledgePersistenceManager
				.getOrCreate(element.getProject().getProjectKey()).getKnowledgeElements();

		projectKnowledgeElements.remove(this.element);
		this.assessRelations(element, projectKnowledgeElements);
		// calculate context score

		// get filtered issues
		Set<KnowledgeElement> elementsToKeep = this.filterKnowledgeElements(projectKnowledgeElements);

		// retain scores of filtered issues
		return cips.stream().flatMap(cip -> cip.getLinkSuggestions().stream()).collect(Collectors.toList());
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
	public double assessRelation(KnowledgeElement baseElement, KnowledgeElement elementToTest) {
		LinkSuggestion linkSuggestion = new LinkSuggestion(this.element, elementToTest);
		for (ContextInformationProvider cip : cips) {
			double nullCompensation = 0.;

			cip.assessRelation(baseElement, elementToTest);
			Collection<LinkSuggestion> suggestions = cip.getLinkSuggestions();
			double sumOfIndividualScoresForCurrentCip = suggestions.parallelStream()
					.mapToDouble(LinkSuggestion::getTotalScore).sum();

			if (sumOfIndividualScoresForCurrentCip == 0) {
				sumOfIndividualScoresForCurrentCip = 1.;
				nullCompensation = 1. / suggestions.size();
			}

			double finalSumOfIndividualScoresForCurrentCip = sumOfIndividualScoresForCurrentCip;
			// Divide each score by the max value to scale it to [0,1]
			double finalNullCompensation = nullCompensation;
			suggestions.parallelStream().forEach(score -> {
				// System.out.println("Thread : " + Thread.currentThread().getName() + ", value:
				// " + score.getTargetElement().getKey());
				linkSuggestion.addToScore((score.getTotalScore() + finalNullCompensation)
						/ (finalSumOfIndividualScoresForCurrentCip * this.cips.size()), cip.getName());// sumOfIndividualScoresForCurrentCip);
			});
		}
		return 0.0;
	}

	@Override
	public String getId() {
		return "BaseCalculation";
	}

	@Override
	public String getName() {
		return "BaseCalculation";
	}

}
