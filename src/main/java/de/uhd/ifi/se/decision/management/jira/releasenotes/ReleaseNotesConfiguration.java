package de.uhd.ifi.se.decision.management.jira.releasenotes;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

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
	private TargetGroup targetGroup;
	private transient EnumMap<JiraIssueMetric, Double> jiraIssueMetricWeights;
	private transient EnumMap<AdditionalConfigurationOptions, Boolean> additionalConfiguration;
	private List<String> jiraIssueTypesForImprovements;
	private List<String> jiraIssueTypesForBugFixes;
	private List<String> jiraIssueTypesForNewFeatures;

	public ReleaseNotesConfiguration() {
		this.targetGroup = TargetGroup.getTargetGroup("");
		this.jiraIssueMetricWeights = JiraIssueMetric.toEnumMap();
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
	 * @return targetGroup of the release notes.
	 */
	public TargetGroup getTargetGroup() {
		return targetGroup;
	}

	/**
	 * @param targetGroup
	 *            of the release notes.
	 */
	@JsonProperty
	public void setTargetGroup(TargetGroup targetGroup) {
		this.targetGroup = targetGroup;
	}

	/**
	 * @return weights of {@link JiraIssueMetric}s used for Jira issue rating in
	 *         release notes.
	 */
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
	 * @return map with the additional configuration of the release notes.
	 */
	public EnumMap<AdditionalConfigurationOptions, Boolean> getAdditionalConfiguration() {
		return additionalConfiguration;
	}

	/**
	 * @param additionalConfiguration
	 *            map with the additional configuration of the release notes.
	 */
	@JsonProperty
	public void setAdditionalConfiguration(EnumMap<AdditionalConfigurationOptions, Boolean> additionalConfiguration) {
		this.additionalConfiguration = additionalConfiguration;
	}

	public List<String> getJiraIssueTypesForImprovements() {
		return jiraIssueTypesForImprovements;
	}

	@JsonProperty
	public void setJiraIssueTypesForImprovements(List<String> jiraIssueTypesForImprovements) {
		this.jiraIssueTypesForImprovements = jiraIssueTypesForImprovements;
	}

	public List<String> getJiraIssueTypesForBugFixes() {
		return jiraIssueTypesForBugFixes;
	}

	@JsonProperty
	public void setJiraIssueTypesForBugFixes(List<String> jiraIssueTypesForBugFixes) {
		this.jiraIssueTypesForBugFixes = jiraIssueTypesForBugFixes;
	}

	public List<String> getJiraIssueTypesForNewFeatures() {
		return jiraIssueTypesForNewFeatures;
	}

	@JsonProperty
	public void setJiraIssueTypesForNewFeatures(List<String> jiraIssueTypesForNewFeatures) {
		this.jiraIssueTypesForNewFeatures = jiraIssueTypesForNewFeatures;
	}
}