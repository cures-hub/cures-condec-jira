package de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.contextinformation;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.recommendation.RecommendationScore;

/**
 * Uses information about the author of a {@link KnowledgeElement} or about the
 * person last modifying an element for rating relations. A relation is assumed
 * if an element have been implemented or modified by the same user.
 */
public class UserContextInformationProvider extends ContextInformationProvider {

	public UserContextInformationProvider() {
		super();
		isActive = false;
	}

	@Override
	public RecommendationScore assessRelation(KnowledgeElement baseElement, KnowledgeElement elementToTest) {
		double score = isApplicationUserEqual(baseElement.getCreator(), elementToTest.getCreator());
		Issue baseElementJiraIssue = baseElement.getJiraIssue();
		Issue otherElementJiraIssue = elementToTest.getJiraIssue();
		if (baseElementJiraIssue != null && otherElementJiraIssue != null) {
			score += isApplicationUserEqual(baseElementJiraIssue.getAssignee(), otherElementJiraIssue.getAssignee());
			score += isApplicationUserEqual(baseElementJiraIssue.getReporter(), otherElementJiraIssue.getReporter());
		}
		return score >= 1.0 ? new RecommendationScore((float) 1.0, getName() + " (equalCreatorOrEqualAssignee)")
			: new RecommendationScore((float) 0.75, getName() + " (equalCreatorOrEqualAssignee)");
	}

	public static double isApplicationUserEqual(ApplicationUser user1, ApplicationUser user2) {
		if (user1 != null && user1.equals(user2)) {
			return 1.0;
		}
		return 0;
	}

	@Override
	public String getExplanation() {
		return "Assumes that knowledge elements created or modified by the same user are related.";
	}
}