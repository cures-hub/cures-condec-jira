package ut.mocks;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.codehaus.jackson.JsonNode;

import com.atlassian.jira.action.MockAction;
import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.event.type.EventDispatchOption;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueInputParameters;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.changehistory.metadata.HistoryMetadata;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.issuetype.MockIssueType;
import com.atlassian.jira.mock.issue.MockIssue;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.util.ErrorCollection;
import com.atlassian.jira.workflow.TransitionOptions;

public class MockIssueService implements IssueService {

	@Override
	public IssueResult assign(ApplicationUser arg0, AssignValidationResult arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AsynchronousTaskResult clone(ApplicationUser arg0, CloneValidationResult arg1) {
		// TODO Auto-genreated method stub
		return null;
	}

	@Override
	public IssueResult create(ApplicationUser arg0, CreateValidationResult arg1) {
		IssueResult issueResult = new IssueResult((MutableIssue) arg1.getIssue());
		return issueResult;
	}

	@Override
	public IssueResult create(ApplicationUser arg0, CreateValidationResult arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ErrorCollection delete(ApplicationUser arg0, DeleteValidationResult arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ErrorCollection delete(ApplicationUser arg0, DeleteValidationResult arg1, EventDispatchOption arg2,
			boolean arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IssueResult getIssue(ApplicationUser arg0, Long arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IssueResult getIssue(ApplicationUser arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isEditable(Issue arg0, ApplicationUser arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IssueInputParameters newIssueInputParameters() {
		IssueInputParameters issueInputParameters = new MockIssueInputParameters();
		return issueInputParameters;
	}

	@Override
	public IssueInputParameters newIssueInputParameters(Map<String, String[]> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IssueResult transition(ApplicationUser arg0, TransitionValidationResult arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IssueResult update(ApplicationUser arg0, UpdateValidationResult arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IssueResult update(ApplicationUser arg0, UpdateValidationResult arg1, EventDispatchOption arg2,
			boolean arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AssignValidationResult validateAssign(ApplicationUser arg0, Long arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CloneValidationResult validateClone(ApplicationUser arg0, Issue arg1, String arg2, boolean arg3,
			boolean arg4, boolean arg5, Map<CustomField, Optional<Boolean>> arg6) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CreateValidationResult validateCreate(ApplicationUser arg0, IssueInputParameters arg1) {
		MutableIssue issue = new MockIssue(1, "TEST-12");
		IssueType issueType = new MockIssueType(12, "Solution");
		((MockIssue) issue).setIssueType(issueType);
		ErrorCollection col = new MockAction();
		Map<String,Object> fieldValuesHolder = new HashMap<>();
		Map<String,org.codehaus.jackson.JsonNode> properties = new HashMap<>();
		IssueService.CreateValidationResult ret = new CreateValidationResult(issue, col, fieldValuesHolder, properties);
		return ret;
	}

	@Override
	public DeleteValidationResult validateDelete(ApplicationUser arg0, Long arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CreateValidationResult validateSubTaskCreate(ApplicationUser arg0, Long arg1, IssueInputParameters arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TransitionValidationResult validateTransition(ApplicationUser arg0, Long arg1, int arg2,
			IssueInputParameters arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TransitionValidationResult validateTransition(ApplicationUser arg0, Long arg1, int arg2,
			IssueInputParameters arg3, TransitionOptions arg4) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UpdateValidationResult validateUpdate(ApplicationUser arg0, Long arg1, IssueInputParameters arg2) {
		// TODO Auto-generated method stub
		return null;
	}


}
