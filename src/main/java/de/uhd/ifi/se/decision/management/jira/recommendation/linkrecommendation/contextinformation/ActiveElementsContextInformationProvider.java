package de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.contextinformation;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.atlassian.greenhopper.web.rapid.plan.PlanningModeService;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.recommendation.RecommendationScore;

/**
 * Assumes that the {@link KnowledgeElement}s within the active sprint are
 * related.
 */
public class ActiveElementsContextInformationProvider extends ContextInformationProvider {

	private List<Long> activeIssueIds;

	public ActiveElementsContextInformationProvider() {
		super();
		isActive = false;
		activeIssueIds = new PlanningModeService.CurrentSprints().getSprintsToIssues().keySet().parallelStream()
				.map(sprintPlanEntry -> sprintPlanEntry.issuesIds).flatMap(Collection::stream)
				.collect(Collectors.toList());
	}

	@Override
	public RecommendationScore assessRelation(KnowledgeElement baseElement, KnowledgeElement elementToTest) {
		double isActive = activeIssueIds.contains(elementToTest.getJiraIssue().getId()) ? 1. : 0.;
		return new RecommendationScore((float) isActive, getName() + " (same Sprint)");
	}

	@Override
	public String getExplanation() {
		return "Assumes that the knowledge elements within the active sprint are related.";
	}

	@Override
	public String getDescription() {
		return "Recommend elements that are assigned to the same decision group as the source element.";
	}
}
