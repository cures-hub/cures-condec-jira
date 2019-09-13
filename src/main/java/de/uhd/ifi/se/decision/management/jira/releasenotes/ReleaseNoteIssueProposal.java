package de.uhd.ifi.se.decision.management.jira.releasenotes;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.user.ApplicationUser;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.releasenotes.impl.ReleaseNoteIssueProposalImpl;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

import java.util.EnumMap;
import java.util.HashMap;

/**
 * Interface Release Note Issue Proposal
 * It saves the decision knowledge element, the final rating and the issue metrics.
 */
@JsonDeserialize(as = ReleaseNoteIssueProposalImpl.class)
public interface ReleaseNoteIssueProposal {

	/**
	 * Get the DecisionKnowledgeElement.
	 *
	 * @return DecisionKnowledgeElement of the ReleaseNoteIssueProposal.
	 */
	DecisionKnowledgeElement getDecisionKnowledgeElement();

	/**
	 * Set the DecisionKnowledgeElement.
	 *
	 * @param decisionKnowledgeElement of the ReleaseNoteIssueProposal.
	 */
	void setDecisionKnowledgeElement(DecisionKnowledgeElement decisionKnowledgeElement);

	/**
	 * Get rating of the ReleaseNoteIssueProposal.
	 *
	 * @return ratings of ReleaseNoteIssueProposal.
	 */
	double getRating();

	/**
	 * Set rating of the ReleaseNoteIssueProposal.
	 *
	 * @param rating of ReleaseNoteIssueProposal.
	 */
	void setRating(double rating);

	/**
	 * Get metrics of the ReleaseNoteIssueProposal.
	 *
	 * @return metrics of ReleaseNoteIssueProposal.
	 */
	EnumMap<IssueMetric, Integer> getMetrics();

	/**
	 * Set metrics of ReleaseNoteIssueProposal.
	 *
	 * @param metrics of ReleaseNoteIssueProposal.
	 */
	void setMetrics(EnumMap<IssueMetric, Integer> metrics);

	/**
	 * Gets the priority of the issue and sets the priority criteria of the ReleaseNoteIssueProposal
	 * @param issue of the associated DecisionKnowledgeElement
	 */
	void getAndSetPriority(Issue issue);
	/**
	 * Gets the amount of comments of the issue and sets the count comment criteria of the ReleaseNoteIssueProposal
	 * @param issue of the associated DecisionKnowledgeElement
	 */
	void getAndSetCountOfComments(Issue issue);
	/**
	 * Gets the size of the summary and sets the size summary criteria of the ReleaseNoteIssueProposal
	 */
	void getAndSetSizeOfSummary();
	/**
	 * Gets the size of the description and sets the size description criteria of the ReleaseNoteIssueProposal
	 */
	void getAndSetSizeOfDescription();
	/**
	 * Gets the days to completion of the issue and sets the days to completion criteria of the ReleaseNoteIssueProposal
	 * @param issue of the associated DecisionKnowledgeElement
	 */
	void getAndSetDaysToCompletion(Issue issue);
	/**
	 * Gets the total count of created issues of the issue reporter and sets the experienceReporter criteria
	 * of the ReleaseNoteIssueProposal. The existing Reporter count HashMap is used to avoid duplicated equal JQL queries.
	 * The result may differ, depending of the logged-in user and his permissions.
	 * @param issue of the associated DecisionKnowledgeElement
	 * @param existingReporterCount HashMap to save JQL results
	 * @param user Application user which makes the request
	 */
	void getAndSetExperienceReporter(Issue issue, HashMap<String, Integer> existingReporterCount, ApplicationUser user);

	/**
	 * Gets the total count of resolved issues of the issue resolver and sets the experienceResolver criteria
	 * of the ReleaseNoteIssueProposal. The existing Resolver count HashMap is used to avoid duplicated equal JQL queries.
	 * The result may differ, depending of the logged-in user and his permissions.
	 * @param issue of the associated DecisionKnowledgeElement
	 * @param existingResolverCount HashMap to save JQL results
	 * @param user Application user which makes the request
	 */
	void getAndSetExperienceResolver(Issue issue, HashMap<String, Integer> existingResolverCount, ApplicationUser user);

}