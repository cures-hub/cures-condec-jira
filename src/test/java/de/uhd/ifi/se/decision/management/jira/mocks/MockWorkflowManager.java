package de.uhd.ifi.se.decision.management.jira.mocks;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.ofbiz.core.entity.GenericValue;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.status.Status;
import com.atlassian.jira.scheme.Scheme;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.workflow.JiraWorkflow;
import com.atlassian.jira.workflow.MockJiraWorkflow;
import com.atlassian.jira.workflow.WorkflowException;
import com.atlassian.jira.workflow.WorkflowManager;
import com.atlassian.jira.workflow.WorkflowProgressAware;
import com.atlassian.jira.workflow.WorkflowScheme;
import com.opensymphony.workflow.StoreException;
import com.opensymphony.workflow.Workflow;
import com.opensymphony.workflow.loader.ActionDescriptor;
import com.opensymphony.workflow.loader.FunctionDescriptor;
import com.opensymphony.workflow.spi.WorkflowStore;

public class MockWorkflowManager implements WorkflowManager {

	@Override
	public void copyAndDeleteDraftWorkflows(ApplicationUser arg0, Set<JiraWorkflow> arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void copyAndDeleteDraftsForInactiveWorkflowsIn(ApplicationUser arg0, Iterable<JiraWorkflow> arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public JiraWorkflow copyWorkflow(String arg0, String arg1, String arg2, JiraWorkflow arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JiraWorkflow copyWorkflow(ApplicationUser arg0, String arg1, String arg2, JiraWorkflow arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JiraWorkflow createDraftWorkflow(String arg0, String arg1)
			throws IllegalStateException, IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JiraWorkflow createDraftWorkflow(ApplicationUser arg0, String arg1)
			throws IllegalStateException, IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GenericValue createIssue(String arg0, Map<String, Object> arg1) throws WorkflowException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void createWorkflow(String arg0, JiraWorkflow arg1) throws WorkflowException {
		// TODO Auto-generated method stub

	}

	@Override
	public void createWorkflow(ApplicationUser arg0, JiraWorkflow arg1) throws WorkflowException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean deleteDraftWorkflow(String arg0) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void deleteWorkflow(JiraWorkflow arg0) throws WorkflowException {
		// TODO Auto-generated method stub

	}

	@Override
	public void doWorkflowAction(WorkflowProgressAware arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public ActionDescriptor getActionDescriptor(WorkflowProgressAware arg0) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ActionDescriptor getActionDescriptor(Issue arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<JiraWorkflow> getActiveWorkflows() throws WorkflowException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JiraWorkflow getDefaultWorkflow() throws WorkflowException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JiraWorkflow getDraftWorkflow(String arg0) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<String> getInitialStatusIdForIssue(Issue arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNextStatusIdForAction(Issue arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<ActionDescriptor, Collection<FunctionDescriptor>> getPostFunctionsForWorkflow(JiraWorkflow arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getStepId(long arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WorkflowStore getStore() throws StoreException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JiraWorkflow getWorkflow(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JiraWorkflow getWorkflow(GenericValue arg0) throws WorkflowException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JiraWorkflow getWorkflow(Issue arg0) throws WorkflowException {
		MockJiraWorkflow workflow = new MockJiraWorkflow();
		workflow.addStep(1, "Unresolved");
		return workflow;
	}

	@Override
	public JiraWorkflow getWorkflow(Long arg0, String arg1) throws WorkflowException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JiraWorkflow getWorkflowClone(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JiraWorkflow getWorkflowFromScheme(GenericValue arg0, String arg1) throws WorkflowException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JiraWorkflow getWorkflowFromScheme(WorkflowScheme arg0, String arg1) throws WorkflowException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<JiraWorkflow> getWorkflows() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<JiraWorkflow> getWorkflowsFromScheme(GenericValue arg0) throws WorkflowException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<JiraWorkflow> getWorkflowsFromScheme(Scheme arg0) throws WorkflowException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<JiraWorkflow> getWorkflowsIncludingDrafts() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isActive(JiraWorkflow arg0) throws WorkflowException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEditable(Issue arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEditable(Issue arg0, String arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSystemWorkflow(JiraWorkflow arg0) throws WorkflowException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Workflow makeWorkflow(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Workflow makeWorkflow(ApplicationUser arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Workflow makeWorkflowWithUserKey(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Workflow makeWorkflowWithUserName(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void migrateIssueToWorkflow(MutableIssue arg0, JiraWorkflow arg1, Status arg2) throws WorkflowException {
		// TODO Auto-generated method stub

	}

	@Override
	public void migrateIssueToWorkflow(GenericValue arg0, JiraWorkflow arg1, GenericValue arg2)
			throws WorkflowException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean migrateIssueToWorkflowNoReindex(GenericValue arg0, JiraWorkflow arg1, GenericValue arg2)
			throws WorkflowException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void overwriteActiveWorkflow(String arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void overwriteActiveWorkflow(ApplicationUser arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeWorkflowEntries(GenericValue arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void replaceConditionInTransition(ActionDescriptor arg0, Map<String, String> arg1,
			Map<String, String> arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void saveWorkflowWithoutAudit(JiraWorkflow arg0) throws WorkflowException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateWorkflow(String arg0, JiraWorkflow arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateWorkflow(ApplicationUser arg0, JiraWorkflow arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateWorkflowNameAndDescription(String arg0, JiraWorkflow arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateWorkflowNameAndDescription(ApplicationUser arg0, JiraWorkflow arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean workflowExists(String arg0) throws WorkflowException {
		// TODO Auto-generated method stub
		return false;
	}

}
