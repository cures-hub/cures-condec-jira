package de.uhd.ifi.se.decision.management.jira.quality.consistency.contextinformation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.atlassian.greenhopper.web.rapid.plan.PlanningModeService;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.quality.consistency.suggestions.LinkSuggestion;

/**
 * Assumes that the {@link KnowledgeElement}s within the active sprint are
 * related.
 */
public class ActiveElementsContextInformationProvider implements ContextInformationProvider {
	private List<LinkSuggestion> linkSuggestions = new ArrayList<>();

	@Override
	public String getId() {
		return "ActiveCIP_Sprint";
	}

	@Override
	public String getName() {
		return "Active elements";
	}

	@Override
	public Collection<LinkSuggestion> getLinkSuggestions() {
		return linkSuggestions;
	}

	@Override
	public void assessRelation(KnowledgeElement baseElement, List<KnowledgeElement> knowledgeElements) {
		List<Long> activeIssueIds = new PlanningModeService.CurrentSprints().getSprintsToIssues().keySet()
				.parallelStream().map(sprintPlanEntry -> sprintPlanEntry.issuesIds).flatMap(Collection::stream)
				.collect(Collectors.toList());

		this.linkSuggestions = knowledgeElements.parallelStream().map(knowledgeElement -> {
			LinkSuggestion ls = new LinkSuggestion(baseElement, knowledgeElement);
			double isActive = activeIssueIds.contains(knowledgeElement.getJiraIssue().getId()) ? 1. : 0.;
			ls.addToScore(isActive, this.getId());
			return ls;
		}).collect(Collectors.toList());
	}
}
