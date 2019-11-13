package de.uhd.ifi.se.decision.management.jira.releasenotes.impl;

import java.util.EnumMap;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

import de.uhd.ifi.se.decision.management.jira.releasenotes.AdditionalConfigurationOptions;
import de.uhd.ifi.se.decision.management.jira.releasenotes.JiraIssueMetric;
import de.uhd.ifi.se.decision.management.jira.releasenotes.ReleaseNoteConfiguration;
import de.uhd.ifi.se.decision.management.jira.releasenotes.TargetGroup;

/**
 * Model class for release notes configuration
 */
public class ReleaseNoteConfigurationImpl implements ReleaseNoteConfiguration {
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
	public ReleaseNoteConfigurationImpl() {
		this.targetGroup = TargetGroup.getTargetGroup("");
		this.jiraIssueMetric = JiraIssueMetric.toDoubleEnumMap();
	}

	@Override
	public String getTitle() {
		return this.title;
	}

	@Override
	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String getStartDate() {
		return this.startDate;
	}

	@Override
	@JsonProperty("startDate")
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	@Override
	public String getEndDate() {
		return this.endDate;
	}

	@Override
	@JsonProperty("endDate")
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	@Override
	public String getSprintId() {
		return this.sprintId;
	}

	@Override
	@JsonProperty("sprintId")
	public void setSprintId(String sprintId) {
		this.sprintId = sprintId;
	}

	@Override
	public TargetGroup getTargetGroup() {
		return this.targetGroup;
	}

	@Override
	@JsonProperty("targetGroup")
	public void setTargetGroup(TargetGroup targetGroup) {
		this.targetGroup = targetGroup;
	}

	@Override
	public EnumMap<JiraIssueMetric, Double> getJiraIssueMetricWeight() {
		return this.jiraIssueMetric;
	}

	@Override
	@JsonProperty("jiraIssueMetric")
	public void setJiraIssueMetricWeight(EnumMap<JiraIssueMetric, Double> jiraIssueMetricWeight) {
		this.jiraIssueMetric = jiraIssueMetricWeight;
	}

	@Override
	public List<Integer> getBugFixMapping() {
		return this.bugFixMapping;
	}

	@Override
	@JsonProperty("bugFixMapping")
	public void setBugFixMapping(List<Integer> bugFixMapping) {
		this.bugFixMapping = bugFixMapping;
	}

	@Override
	public List<Integer> getFeatureMapping() {
		return this.featureMapping;
	}

	@Override
	@JsonProperty("featureMapping")
	public void setFeatureMapping(List<Integer> featureMapping) {
		this.featureMapping = featureMapping;
	}

	@Override
	public List<Integer> getImprovementMapping() {
		return this.improvementMapping;
	}

	@Override
	@JsonProperty("improvementMapping")
	public void setImprovementMapping(List<Integer> improvementMapping) {
		this.improvementMapping = improvementMapping;
	}

	@Override
	public EnumMap<AdditionalConfigurationOptions, Boolean> getAdditionalConfiguration() {
		return this.additionalConfiguration;
	}

	@Override
	@JsonProperty("additionalConfiguration")
	public void setAdditionalConfiguration(EnumMap<AdditionalConfigurationOptions, Boolean> additionalConfiguration) {
		this.additionalConfiguration = additionalConfiguration;
	}

}