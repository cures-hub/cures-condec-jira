package de.uhd.ifi.se.decision.management.jira.quality.consistency.contextinformation;

import java.util.List;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.quality.consistency.suggestions.LinkSuggestion;

/**
 * Uses information about the author of a {@link KnowledgeElement} or about the
 * person last modifying an element for rating relations. A relation is assumed
 * if an element have been implemented or modified by the same user.
 */
public class UserContextInformationProvider extends ContextInformationProvider {

	@Override
	public String getId() {
		return "UserCIP_equalCreatorOrEqualAssignee";
	}

	@Override
	public void assessRelations(KnowledgeElement baseElement, List<KnowledgeElement> knowledgeElements) {
		for (KnowledgeElement elementToTest : knowledgeElements) {
			assessRelation(baseElement, elementToTest);
		}
	}

	@Override
	public double assessRelation(KnowledgeElement baseElement, KnowledgeElement elementToTest) {
		LinkSuggestion linkSuggestion = new LinkSuggestion(baseElement, elementToTest);

		Double score = 0.;
		if (baseElement.getJiraIssue() != null && elementToTest.getJiraIssue() != null) {
			score = this.isApplicationUserEqual(baseElement.getJiraIssue().getCreator(),
					elementToTest.getJiraIssue().getCreator());
			score += this.isApplicationUserEqual(baseElement.getJiraIssue().getAssignee(),
					elementToTest.getJiraIssue().getAssignee())
					+ this.isApplicationUserEqual(baseElement.getJiraIssue().getReporter(),
							elementToTest.getJiraIssue().getReporter())
					+ this.isApplicationUserEqual(baseElement.getJiraIssue().getArchivedByUser(),
							elementToTest.getJiraIssue().getArchivedByUser());
		}
		linkSuggestion.addToScore(score, this.getName());

		linkSuggestions.add(linkSuggestion);
		return score;
	}

	private Double isApplicationUserEqual(ApplicationUser user1, ApplicationUser user2) {
		int isUserEqual = 0;
		if (user1 != null && user1.equals(user2)) { // || (user1 == null && user2 == null)) {
			isUserEqual = 1;
		}
		return Double.valueOf(isUserEqual);
	}
}
