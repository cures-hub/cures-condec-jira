package de.uhd.ifi.se.decision.management.jira.releasenotes;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import com.atlassian.jira.issue.Issue;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;

/**
 * Contains the configuration details for the release notes creation for one
 * Jira project (see {@link DecisionKnowledgeProject}).
 */
public class ReleaseNotesConfiguration {

	private String title;
	private String startDate;
	private String endDate;
	private String sprintId;
	private List<String> jiraIssueTypesForImprovements;
	private List<String> jiraIssueTypesForBugFixes;
	private List<String> jiraIssueTypesForNewFeatures;
	private transient EnumMap<JiraIssueMetric, Double> jiraIssueMetricWeights;

	@JsonCreator
	public ReleaseNotesConfiguration() {
		title = "";
		jiraIssueMetricWeights = JiraIssueMetric.toEnumMap();
		jiraIssueTypesForImprovements = new ArrayList<>();
		jiraIssueTypesForBugFixes = new ArrayList<>();
		jiraIssueTypesForNewFeatures = new ArrayList<>();
	}

	/**
	 * @return title of the release notes.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            of the release notes.
	 */
	@JsonProperty
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return startDate of the release notes.
	 */
	public String getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate
	 *            of the release notes.
	 */
	@JsonProperty
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return end date of the release notes.
	 */
	public String getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate
	 *            of the release notes.
	 */
	@JsonProperty
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return sprint id of the release notes.
	 */
	public String getSprintId() {
		return sprintId;
	}

	/**
	 * @param sprintId
	 *            of the release notes.
	 */
	@JsonProperty
	public void setSprintId(String sprintId) {
		this.sprintId = sprintId;
	}

	/**
	 * @return weights of {@link JiraIssueMetric}s used for Jira issue rating in
	 *         release notes.
	 */
	@XmlElement
	public EnumMap<JiraIssueMetric, Double> getJiraIssueMetricWeights() {
		return jiraIssueMetricWeights;
	}

	/**
	 * @param issueMetricWeight
	 *            of the release notes.
	 */
	@JsonProperty
	public void setJiraIssueMetricWeights(EnumMap<JiraIssueMetric, Double> jiraIssueMetricWeights) {
		this.jiraIssueMetricWeights = jiraIssueMetricWeights;
	}

	/**
	 * @return jira issue types for the {@link ReleaseNotesCategory#IMPROVEMENTS}.
	 */
	@XmlElement
	public List<String> getJiraIssueTypesForImprovements() {
		return jiraIssueTypesForImprovements;
	}

	/**
	 * @param jiraIssueTypesForImprovements
	 *            jira issue types for the
	 *            {@link ReleaseNotesCategory#IMPROVEMENTS}.
	 */
	@JsonProperty
	public void setJiraIssueTypesForImprovements(List<String> jiraIssueTypesForImprovements) {
		this.jiraIssueTypesForImprovements = jiraIssueTypesForImprovements;
	}

	/**
	 * @return jira issue types for the {@link ReleaseNotesCategory#BUG_FIXES}.
	 */
	@XmlElement
	public List<String> getJiraIssueTypesForBugFixes() {
		return jiraIssueTypesForBugFixes;
	}

	/**
	 * @param jiraIssueTypesForBugFixes
	 *            jira issue types for the {@link ReleaseNotesCategory#BUG_FIXES}.
	 */
	@JsonProperty
	public void setJiraIssueTypesForBugFixes(List<String> jiraIssueTypesForBugFixes) {
		this.jiraIssueTypesForBugFixes = jiraIssueTypesForBugFixes;
	}

	/**
	 * @return jira issue types for the {@link ReleaseNotesCategory#NEW_FEATURES}.
	 */
	@XmlElement
	public List<String> getJiraIssueTypesForNewFeatures() {
		return jiraIssueTypesForNewFeatures;
	}

	/**
	 * @param jiraIssueTypesForNewFeatures
	 *            jira issue types for the
	 *            {@link ReleaseNotesCategory#NEW_FEATURES}.
	 */
	@JsonProperty
	public void setJiraIssueTypesForNewFeatures(List<String> jiraIssueTypesForNewFeatures) {
		this.jiraIssueTypesForNewFeatures = jiraIssueTypesForNewFeatures;
	}

	public ReleaseNotesCategory decideCategory(Issue jiraIssue) {
		return decideCategory(jiraIssue.getIssueType().getName());
	}

	public ReleaseNotesCategory decideCategory(String type) {
		if (jiraIssueTypesForNewFeatures.contains(type)) {
			return ReleaseNotesCategory.NEW_FEATURES;
		}
		if (jiraIssueTypesForBugFixes.contains(type)) {
			return ReleaseNotesCategory.BUG_FIXES;
		}
		if (jiraIssueTypesForImprovements.contains(type)) {
			return ReleaseNotesCategory.IMPROVEMENTS;
		}
		return null;
	}
}