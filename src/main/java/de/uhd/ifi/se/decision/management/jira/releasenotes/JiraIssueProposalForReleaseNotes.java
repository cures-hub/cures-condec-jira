package de.uhd.ifi.se.decision.management.jira.releasenotes;

import java.util.EnumMap;

import javax.xml.bind.annotation.XmlElement;

import org.codehaus.jackson.annotate.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.comments.CommentManager;
import com.atlassian.jira.issue.priority.Priority;
import com.atlassian.jira.jql.builder.JqlQueryBuilder;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.query.Query;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;

/**
 * Model class for the release notes Jira issue proposal. It saves the knowledge
 * element, the final rating and the issue metrics.
 */
public class JiraIssueProposalForReleaseNotes {
	private static final Logger LOGGER = LoggerFactory.getLogger(JiraIssueProposalForReleaseNotes.class);

	private Issue jiraIssue;
	private EnumMap<JiraIssueMetric, Integer> jiraIssueMetrics;
	private double rating;

	public JiraIssueProposalForReleaseNotes(Issue jiraIssue) {
		this.jiraIssue = jiraIssue;
		this.jiraIssueMetrics = JiraIssueMetric.toIntegerEnumMap();
		this.jiraIssueMetrics.put(JiraIssueMetric.COUNT_DECISION_KNOWLEDGE, 0);
	}

	/**
	 * @return decisionKnowledgeElement of the ReleaseNoteIssueProposal.
	 */
	@XmlElement(name = "decisionKnowledgeElement")
	public KnowledgeElement getDecisionKnowledgeElement() {
		return new KnowledgeElement(jiraIssue);
	}

	/**
	 * @param decisionKnowledgeElement
	 *            of the ReleaseNoteIssueProposal.
	 */
	@JsonProperty("decisionKnowledgeElement")
	public void setDecisionKnowledgeElement(Issue decisionKnowledgeElement) {
		this.jiraIssue = decisionKnowledgeElement;
	}

	/**
	 * @return rating of the ReleaseNoteIssueProposal.
	 */
	@XmlElement(name = "rating")
	public double getRating() {
		return this.rating;
	}

	/**
	 * @param rating
	 *            of the ReleaseNoteIssueProposal.
	 */
	@JsonProperty("rating")
	public void setRating(double rating) {
		this.rating = rating;
	}

	/**
	 * @return jiraIssueMetrics (criteria for prioritisation) of the
	 *         ReleaseNoteIssueProposal.
	 */
	@XmlElement(name = "jiraIssueMetrics")
	public EnumMap<JiraIssueMetric, Integer> getMetrics() {
		return this.jiraIssueMetrics;
	}

	/**
	 * @param jiraIssueMetrics
	 *            of the ReleaseNoteIssueProposal.
	 */
	public void setMetrics(EnumMap<JiraIssueMetric, Integer> jiraIssueMetrics) {
		this.jiraIssueMetrics = jiraIssueMetrics;
	}

	/**
	 * Gets the priority of the Jira issue and sets the priority criteria of the
	 * ReleaseNoteIssueProposal.
	 *
	 * @param issue
	 *            of the associated DecisionKnowledgeElement.
	 */
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
	public void getAndSetCountOfComments(Issue issue) {
		CommentManager commentManager = ComponentAccessor.getCommentManager();
		int countComments = commentManager.getComments(issue).size();
		this.getMetrics().put(JiraIssueMetric.COUNT_COMMENTS, countComments);
	}

	/**
	 * Gets the size of the summary and sets the size summary criteria of the
	 * ReleaseNoteIssueProposal
	 */
	public void getAndSetSizeOfSummary() {
		int sizeSummary = countWordsUsingSplit(this.getDecisionKnowledgeElement().getSummary());
		this.getMetrics().put(JiraIssueMetric.SIZE_SUMMARY, sizeSummary);
	}

	/**
	 * Gets the size of the description and sets the size description criteria of
	 * the ReleaseNoteIssueProposal
	 */
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
	public void getAndSetDaysToCompletion(Issue issue) {
		Long created = issue.getCreated().getTime();
		Long resolved = issue.getResolutionDate().getTime();
		Long diff = resolved - created;
		int days = (int) Math.floor(diff / (1000 * 60 * 60 * 24));
		this.getMetrics().put(JiraIssueMetric.DAYS_COMPLETION, days);
	}

	/**
	 * Gets the total count of created issues of the issue reporter and sets the
	 * experienceReporter criteria of the ReleaseNoteIssueProposal.
	 *
	 * @param jiraIssue
	 *            of the associated DecisionKnowledgeElement
	 * @param existingReporterCount
	 *            HashMap to save JQL results
	 * @param user
	 *            Application user which makes the request
	 */
	public void calculateReporterExperience(Issue jiraIssue, ApplicationUser user) {
		SearchService searchService = ComponentAccessor.getComponentOfType(SearchService.class);
		int countReporter = 0;
		try {
			Query query = JqlQueryBuilder.newBuilder().where().reporterUser(jiraIssue.getReporterId()).buildQuery();
			countReporter = (int) searchService.searchCount(user, query);
		} catch (Exception e) {
			LOGGER.debug(e.getMessage());
		}
		getMetrics().put(JiraIssueMetric.EXPERIENCE_REPORTER, countReporter);
	}

	/**
	 * Gets the total count of resolved issues of the issue resolver and sets the
	 * experienceResolver criteria of the ReleaseNoteIssueProposal.
	 *
	 * @param jiraIssue
	 *            of the associated DecisionKnowledgeElement
	 * @param existingResolverCount
	 *            HashMap to save JQL results
	 * @param user
	 *            Application user which makes the request
	 */
	public void calculateResolverExperience(Issue jiraIssue, ApplicationUser user) {
		SearchService searchProvider = ComponentAccessor.getComponentOfType(SearchService.class);

		String assigneeId = jiraIssue.getAssigneeId();
		int countResolver = 0;
		try {
			Query query = JqlQueryBuilder.newBuilder().where().status("resolved").and().assigneeUser(assigneeId)
					.buildQuery();
			countResolver = (int) searchProvider.searchCount(user, query);
		} catch (Exception e) {
			LOGGER.debug(e.getMessage());
		}
		getMetrics().put(JiraIssueMetric.EXPERIENCE_RESOLVER, countResolver);
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