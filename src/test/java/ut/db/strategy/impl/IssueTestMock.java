package ut.db.strategy.impl;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Set;

import org.ofbiz.core.entity.GenericValue;

import com.atlassian.jira.bc.project.component.ProjectComponent;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.attachment.Attachment;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.renderer.IssueRenderContext;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.label.Label;
import com.atlassian.jira.issue.priority.Priority;
import com.atlassian.jira.issue.resolution.Resolution;
import com.atlassian.jira.issue.status.Status;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.version.Version;
import com.atlassian.jira.user.ApplicationUser;

public class IssueTestMock implements Issue{

	private long id;
	private String key;
	private IssueType type; 
	private String description;
	private String summary;
	
	public void setId(long id) {
		this.id = id;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setType(IssueType type) {
		this.type = type;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}
	
	@Override
	public Long getLong(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getString(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Timestamp getTimestamp(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void store() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Collection<Version> getAffectedVersions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ApplicationUser getAssignee() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAssigneeId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ApplicationUser getAssigneeUser() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Attachment> getAttachments() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<ProjectComponent> getComponentObjects() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<ProjectComponent> getComponents() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Timestamp getCreated() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ApplicationUser getCreator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCreatorId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getCustomFieldValue(CustomField arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDescription() {
		return this.description;
	}

	@Override
	public Timestamp getDueDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEnvironment() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getEstimate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getExternalFieldValue(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Version> getFixVersions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GenericValue getGenericValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getId() {
		return this.id;
	}

	@Override
	public IssueRenderContext getIssueRenderContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IssueType getIssueType() {
		return this.type;
	}

	@Override
	public String getIssueTypeId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IssueType getIssueTypeObject() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getKey() {
		return this.key;
	}

	@Override
	public Set<Label> getLabels() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getNumber() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getOriginalEstimate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GenericValue getParent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getParentId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Issue getParentObject() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Priority getPriority() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Priority getPriorityObject() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GenericValue getProject() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getProjectId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Project getProjectObject() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ApplicationUser getReporter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getReporterId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ApplicationUser getReporterUser() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Resolution getResolution() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Timestamp getResolutionDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getResolutionId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Resolution getResolutionObject() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GenericValue getSecurityLevel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getSecurityLevelId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Status getStatus() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getStatusId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Status getStatusObject() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Issue> getSubTaskObjects() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<GenericValue> getSubTasks() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSummary() {
		return this.summary;
	}

	@Override
	public Long getTimeSpent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Timestamp getUpdated() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getVotes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getWatches() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getWorkflowId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isCreated() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEditable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSubTask() {
		// TODO Auto-generated method stub
		return false;
	}
	
	

}
