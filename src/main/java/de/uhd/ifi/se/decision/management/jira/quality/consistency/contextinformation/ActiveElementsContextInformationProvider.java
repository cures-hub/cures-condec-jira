package de.uhd.ifi.se.decision.management.jira.quality.consistency.contextinformation;

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
public class ActiveElementsContextInformationProvider extends ContextInformationProvider {

	private List<Long> activeIssueIds;

	public ActiveElementsContextInformationProvider() {
		super();
		activeIssueIds = new PlanningModeService.CurrentSprints().getSprintsToIssues().keySet().parallelStream()
				.map(sprintPlanEntry -> sprintPlanEntry.issuesIds).flatMap(Collection::stream)
				.collect(Collectors.toList());
	}

	@Override
	public String getId() {
		return "ActiveCIP_Sprint";
	}

	@Override
	public double assessRelation(KnowledgeElement baseElement, KnowledgeElement elementToTest) {
		LinkSuggestion ls = new LinkSuggestion(baseElement, elementToTest);
		double isActive = activeIssueIds.contains(elementToTest.getJiraIssue().getId()) ? 1. : 0.;
		ls.addToScore(isActive, this.getId());
		this.linkSuggestions.add(ls);
		return isActive;
	}
}
