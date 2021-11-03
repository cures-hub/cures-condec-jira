package de.uhd.ifi.se.decision.management.jira.releasenotes;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

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
	private List<Integer> bugFixMapping;
	private List<Integer> featureMapping;
	private List<Integer> improvementMapping;
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
	 * @return list with mapped bug fix issues.
	 */
	public List<Integer> getBugFixMapping() {
		return bugFixMapping;
	}

	/**
	 * @param bugFixMapping
	 *            list with mapped bug fix issues of the release notes.
	 */
	@JsonProperty
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
	@JsonProperty
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
	@JsonProperty
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
	@JsonProperty
	public void setAdditionalConfiguration(EnumMap<AdditionalConfigurationOptions, Boolean> additionalConfiguration) {
		this.additionalConfiguration = additionalConfiguration;
	}

	@XmlElement
	public List<String> getJiraIssueTypesForImprovements() {
		return jiraIssueTypesForImprovements;
	}

	@XmlElement
	public List<String> getJiraIssueTypesForBugFixes() {
		return jiraIssueTypesForBugFixes;
	}

	@XmlElement
	public List<String> getJiraIssueTypesForNewFeatures() {
		return jiraIssueTypesForNewFeatures;
	}

	@JsonProperty
	public void setJiraIssueTypesForImprovements(List<String> jiraIssueTypesForImprovements) {
		this.jiraIssueTypesForImprovements = jiraIssueTypesForImprovements;
	}

	@JsonProperty
	public void setJiraIssueTypesForBugFixes(List<String> jiraIssueTypesForBugFixes) {
		this.jiraIssueTypesForBugFixes = jiraIssueTypesForBugFixes;
	}

	@JsonProperty
	public void setJiraIssueTypesForNewFeatures(List<String> jiraIssueTypesForNewFeatures) {
		this.jiraIssueTypesForNewFeatures = jiraIssueTypesForNewFeatures;
	}

}