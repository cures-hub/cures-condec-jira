package de.uhd.ifi.se.decision.management.jira.releasenotes;

import java.util.EnumMap;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Models release notes configuration.
 */
public class ReleaseNotesConfiguration {
	private String title;
	private String startDate;
	private String endDate;
	private String sprintId;
	private TargetGroup targetGroup;
	private EnumMap<JiraIssueMetric, Double> jiraIssueMetric;
	private List<Integer> bugFixMapping;
	private List<Integer> featureMapping;
	private List<Integer> improvementMapping;
	private EnumMap<AdditionalConfigurationOptions, Boolean> additionalConfiguration;

	// This default constructor is necessary for the JSON string to object mapping.
	// Do not delete it!
	public ReleaseNotesConfiguration() {
		this.targetGroup = TargetGroup.getTargetGroup("");
		this.jiraIssueMetric = JiraIssueMetric.toDoubleEnumMap();
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
	@JsonProperty("startDate")
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
	@JsonProperty("endDate")
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
	@JsonProperty("sprintId")
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
	@JsonProperty("targetGroup")
	public void setTargetGroup(TargetGroup targetGroup) {
		this.targetGroup = targetGroup;
	}

	/**
	 * @return weight of JiraIssueMetric of the release notes.
	 */
	public EnumMap<JiraIssueMetric, Double> getJiraIssueMetricWeight() {
		return jiraIssueMetric;
	}

	/**
	 * @param issueMetricWeight
	 *            of the release notes.
	 */
	@JsonProperty("jiraIssueMetric")
	public void setJiraIssueMetricWeight(EnumMap<JiraIssueMetric, Double> jiraIssueMetricWeight) {
		this.jiraIssueMetric = jiraIssueMetricWeight;
	}

	/**
	 * @return list with mapped bug fix issues.
	 */
	public List<Integer> getBugFixMapping() {
		return bugFixMapping;
	}

	/**
	 * @param bugFixMapping
	 *            list with mapped bug fix issues of the release notes.
	 */
	@JsonProperty("bugFixMapping")
	public void setBugFixMapping(List<Integer> bugFixMapping) {
		this.bugFixMapping = bugFixMapping;
	}

	/**
	 * @return list with mapped bug fix issues.
	 */
	public List<Integer> getFeatureMapping() {
		return this.featureMapping;
	}

	/**
	 * @param featureMapping
	 *            list with mapped bug fix issues of the release note.
	 */
	@JsonProperty("featureMapping")
	public void setFeatureMapping(List<Integer> featureMapping) {
		this.featureMapping = featureMapping;
	}

	/**
	 * @return list with mapped bug fix issues.
	 */
	public List<Integer> getImprovementMapping() {
		return this.improvementMapping;
	}

	/**
	 * @param improvementMapping
	 *            list with mapped bug fix issues of the release notes.
	 */
	@JsonProperty("improvementMapping")
	public void setImprovementMapping(List<Integer> improvementMapping) {
		this.improvementMapping = improvementMapping;
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
	@JsonProperty("additionalConfiguration")
	public void setAdditionalConfiguration(EnumMap<AdditionalConfigurationOptions, Boolean> additionalConfiguration) {
		this.additionalConfiguration = additionalConfiguration;
	}

}