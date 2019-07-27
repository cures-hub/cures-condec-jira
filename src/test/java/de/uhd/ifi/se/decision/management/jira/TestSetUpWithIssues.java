package de.uhd.ifi.se.decision.management.jira;

import static org.mockito.Mockito.mock;

import java.util.List;
import java.util.Locale;

import org.junit.runner.RunWith;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.issuetype.MockIssueType;
import com.atlassian.jira.mock.issue.MockIssue;
import com.atlassian.jira.project.Project;

import de.uhd.ifi.se.decision.management.jira.extraction.TestTextSplitter;
import de.uhd.ifi.se.decision.management.jira.mocks.MockComponentAccessor;
import de.uhd.ifi.se.decision.management.jira.mocks.MockDatabase;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssueTextPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraProjects;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(MockDatabase.class)
public abstract class TestSetUpWithIssues {

	protected static MutableIssue issue;
	private static EntityManager entityManager;

	public static void initialization() {
		initComponentAccessor();
		initComponentGetter();
		issue = ComponentAccessor.getIssueManager().getIssueObject("TEST-30");
	}

	/**
	 * The ComponentAccessor is a class provided by JIRA. It contains classes to
	 * access JIRA's internal classes such as the ProjectManager or a UserManager.
	 * 
	 * @see ComponentAccessor
	 * @see MockComponentAccessor
	 */
	public static void initComponentAccessor() {
		new MockComponentAccessor();
	}

	/**
	 * The ComponentGetter is a class provided by the ConDec plugin. It enables to
	 * access the active objects databases for object relational mapping. Further,
	 * it contains a different user manager than that provided by the
	 * ComponentAccessor to handle users in HTTP requests.
	 * 
	 * @see ComponentGetter
	 */
	public static void initComponentGetter() {
		ActiveObjects activeObjects = mock(ActiveObjects.class);
		if (entityManager != null) {
			activeObjects = new TestActiveObjects(entityManager);
		}
		new ComponentGetter(new de.uhd.ifi.se.decision.management.jira.mocks.MockUserManager(), activeObjects);
	}

	public MutableIssue createGlobalIssue() {
		if (issue != null) {
			return issue;
		}
		Project project = JiraProjects.getTestProject();
		issue = new MockIssue(30, "TEST-" + 30);
		((MockIssue) issue).setProjectId(project.getId());
		issue.setProjectObject(project);
		IssueType issueType = new MockIssueType(1, KnowledgeType.DECISION.toString().toLowerCase(Locale.ENGLISH));
		issue.setIssueType(issueType);
		issue.setSummary("Test");
		return issue;
	}

	public static MockIssue createGlobalIssueWithComment() {
		List<PartOfJiraIssueText> comment = TestTextSplitter.getSentencesForCommentText("{issue} testobject {issue}");
		PartOfJiraIssueText sentence = comment.get(0);
		sentence.setJiraIssueId(createIssue().getId());
		JiraIssueTextPersistenceManager.insertDecisionKnowledgeElement(sentence,
				JiraUsers.SYS_ADMIN.getApplicationUser());

		return (MockIssue) sentence.getJiraIssue();
	}

	public static MockIssue createIssue() {
		Project project = JiraProjects.getTestProject();
		MockIssue issue = new MockIssue(30, "TEST-" + 30);
		((MockIssue) issue).setProjectId(project.getId());
		issue.setProjectObject(project);
		IssueType issueType = new MockIssueType(1, KnowledgeType.DECISION.toString().toLowerCase(Locale.ENGLISH));
		issue.setIssueType(issueType);
		issue.setSummary("Test");
		return issue;
	}
}