package de.uhd.ifi.se.decision.management.jira.mocks;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.ofbiz.core.entity.GenericEntityException;
import org.ofbiz.core.entity.GenericValue;

import com.atlassian.crowd.embedded.api.Group;
import com.atlassian.jira.exception.DataAccessException;
import com.atlassian.jira.exception.RemoveException;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.permission.PermissionContext;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.scheme.Scheme;
import com.atlassian.jira.scheme.SchemeEntity;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.workflow.AssignableWorkflowScheme;
import com.atlassian.jira.workflow.AssignableWorkflowScheme.Builder;
import com.atlassian.jira.workflow.DraftWorkflowScheme;
import com.atlassian.jira.workflow.JiraWorkflow;
import com.atlassian.jira.workflow.WorkflowException;
import com.atlassian.jira.workflow.WorkflowScheme;
import com.atlassian.jira.workflow.WorkflowSchemeManager;

public class MockWorkflowSchemeManager implements WorkflowSchemeManager {

	@Override
	public void addDefaultSchemeToProject(GenericValue arg0) throws GenericEntityException {
		// TODO Auto-generated method stub

	}

	@Override
	public void addDefaultSchemeToProject(Project arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addSchemeToProject(GenericValue arg0, GenericValue arg1) throws GenericEntityException {
		// TODO Auto-generated method stub

	}

	@Override
	public void addSchemeToProject(Project arg0, Scheme arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public GenericValue copyScheme(GenericValue arg0) throws GenericEntityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Scheme copyScheme(Scheme arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GenericValue createDefaultScheme() throws GenericEntityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GenericValue createScheme(String arg0, String arg1) throws GenericEntityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Scheme createSchemeAndEntities(Scheme arg0) throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GenericValue createSchemeEntity(GenericValue arg0, SchemeEntity arg1) throws GenericEntityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Scheme createSchemeObject(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteEntities(Iterable<Long> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteEntity(Long arg0) throws GenericEntityException {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteScheme(Long arg0) throws GenericEntityException {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Scheme> getAssociatedSchemes(boolean arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GenericValue getDefaultScheme() throws GenericEntityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Scheme getDefaultSchemeObject() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<GenericValue> getEntities(String arg0, String arg1) throws GenericEntityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<GenericValue> getEntitiesByIds(List<Long> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GenericValue getEntity(Long arg0) throws GenericEntityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Group> getGroups(Long arg0, GenericValue arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Group> getGroups(Long arg0, Project arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Project> getProjects(Scheme arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GenericValue getScheme(Long arg0) throws GenericEntityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GenericValue getScheme(String arg0) throws GenericEntityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Scheme getSchemeFor(Project arg0) {
		return new Scheme();
	}

	@Override
	public Long getSchemeIdFor(Project arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Scheme getSchemeObject(Long arg0) throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Scheme getSchemeObject(String arg0) throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Scheme> getSchemeObjects() throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<GenericValue> getSchemes() throws GenericEntityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<GenericValue> getSchemes(GenericValue arg0) throws GenericEntityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Scheme> getUnassociatedSchemes() throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<ApplicationUser> getUsers(Long arg0, Project arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<ApplicationUser> getUsers(Long arg0, Issue arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<ApplicationUser> getUsers(Long arg0, GenericValue arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<ApplicationUser> getUsers(Long arg0, PermissionContext arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean removeEntities(GenericValue arg0, Long arg1) throws RemoveException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeEntities(String arg0, String arg1) throws RemoveException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeSchemesFromProject(GenericValue arg0) throws GenericEntityException {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeSchemesFromProject(Project arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean schemeExists(String arg0) throws GenericEntityException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void swapParameterForEntitiesOfType(String arg0, String arg1, String arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateScheme(GenericValue arg0) throws GenericEntityException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateScheme(Scheme arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addWorkflowToScheme(GenericValue arg0, String arg1, String arg2) throws GenericEntityException {
		// TODO Auto-generated method stub

	}

	@Override
	public Builder assignableBuilder() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AssignableWorkflowScheme cleanUpSchemeDraft(Project arg0, ApplicationUser arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clearWorkflowCache() {
		// TODO Auto-generated method stub

	}

	@Override
	public AssignableWorkflowScheme copyDraft(DraftWorkflowScheme arg0, ApplicationUser arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DraftWorkflowScheme createDraft(ApplicationUser arg0, DraftWorkflowScheme arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DraftWorkflowScheme createDraftOf(ApplicationUser arg0, AssignableWorkflowScheme arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AssignableWorkflowScheme createScheme(AssignableWorkflowScheme arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean deleteWorkflowScheme(WorkflowScheme arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public com.atlassian.jira.workflow.DraftWorkflowScheme.Builder draftBuilder(AssignableWorkflowScheme arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<String> getActiveWorkflowNames() throws GenericEntityException, WorkflowException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<AssignableWorkflowScheme> getAssignableSchemes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAssociationType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GenericValue getDefaultEntity(GenericValue arg0) throws GenericEntityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AssignableWorkflowScheme getDefaultWorkflowScheme() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DraftWorkflowScheme getDraft(long arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DraftWorkflowScheme getDraftForParent(AssignableWorkflowScheme arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<GenericValue> getEntities(GenericValue arg0) throws GenericEntityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<GenericValue> getEntities(GenericValue arg0, Long arg1) throws GenericEntityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<GenericValue> getEntities(GenericValue arg0, String arg1) throws GenericEntityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<GenericValue> getEntities(GenericValue arg0, Long arg1, String arg2) throws GenericEntityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<GenericValue> getEntities(GenericValue arg0, String arg1, Long arg2) throws GenericEntityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEntityName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<GenericValue> getNonDefaultEntities(GenericValue arg0) throws GenericEntityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AssignableWorkflowScheme getParentForDraft(long arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Project> getProjectsUsing(AssignableWorkflowScheme arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSchemeDesc() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSchemeEntityName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<GenericValue> getSchemesForWorkflow(JiraWorkflow arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<WorkflowScheme> getSchemesForWorkflowIncludingDrafts(JiraWorkflow arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String> getWorkflowMap(Project arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getWorkflowName(Project arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getWorkflowName(GenericValue arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GenericValue getWorkflowScheme(GenericValue arg0) throws GenericEntityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GenericValue getWorkflowScheme(Project arg0) throws GenericEntityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AssignableWorkflowScheme getWorkflowSchemeObj(long arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AssignableWorkflowScheme getWorkflowSchemeObj(String arg0) {
		return new AssignableWorkflowScheme() {

			@Override
			public boolean isDraft() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean isDefault() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public Map<String, String> getMappings() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Long getId() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getConfiguredWorkflow(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getConfiguredDefaultWorkflow() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getActualWorkflow(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getActualDefaultWorkflow() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getDescription() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Builder builder() {
				return new AssignableWorkflowScheme.Builder() {

					@Override
					public Builder setMappings(Map<String, String> arg0) {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public Builder setMapping(String arg0, String arg1) {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public Builder setDefaultWorkflow(String arg0) {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public Builder removeWorkflow(String arg0) {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public Builder removeMapping(String arg0) {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public Builder removeDefault() {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public boolean isDraft() {
						// TODO Auto-generated method stub
						return false;
					}

					@Override
					public boolean isDefault() {
						// TODO Auto-generated method stub
						return false;
					}

					@Override
					public String getName() {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public Map<String, String> getMappings() {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public String getMapping(String arg0) {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public Long getId() {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public String getDescription() {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public String getDefaultWorkflow() {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public Builder clearMappings() {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public Builder setName(String arg0) {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public Builder setDescription(String arg0) {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public AssignableWorkflowScheme build() {
						return null;
					}
				};
			}
		};
	}

	@Override
	public AssignableWorkflowScheme getWorkflowSchemeObj(Project arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasDraft(AssignableWorkflowScheme arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isActive(WorkflowScheme arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isUsingDefaultScheme(Project arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void replaceSchemeWithDraft(DraftWorkflowScheme arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public DraftWorkflowScheme updateDraftWorkflowScheme(ApplicationUser arg0, DraftWorkflowScheme arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateSchemesForRenamedWorkflow(String arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public AssignableWorkflowScheme updateWorkflowScheme(AssignableWorkflowScheme arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T waitForUpdatesToFinishAndExecute(AssignableWorkflowScheme arg0, Callable<T> arg1) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
