package ut.de.uhd.ifi.se.decdoc.jira.mocks;

import java.util.Collection;
import java.util.Map;

import org.codehaus.jackson.JsonNode;

import com.atlassian.jira.issue.IssueInputParameters;
import com.atlassian.jira.issue.changehistory.metadata.HistoryMetadata;

public class MockIssueInputParameters implements IssueInputParameters{

	@Override
	public IssueInputParameters addCustomFieldValue(Long arg0, String... arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IssueInputParameters addCustomFieldValue(String arg0, String... arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addFieldToForcePresent(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IssueInputParameters addProperty(String arg0, JsonNode arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean applyDefaultValuesWhenParameterNotProvided() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Map<String, String[]> getActionParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long[] getAffectedVersionIds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAssigneeId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCommentValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long[] getComponentIds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getCustomFieldValue(Long arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getCustomFieldValue(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDueDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEnvironment() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> getFieldValuesHolder() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long[] getFixVersionIds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFormToken() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HistoryMetadata getHistoryMetadata() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getIssueTypeId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getOriginalEstimate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getOriginalEstimateAsDurationString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPriorityId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getProjectId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<String> getProvidedFields() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getRemainingEstimate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRemainingEstimateAsDurationString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getReporterId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getResolutionDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getResolutionId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getSecurityLevelId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getStatusId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSummary() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getTimeSpent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isFieldPresent(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isFieldSet(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onlyValidatePresentFieldsWhenRetainingExistingValues() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Map<String, JsonNode> properties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean retainExistingValuesWhenParameterNotProvided() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IssueInputParameters setAffectedVersionIds(Long... arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setApplyDefaultValuesWhenParameterNotProvided(boolean arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IssueInputParameters setAssigneeId(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IssueInputParameters setComment(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IssueInputParameters setComment(String arg0, Long arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IssueInputParameters setComment(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IssueInputParameters setComponentIds(Long... arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IssueInputParameters setDescription(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IssueInputParameters setDueDate(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IssueInputParameters setEnvironment(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setFieldValuesHolder(Map<String, Object> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IssueInputParameters setFixVersionIds(Long... arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IssueInputParameters setHistoryMetadata(HistoryMetadata arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IssueInputParameters setIssueTypeId(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IssueInputParameters setOriginalAndRemainingEstimate(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IssueInputParameters setOriginalAndRemainingEstimate(Long arg0, Long arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IssueInputParameters setOriginalEstimate(Long arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IssueInputParameters setOriginalEstimate(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IssueInputParameters setPriorityId(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IssueInputParameters setProjectId(Long arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setProvidedFields(Collection<String> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IssueInputParameters setRemainingEstimate(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IssueInputParameters setRemainingEstimate(Long arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IssueInputParameters setReporterId(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IssueInputParameters setResolutionDate(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IssueInputParameters setResolutionId(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setRetainExistingValuesWhenParameterNotProvided(boolean arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setRetainExistingValuesWhenParameterNotProvided(boolean arg0, boolean arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IssueInputParameters setSecurityLevelId(Long arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSkipLicenceCheck(boolean arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSkipScreenCheck(boolean arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IssueInputParameters setStatusId(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IssueInputParameters setSummary(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IssueInputParameters setTimeSpent(Long arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean skipLicenceCheck() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean skipScreenCheck() {
		// TODO Auto-generated method stub
		return false;
	}

}
