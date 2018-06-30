package de.uhd.ifi.se.decision.documentation.jira.mocks;

import java.util.Collection;
import java.util.Map;

import org.codehaus.jackson.JsonNode;

import com.atlassian.jira.issue.IssueInputParameters;
import com.atlassian.jira.issue.changehistory.metadata.HistoryMetadata;

public class MockIssueInputParameters implements IssueInputParameters {

	@Override
	public IssueInputParameters addCustomFieldValue(Long arg0, String... arg1) {
		return null;
	}

	@Override
	public IssueInputParameters addCustomFieldValue(String arg0, String... arg1) {
		return null;
	}

	@Override
	public void addFieldToForcePresent(String arg0) {
		// method empty since not used for testing
	}

	@Override
	public IssueInputParameters addProperty(String arg0, JsonNode arg1) {
		return null;
	}

	@Override
	public boolean applyDefaultValuesWhenParameterNotProvided() {
		return false;
	}

	@Override
	public Map<String, String[]> getActionParameters() {
		return null;
	}

	@Override
	public Long[] getAffectedVersionIds() {
		return new Long[0];
	}

	@Override
	public String getAssigneeId() {
		return null;
	}

	@Override
	public String getCommentValue() {
		return null;
	}

	@Override
	public Long[] getComponentIds() {
		return new Long[0];
	}

	@Override
	public String[] getCustomFieldValue(Long arg0) {
		return new String[0];
	}

	@Override
	public String[] getCustomFieldValue(String arg0) {
		return new String[0];
	}

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public String getDueDate() {
		return null;
	}

	@Override
	public String getEnvironment() {
		return null;
	}

	@Override
	public Map<String, Object> getFieldValuesHolder() {
		return null;
	}

	@Override
	public Long[] getFixVersionIds() {
		return new Long[0];
	}

	@Override
	public String getFormToken() {
		return null;
	}

	@Override
	public HistoryMetadata getHistoryMetadata() {
		return null;
	}

	@Override
	public String getIssueTypeId() {
		return null;
	}

	@Override
	public Long getOriginalEstimate() {
		return null;
	}

	@Override
	public String getOriginalEstimateAsDurationString() {
		return null;
	}

	@Override
	public String getPriorityId() {
		return null;
	}

	@Override
	public Long getProjectId() {
		return null;
	}

	@Override
	public Collection<String> getProvidedFields() {
		return null;
	}

	@Override
	public Long getRemainingEstimate() {
		return null;
	}

	@Override
	public String getRemainingEstimateAsDurationString() {
		return null;
	}

	@Override
	public String getReporterId() {
		return null;
	}

	@Override
	public String getResolutionDate() {
		return null;
	}

	@Override
	public String getResolutionId() {
		return null;
	}

	@Override
	public Long getSecurityLevelId() {
		return null;
	}

	@Override
	public String getStatusId() {
		return null;
	}

	@Override
	public String getSummary() {
		return null;
	}

	@Override
	public Long getTimeSpent() {
		return null;
	}

	@Override
	public boolean isFieldPresent(String arg0) {
		return false;
	}

	@Override
	public boolean isFieldSet(String arg0) {
		return false;
	}

	@Override
	public boolean onlyValidatePresentFieldsWhenRetainingExistingValues() {
		return false;
	}

	@Override
	public Map<String, JsonNode> properties() {
		return null;
	}

	@Override
	public boolean retainExistingValuesWhenParameterNotProvided() {
		return false;
	}

	@Override
	public IssueInputParameters setAffectedVersionIds(Long... arg0) {
		return null;
	}

	@Override
	public void setApplyDefaultValuesWhenParameterNotProvided(boolean arg0) {
		// method empty since not used for testing
	}

	@Override
	public IssueInputParameters setAssigneeId(String arg0) {
		return null;
	}

	@Override
	public IssueInputParameters setComment(String arg0) {
		return null;
	}

	@Override
	public IssueInputParameters setComment(String arg0, Long arg1) {
		return null;
	}

	@Override
	public IssueInputParameters setComment(String arg0, String arg1) {
		return null;
	}

	@Override
	public IssueInputParameters setComponentIds(Long... arg0) {
		return null;
	}

	@Override
	public IssueInputParameters setDescription(String arg0) {
		return null;
	}

	@Override
	public IssueInputParameters setDueDate(String arg0) {
		return null;
	}

	@Override
	public IssueInputParameters setEnvironment(String arg0) {
		return null;
	}

	@Override
	public void setFieldValuesHolder(Map<String, Object> arg0) {
		// method empty since not used for testing
	}

	@Override
	public IssueInputParameters setFixVersionIds(Long... arg0) {
		return null;
	}

	@Override
	public IssueInputParameters setHistoryMetadata(HistoryMetadata arg0) {
		return null;
	}

	@Override
	public IssueInputParameters setIssueTypeId(String arg0) {
		return null;
	}

	@Override
	public IssueInputParameters setOriginalAndRemainingEstimate(String arg0, String arg1) {
		return null;
	}

	@Override
	public IssueInputParameters setOriginalAndRemainingEstimate(Long arg0, Long arg1) {
		return null;
	}

	@Override
	public IssueInputParameters setOriginalEstimate(Long arg0) {
		return null;
	}

	@Override
	public IssueInputParameters setOriginalEstimate(String arg0) {
		return null;
	}

	@Override
	public IssueInputParameters setPriorityId(String arg0) {
		return null;
	}

	@Override
	public IssueInputParameters setProjectId(Long arg0) {
		return null;
	}

	@Override
	public void setProvidedFields(Collection<String> arg0) {
		// method empty since not used for testing
	}

	@Override
	public IssueInputParameters setRemainingEstimate(String arg0) {
		return null;
	}

	@Override
	public IssueInputParameters setRemainingEstimate(Long arg0) {
		return null;
	}

	@Override
	public IssueInputParameters setReporterId(String arg0) {
		return null;
	}

	@Override
	public IssueInputParameters setResolutionDate(String arg0) {
		return null;
	}

	@Override
	public IssueInputParameters setResolutionId(String arg0) {
		return null;
	}

	@Override
	public void setRetainExistingValuesWhenParameterNotProvided(boolean arg0) {
		// method empty since not used for testing
	}

	@Override
	public void setRetainExistingValuesWhenParameterNotProvided(boolean arg0, boolean arg1) {
		// method empty since not used for testing
	}

	@Override
	public IssueInputParameters setSecurityLevelId(Long arg0) {
		return null;
	}

	@Override
	public void setSkipLicenceCheck(boolean arg0) {
		// method empty since not used for testing
	}

	@Override
	public void setSkipScreenCheck(boolean arg0) {
		// method empty since not used for testing
	}

	@Override
	public IssueInputParameters setStatusId(String arg0) {
		return null;
	}

	@Override
	public IssueInputParameters setSummary(String arg0) {
		return null;
	}

	@Override
	public IssueInputParameters setTimeSpent(Long arg0) {
		return null;
	}

	@Override
	public boolean skipLicenceCheck() {
		return false;
	}

	@Override
	public boolean skipScreenCheck() {
		return false;
	}

}
