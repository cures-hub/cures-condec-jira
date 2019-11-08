package de.uhd.ifi.se.decision.management.jira.releasenotes.impl;

import java.util.EnumMap;
import java.util.HashMap;

import javax.xml.bind.annotation.XmlElement;

import org.codehaus.jackson.annotate.JsonProperty;

import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.comments.CommentManager;
import com.atlassian.jira.issue.priority.Priority;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.jql.builder.JqlQueryBuilder;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.releasenotes.JiraIssueMetric;
import de.uhd.ifi.se.decision.management.jira.releasenotes.ReleaseNoteIssueProposal;

/**
 * Model class for the release note Jira issue proposal It saves the decision
 * knowledge element, the final rating and the issue metrics.
 */
public class ReleaseNoteIssueProposalImpl implements ReleaseNoteIssueProposal {

	private DecisionKnowledgeElement decisionKnowledgeElement;

	private EnumMap<JiraIssueMetric, Integer> jiraIssueMetrics;
	private double rating;

	/**
	 * Constructer to initialize default values and add count of DK
	 *
	 * @param decisionKnowledgeElement
	 * @param countDecisionKnowledge
	 */
	public ReleaseNoteIssueProposalImpl(DecisionKnowledgeElement decisionKnowledgeElement, int countDecisionKnowledge) {
		this.decisionKnowledgeElement = decisionKnowledgeElement;
		// set default values
		this.jiraIssueMetrics = JiraIssueMetric.toIntegerEnumMap();
		this.jiraIssueMetrics.put(JiraIssueMetric.COUNT_DECISION_KNOWLEDGE, countDecisionKnowledge);

	}

	/**
	 * @return decisionKnowledgeElement of the ReleaseNoteIssueProposal
	 */
	@Override
	@XmlElement(name = "decisionKnowledgeElement")
	public DecisionKnowledgeElement getDecisionKnowledgeElement() {
		return this.decisionKnowledgeElement;
	}

	/**
	 * @param decisionKnowledgeElement
	 *            of the ReleaseNoteIssueProposal.
	 */
	@Override
	@JsonProperty("decisionKnowledgeElement")
	public void setDecisionKnowledgeElement(DecisionKnowledgeElement decisionKnowledgeElement) {
		this.decisionKnowledgeElement = decisionKnowledgeElement;
	}

	/**
	 * @return rating of the ReleaseNoteIssueProposal
	 */
	@Override
	@XmlElement(name = "rating")
	public double getRating() {
		return this.rating;
	}

	/**
	 * @param rating
	 *            of the ReleaseNoteIssueProposal.
	 */
	@Override
	@JsonProperty("rating")
	public void setRating(double rating) {
		this.rating = rating;
	}

	/**
	 * Get criteria Prioritisation of the ReleaseNoteIssueProposal.
	 *
	 * @return jiraIssueMetrics of the ReleaseNoteIssueProposal.
	 */
	@Override
	@XmlElement(name = "jiraIssueMetrics")
	public EnumMap<JiraIssueMetric, Integer> getMetrics() {
		return this.jiraIssueMetrics;
	}

	/**
	 * set jiraIssueMetrics of the ReleaseNoteIssueProposal.
	 *
	 * @param jiraIssueMetrics
	 *            of the ReleaseNoteIssueProposal.
	 */
	@Override
	public void setMetrics(EnumMap<JiraIssueMetric, Integer> jiraIssueMetrics) {
		this.jiraIssueMetrics = jiraIssueMetrics;
	}

	/**
	 * Gets the priority of the issue and sets the priority criteria of the
	 * ReleaseNoteIssueProposal
	 *
	 * @param issue
	 *            of the associated DecisionKnowledgeElement
	 */
	@Override
	public void getAndSetPriority(Issue issue) {
		Priority priority = issue.getPriority();
		if (priority != null) {
			int sequence = Math.toIntExact(priority.getSequence());
			this.getMetrics().put(JiraIssueMetric.PRIORITY, sequence);
		} else {
			// set medium value for DK elements for priority
			this.getMetrics().put(JiraIssueMetric.PRIORITY, 3);
		}
	}

	/**
	 * Gets the amount of comments of the issue and sets the count comment criteria
	 * of the ReleaseNoteIssueProposal
	 *
	 * @param issue
	 *            of the associated DecisionKnowledgeElement
	 */
	@Override
	public void getAndSetCountOfComments(Issue issue) {
		CommentManager commentManager = ComponentAccessor.getCommentManager();
		int countComments = commentManager.getComments(issue).size();
		this.getMetrics().put(JiraIssueMetric.COUNT_COMMENTS, countComments);
	}

	/**
	 * Gets the size of the summary and sets the size summary criteria of the
	 * ReleaseNoteIssueProposal
	 */
	@Override
	public void getAndSetSizeOfSummary() {
		int sizeSummary = countWordsUsingSplit(this.getDecisionKnowledgeElement().getSummary());
		this.getMetrics().put(JiraIssueMetric.SIZE_SUMMARY, sizeSummary);
	}

	/**
	 * Gets the size of the description and sets the size description criteria of
	 * the ReleaseNoteIssueProposal
	 */
	@Override
	public void getAndSetSizeOfDescription() {
		int sizeDescription = countWordsUsingSplit(this.getDecisionKnowledgeElement().getDescription());
		this.getMetrics().put(JiraIssueMetric.SIZE_DESCRIPTION, sizeDescription);
	}

	/**
	 * Gets the days to completion of the issue and sets the days to completion
	 * criteria of the ReleaseNoteIssueProposal
	 *
	 * @param issue
	 *            of the associated DecisionKnowledgeElement
	 */
	@Override
	public void getAndSetDaysToCompletion(Issue issue) {
		Long created = issue.getCreated().getTime();
		Long resolved = issue.getResolutionDate().getTime();
		Long diff = resolved - created;
		int days = (int) Math.floor(diff / (1000 * 60 * 60 * 24));
		this.getMetrics().put(JiraIssueMetric.DAYS_COMPLETION, days);
	}

	/**
	 * Gets the total count of created issues of the issue reporter and sets the
	 * experienceReporter criteria of the ReleaseNoteIssueProposal. The existing
	 * Reporter count HashMap is used to avoid duplicated equal JQL queries. The
	 * result may differ, depending of the logged-in user and his permissions.
	 *
	 * @param issue
	 *            of the associated DecisionKnowledgeElement
	 * @param existingReporterCount
	 *            HashMap to save JQL results
	 * @param user
	 *            Application user which makes the request
	 */
	@Override
	public void getAndSetExperienceReporter(Issue issue, HashMap<String, Integer> existingReporterCount,
			ApplicationUser user) {
		// first check if user was already checked
		SearchService searchProvider = ComponentAccessor.getComponentOfType(SearchService.class);
		String reporterId = issue.getReporterId();
		if (reporterId == null) {
			reporterId = issue.getReporter().getKey();
		}
		Integer reporterExistingCount = existingReporterCount.get(reporterId);
		Integer countReporter = 0;

		if (reporterExistingCount != null) {
			countReporter = reporterExistingCount;
		} else {
			JqlQueryBuilder builder = JqlQueryBuilder.newBuilder();
			builder.where().reporterUser(reporterId);
			try {
				countReporter = Math.toIntExact(searchProvider.searchCount(user, builder.buildQuery()));
			} catch (SearchException e) {
				e.printStackTrace();
			}
			existingReporterCount.put(reporterId, countReporter);
		}
		this.getMetrics().put(JiraIssueMetric.EXPERIENCE_REPORTER, (int) countReporter);
	}

	/**
	 * Gets the total count of resolved issues of the issue resolver and sets the
	 * experienceResolver criteria of the ReleaseNoteIssueProposal. The existing
	 * Resolver count HashMap is used to avoid duplicated equal JQL queries. The
	 * result may differ, depending of the logged-in user and his permissions.
	 *
	 * @param issue
	 *            of the associated DecisionKnowledgeElement
	 * @param existingResolverCount
	 *            HashMap to save JQL results
	 * @param user
	 *            Application user which makes the request
	 */
	@Override
	public void getAndSetExperienceResolver(Issue issue, HashMap<String, Integer> existingResolverCount,
			ApplicationUser user) {
		// the resolver is most of the times the last assigned user
		JqlQueryBuilder builderResolver = JqlQueryBuilder.newBuilder();
		SearchService searchProvider = ComponentAccessor.getComponentOfType(SearchService.class);

		String assigneeId = issue.getAssigneeId();
		// not all issues have assigneeId, if it is null use the reporterId
		if (assigneeId == null) {
			assigneeId = issue.getReporterId();
			if (assigneeId == null) {
				assigneeId = issue.getReporter().getKey();
			}
		}
		// first check if user was already checked
		Integer resolverExistingCount = existingResolverCount.get(assigneeId);
		int countResolver = 0;

		if (resolverExistingCount != null) {
			countResolver = resolverExistingCount;
		} else {
			builderResolver.where().status("resolved").and().assigneeUser(assigneeId);
			try {
				countResolver = Math.toIntExact(searchProvider.searchCount(user, builderResolver.buildQuery()));
			} catch (SearchException e) {
				e.printStackTrace();
			}
			existingResolverCount.put(assigneeId, countResolver);
		}
		this.getMetrics().put(JiraIssueMetric.EXPERIENCE_RESOLVER, countResolver);

	}

	/**
	 * Count words of a string
	 *
	 * @param input
	 *            of string with space separated words
	 * @return count of words
	 */
	private int countWordsUsingSplit(String input) {
		if (input == null || input.isEmpty()) {
			return 0;
		}

		String[] words = input.split("\\s+");
		return words.length;
	}

}