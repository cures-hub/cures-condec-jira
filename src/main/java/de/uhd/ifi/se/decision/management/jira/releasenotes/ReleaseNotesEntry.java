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
 * Model class for one Jira issue to be included in the {@link ReleaseNotes}. It
 * calculates the {@link JiraIssueMetric}s for the Jira issue and saves the
 * final rating.
 */
public class ReleaseNotesEntry {
	private static final Logger LOGGER = LoggerFactory.getLogger(ReleaseNotesEntry.class);

	private Issue jiraIssue;
	private EnumMap<JiraIssueMetric, Double> jiraIssueMetrics;
	private double rating;

	public ReleaseNotesEntry(Issue jiraIssue, ApplicationUser user) {
		this.jiraIssue = jiraIssue;
		this.jiraIssueMetrics = JiraIssueMetric.toEnumMap();
		jiraIssueMetrics.put(JiraIssueMetric.PRIORITY, getPriority(jiraIssue));
		jiraIssueMetrics.put(JiraIssueMetric.COMMENT_COUNT, countComments(jiraIssue));
		jiraIssueMetrics.put(JiraIssueMetric.SIZE_SUMMARY, getNumberOfWordsInSummary(jiraIssue));
		jiraIssueMetrics.put(JiraIssueMetric.SIZE_DESCRIPTION, getNumberOfWordsInDescription(jiraIssue));
		jiraIssueMetrics.put(JiraIssueMetric.DAYS_COMPLETION, getAndSetDaysToCompletion(jiraIssue));
		jiraIssueMetrics.put(JiraIssueMetric.EXPERIENCE_REPORTER, calculateReporterExperience(jiraIssue, user));
		jiraIssueMetrics.put(JiraIssueMetric.EXPERIENCE_RESOLVER, calculateResolverExperience(jiraIssue, user));
	}

	/**
	 * @return Jira issue to be included in the {@link ReleaseNotes} as a
	 *         {@link KnowledgeElement} object.
	 */
	@XmlElement
	public KnowledgeElement getElement() {
		return new KnowledgeElement(jiraIssue);
	}

	/**
	 * @return Jira issue to be included in the {@link ReleaseNotes}.
	 */
	public Issue getJiraIssue() {
		return jiraIssue;
	}

	/**
	 * @return rating of the Jira issue within the release notes.
	 */
	@XmlElement
	public double getRating() {
		return rating;
	}

	/**
	 * @param rating
	 *            of the Jira issue within the release notes.
	 */
	@JsonProperty
	public void setRating(double rating) {
		this.rating = rating;
	}

	/**
	 * @return map of {@link JiraIssueMetric}s and their respective values to be
	 *         used for prioritization of the Jira issue within the release notes.
	 */
	@XmlElement
	public EnumMap<JiraIssueMetric, Double> getJiraIssueMetrics() {
		return jiraIssueMetrics;
	}

	/**
	 * @param jiraIssue
	 *            to be included in the {@link ReleaseNotes}.
	 * @return priority of the Jira issue. Return 3 as a medium value if the
	 *         priority is not available.
	 */
	public static double getPriority(Issue jiraIssue) {
		Priority priority = jiraIssue.getPriority();
		if (priority != null) {
			double sequence = priority.getSequence();
			return sequence;
		}
		return 3.0;
	}

	/**
	 * @param jiraIssue
	 *            to be included in the {@link ReleaseNotes}.
	 * @return number of comments of the Jira issue.
	 */
	public static double countComments(Issue jiraIssue) {
		CommentManager commentManager = ComponentAccessor.getCommentManager();
		return commentManager.getComments(jiraIssue).size();
	}

	/**
	 * @param jiraIssue
	 *            to be included in the {@link ReleaseNotes}.
	 * @return size of the summary as the number of words.
	 */
	public static double getNumberOfWordsInSummary(Issue jiraIssue) {
		return countWords(jiraIssue.getSummary());
	}

	/**
	 * @param jiraIssue
	 *            to be included in the {@link ReleaseNotes}.
	 * @return size of the description as the number of words.
	 */
	public static double getNumberOfWordsInDescription(Issue jiraIssue) {
		return countWords(jiraIssue.getDescription());
	}

	/**
	 * @param jiraIssue
	 *            to be included in the {@link ReleaseNotes}.
	 * @return number of days until the Jira issue was completed.
	 */
	public static double getAndSetDaysToCompletion(Issue issue) {
		long created = issue.getCreated().getTime();
		long resolved = issue.getResolutionDate().getTime();
		long diff = resolved - created;
		return diff / (1000 * 60 * 60 * 24);
	}

	/**
	 * @param jiraIssue
	 *            to be included in the {@link ReleaseNotes}.
	 * @param user
	 *            authenticated Jira {@link ApplicationUser} who makes the request.
	 * @return total number of created Jira issues by the reporter of this Jira
	 *         issue.
	 */
	public static double calculateReporterExperience(Issue jiraIssue, ApplicationUser user) {
		SearchService searchService = ComponentAccessor.getComponentOfType(SearchService.class);
		double numberOfReportedJiraIssues = 0;
		try {
			Query query = JqlQueryBuilder.newBuilder().where().reporterUser(jiraIssue.getReporterId()).buildQuery();
			numberOfReportedJiraIssues = (int) searchService.searchCount(user, query);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return numberOfReportedJiraIssues;
	}

	/**
	 * @param jiraIssue
	 *            to be included in the {@link ReleaseNotes}.
	 * @param user
	 *            authenticated Jira {@link ApplicationUser} who makes the request.
	 * @return total number of resolved Jira issues by the assignee of this Jira
	 *         issue.
	 */
	public static double calculateResolverExperience(Issue jiraIssue, ApplicationUser user) {
		SearchService searchProvider = ComponentAccessor.getComponentOfType(SearchService.class);

		String assigneeId = jiraIssue.getAssigneeId();
		double numberOfResolvedJiraIssues = 0;
		try {
			Query query = JqlQueryBuilder.newBuilder().where().status("resolved").and().assigneeUser(assigneeId)
					.buildQuery();
			numberOfResolvedJiraIssues = (int) searchProvider.searchCount(user, query);
		} catch (Exception e) {
			LOGGER.debug(e.getMessage());
		}
		return numberOfResolvedJiraIssues;
	}

	/**
	 * @param input
	 *            String with words separated by spaces.
	 * @return number of words in the input string.
	 */
	public static int countWords(String input) {
		if (input == null || input.isEmpty()) {
			return 0;
		}

		String[] words = input.split("\\s+");
		return words.length;
	}
}