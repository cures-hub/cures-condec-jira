package de.uhd.ifi.se.decision.management.jira.releasenotes.impl;


import de.uhd.ifi.se.decision.management.jira.releasenotes.ReleaseNoteConfiguration;
import de.uhd.ifi.se.decision.management.jira.releasenotes.TargetGroup;
import de.uhd.ifi.se.decision.management.jira.releasenotes.TaskCriteriaPrioritisation;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.EnumMap;
import java.util.List;

/**
 * Model class for Release Notes Configuration
 */
public class ReleaseNoteConfigurationImpl implements ReleaseNoteConfiguration {
	private String startDate;
	private String endDate;
	private String sprintId;
	private TargetGroup targetGroup;
	private EnumMap<TaskCriteriaPrioritisation, Double> taskCriteriaPrioritisation;
	private List<Integer> bugFixMapping;
	private List<Integer> featureMapping;
	private List<Integer> improvementMapping;


	// This default constructor is necessary for the JSON string to object mapping.
	// Do not delete it!
	public ReleaseNoteConfigurationImpl() {
		this.targetGroup = TargetGroup.getTargetGroup("");
		this.taskCriteriaPrioritisation = TaskCriteriaPrioritisation.toDoubleEnumMap();
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
	public EnumMap<TaskCriteriaPrioritisation, Double> getTaskCriteriaPrioritisation() {
		return this.taskCriteriaPrioritisation;
	}

	@Override
	public void setTaskCriteriaPrioritisation(EnumMap<TaskCriteriaPrioritisation, Double> taskCriteriaPrioritisation) {
		this.taskCriteriaPrioritisation = taskCriteriaPrioritisation;
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

}