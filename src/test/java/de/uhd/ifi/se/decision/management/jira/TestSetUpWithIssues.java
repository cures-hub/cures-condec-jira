package de.uhd.ifi.se.decision.management.jira;

import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Arrays;
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
import com.atlassian.jira.project.MockProject;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.user.MockApplicationUser;

import de.uhd.ifi.se.decision.management.jira.extraction.TestTextSplitter;
import de.uhd.ifi.se.decision.management.jira.mocks.MockComponentAccessor;
import de.uhd.ifi.se.decision.management.jira.mocks.MockDatabase;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssueTextPersistenceManager;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(MockDatabase.class)
public abstract class TestSetUpWithIssues {

	protected static MockIssue issue;
	private static EntityManager entityManager;

	public static void initialization() {
		new MockComponentAccessor();
		createProjectIssueStructure();
		initComponentGetter();
	}

	public static void initComponentGetter() {
		ActiveObjects activeObjects = mock(ActiveObjects.class);
		if (entityManager != null) {
			activeObjects = new TestActiveObjects(entityManager);
		}
		new ComponentGetter(new de.uhd.ifi.se.decision.management.jira.mocks.MockUserManager(), activeObjects);
	}

	private static void createProjectIssueStructure() {
		Project project = new MockProject(1, "TEST");
		((MockProject) project).setKey("TEST");
		MockComponentAccessor.getProjectManager().addProject(project);

		List<IssueType> jiraIssueTypes = createJiraIssueTypesForDecisionKnowledgeTypes();

		List<KnowledgeType> types = Arrays.asList(KnowledgeType.values());
		addJiraIssue(30, "TEST-" + 30, jiraIssueTypes.get(13), project);

		for (int i = 2; i < jiraIssueTypes.size() + 2; i++) {
			issue = addJiraIssue(i, "TEST-" + i, jiraIssueTypes.get(i - 2), project);
			if (i > types.size() - 4) {
				issue.setParentId((long) 3);
			}
		}
		IssueType issueType = new MockIssueType(50, "Class");
		issue = addJiraIssue(50, "TEST-" + 50, issueType, project);
		issue.setParentId((long) 3);

		Project condecProject = new MockProject(3, "CONDEC");
		((MockProject) condecProject).setKey("CONDEC");
		MockComponentAccessor.getProjectManager().addProject(condecProject);
		addJiraIssue(1234, "CONDEC-" + 1234, jiraIssueTypes.get(2), condecProject);
	}

	private static List<IssueType> createJiraIssueTypesForDecisionKnowledgeTypes() {
		List<IssueType> jiraIssueTypes = new ArrayList<IssueType>();
		int i = 0;
		for (KnowledgeType type : KnowledgeType.values()) {
			IssueType issueType = new MockIssueType(i, type.name().toLowerCase(Locale.ENGLISH));
			MockComponentAccessor.getConstantsManager().addIssueType(issueType);
			jiraIssueTypes.add(issueType);
			i++;
		}
		return jiraIssueTypes;
	}

	private static MockIssue addJiraIssue(int id, String key, IssueType issueType, Project project) {
		MutableIssue issue = new MockIssue(id, key);
		((MockIssue) issue).setProjectId(project.getId());
		issue.setProjectObject(project);
		issue.setIssueType(issueType);
		issue.setSummary("Test");
		issue.setDescription("Test");
		MockComponentAccessor.getIssueManager().addIssue(issue);
		return (MockIssue) issue;
	}

	public MockIssue createGlobalIssue() {
		if (issue != null) {
			return issue;
		}
		Project project = ComponentAccessor.getProjectManager().getProjectByCurrentKey("TEST");
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
		JiraIssueTextPersistenceManager.insertDecisionKnowledgeElement(sentence, new MockApplicationUser("NoFails"));

		return (MockIssue) sentence.getJiraIssue();
	}

	public static MockIssue createIssue() {
		Project project = ComponentAccessor.getProjectManager().getProjectByCurrentKey("TEST");
		MockIssue issue = new MockIssue(30, "TEST-" + 30);
		((MockIssue) issue).setProjectId(project.getId());
		issue.setProjectObject(project);
		IssueType issueType = new MockIssueType(1, KnowledgeType.DECISION.toString().toLowerCase(Locale.ENGLISH));
		issue.setIssueType(issueType);
		issue.setSummary("Test");
		return issue;
	}
}