package de.uhd.ifi.se.decision.management.jira.mocks;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.atlassian.jira.action.MockAction;
import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.event.type.EventDispatchOption;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueInputParameters;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.issuetype.MockIssueType;
import com.atlassian.jira.mock.issue.MockIssue;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.util.ErrorCollection;
import com.atlassian.jira.workflow.TransitionOptions;

import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

/**
 * @issue What is the difference between the IssueManager and the IssueService?
 *        Do we really need both?
 * @decision we need both
 * @pro the issueService only can create new thinks. the manger only manages
 *      existing elements and can't change them.
 */
public class MockIssueService implements IssueService {

	@Override
	public IssueResult assign(ApplicationUser user, AssignValidationResult arg1) {
		return null;
	}

	@Override
	public AsynchronousTaskResult clone(ApplicationUser user, CloneValidationResult arg1) {
		return null;
	}

	@Override
	public IssueResult create(ApplicationUser user, CreateValidationResult arg1) {
		return new IssueResult(arg1.getIssue());
	}

	@Override
	public IssueResult create(ApplicationUser user, CreateValidationResult result, String arg2) {
		return null;
	}

	@Override
	public ErrorCollection delete(ApplicationUser user, DeleteValidationResult result) {
		if (user == null || user == JiraUsers.BLACK_HEAD.getApplicationUser()) {
			ErrorCollection errors = new MockAction();
			errors.addError("user", "User is not authorized.");
			return errors;
		}
		return result.getErrorCollection();
	}

	@Override
	public ErrorCollection delete(ApplicationUser user, DeleteValidationResult arg1, EventDispatchOption arg2,
			boolean arg3) {
		return null;
	}

	@Override
	public IssueResult getIssue(ApplicationUser user, Long issueId) {
		Issue issue = JiraIssues.getTestJiraIssues().stream()
				.filter(jiraIssue -> issueId.longValue() == jiraIssue.getId()).findFirst()
				.orElse(new MockIssue(issueId));
		IssueResult result = new IssueResult((MutableIssue) issue);
		if (user == null || JiraUsers.valueOf(user) == JiraUsers.BLACK_HEAD) {
			ErrorCollection errors = new MockAction();
			errors.addError("user", "User is not authorized.");
			result = new IssueResult((MutableIssue) issue, errors);
		}
		return result;
	}

	@Override
	public IssueResult getIssue(ApplicationUser user, String key) {
		Issue issue = JiraIssues.getTestJiraIssues().stream().filter(jiraIssue -> key.equals(jiraIssue.getKey()))
				.findFirst().orElse(new MockIssue(1, key));
		IssueResult result = new IssueResult((MutableIssue) issue);
		if (JiraUsers.valueOf(user) == JiraUsers.BLACK_HEAD) {
			ErrorCollection errors = new MockAction();
			errors.addError("user", "User is not authorized.");
			result = new IssueResult((MutableIssue) issue, errors);
		}
		return result;
	}

	@Override
	public boolean isEditable(Issue jiraIssue, ApplicationUser user) {
		return false;
	}

	@Override
	public IssueInputParameters newIssueInputParameters() {
		return new MockIssueInputParameters();
	}

	@Override
	public IssueInputParameters newIssueInputParameters(Map<String, String[]> arg0) {
		return new MockIssueInputParameters();
	}

	@Override
	public IssueResult transition(ApplicationUser arg0, TransitionValidationResult arg1) {
		return null;
	}

	@Override
	public IssueResult update(ApplicationUser user, UpdateValidationResult arg1) {
		return new IssueResult(arg1.getIssue());
	}

	@Override
	public IssueResult update(ApplicationUser arg0, UpdateValidationResult arg1, EventDispatchOption arg2,
			boolean arg3) {
		return null;
	}

	@Override
	public AssignValidationResult validateAssign(ApplicationUser arg0, Long arg1, String arg2) {

		return null;
	}

	@Override
	public CloneValidationResult validateClone(ApplicationUser arg0, Issue arg1, String arg2, boolean arg3,
			boolean arg4, boolean arg5, Map<CustomField, Optional<Boolean>> arg6) {

		return null;
	}

	@Override
	public CreateValidationResult validateCreate(ApplicationUser user, IssueInputParameters arg1) {
		MutableIssue issue = new MockIssue(1, "TEST-12");
		IssueType issueType = new MockIssueType(12, "Solution");
		issue.setIssueType(issueType);
		ErrorCollection errors = new MockAction();
		Map<String, Object> fieldValuesHolder = new ConcurrentHashMap<>();
		Map<String, org.codehaus.jackson.JsonNode> properties = new ConcurrentHashMap<>();
		if (user == null || JiraUsers.valueOf(user) == JiraUsers.BLACK_HEAD) {
			errors.addError("user", "User is not authorized.");
			return new CreateValidationResult(issue, errors, fieldValuesHolder, properties);
		}
		return new CreateValidationResult(issue, errors, fieldValuesHolder, properties);
	}

	@Override
	public DeleteValidationResult validateDelete(ApplicationUser user, Long issueId) {
		MutableIssue issue = new MockIssue(1, "TEST-12");
		IssueType issueType = new MockIssueType(12, "Solution");
		issue.setIssueType(issueType);
		ErrorCollection errors = new MockAction();
		if (user == null || JiraUsers.valueOf(user) == JiraUsers.BLACK_HEAD) {
			errors.addError("user", "User is not authorized.");
		}
		return new DeleteValidationResult(issue, errors);
	}

	@Override
	public CreateValidationResult validateSubTaskCreate(ApplicationUser arg0, Long arg1, IssueInputParameters arg2) {
		return null;
	}

	@Override
	public TransitionValidationResult validateTransition(ApplicationUser arg0, Long arg1, int arg2,
			IssueInputParameters arg3) {
		return null;
	}

	@Override
	public TransitionValidationResult validateTransition(ApplicationUser arg0, Long arg1, int arg2,
			IssueInputParameters arg3, TransitionOptions arg4) {
		return null;
	}

	@Override
	public UpdateValidationResult validateUpdate(ApplicationUser user, Long arg1, IssueInputParameters arg2) {
		Issue issue = JiraIssues.getJiraIssueByKey("TEST-12");
		ErrorCollection col = new MockAction();
		Map<String, Object> fieldValuesHolder = new ConcurrentHashMap<>();
		if (JiraUsers.valueOf(user) == JiraUsers.BLACK_HEAD) {
			col.addError("Test", "Test");
		}
		return new UpdateValidationResult((MutableIssue) issue, col, fieldValuesHolder);
	}

}
