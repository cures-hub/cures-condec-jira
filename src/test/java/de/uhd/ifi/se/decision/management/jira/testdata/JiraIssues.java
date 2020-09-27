package de.uhd.ifi.se.decision.management.jira.testdata;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.issue.comments.CommentManager;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.status.MockStatus;
import com.atlassian.jira.issue.status.Status;
import com.atlassian.jira.mock.issue.MockIssue;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.GenericLinkManager;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssueTextPersistenceManager;

/**
 * Creates Jira issues used in unit tests.
 */
public class JiraIssues {

	public static List<MutableIssue> jiraIssues = new ArrayList<MutableIssue>();

	public static List<MutableIssue> getTestJiraIssues() {
		if (jiraIssues == null || jiraIssues.isEmpty()) {
			jiraIssues = createJiraIssues(JiraProjects.TEST.createJiraProject(1));
		}
		return jiraIssues;
	}

	public static List<MutableIssue> createJiraIssues(Project project) {
		List<MutableIssue> jiraIssues = new ArrayList<MutableIssue>();

		if (project == null) {
			return jiraIssues;
		}
		ApplicationUser user = JiraUsers.SYS_ADMIN.createApplicationUser();

		List<IssueType> jiraIssueTypes = JiraIssueTypes.getTestTypes();

		// Work items
		MutableIssue jiraIssue = createJiraIssue(1, jiraIssueTypes.get(0), project, "WI: Implement feature", user);
		jiraIssues.add(jiraIssue);
		jiraIssue = createJiraIssue(14, jiraIssueTypes.get(0), project, "WI: Yet another work item", user);
		jiraIssues.add(jiraIssue);
		jiraIssue = createJiraIssue(30, jiraIssueTypes.get(0), project, "WI: Do an interesting task", user);
		jiraIssues.add(jiraIssue);

		// Issues (= decision problems)
		jiraIssue = createJiraIssue(2, jiraIssueTypes.get(1), project, "How can we implement the feature?", user);
		jiraIssues.add(jiraIssue);
		jiraIssue = createJiraIssue(12, jiraIssueTypes.get(1), project, "How can we implement the new get function?",
				user);
		jiraIssues.add(jiraIssue);

		// Alternative
		jiraIssue = createJiraIssue(3, jiraIssueTypes.get(2), project, "We could do it like this!", user);
		jiraIssues.add(jiraIssue);

		// Decision
		jiraIssue = createJiraIssue(4, jiraIssueTypes.get(3), project, "We will do it like this!", user);
		jiraIssues.add(jiraIssue);

		// Pro-Argument for the decision
		jiraIssue = createJiraIssue(5, jiraIssueTypes.get(4), project, "This is a great solution.", user);
		jiraIssues.add(jiraIssue);

		// Con-Argument
		jiraIssue = createJiraIssue(6, jiraIssueTypes.get(4), project, "This sucks!", user);
		jiraIssues.add(jiraIssue);

		// Non-functional requirement (used as criteria in decision table)
		jiraIssue = createJiraIssue(7, jiraIssueTypes.get(5), project, "NFR: Usabililty", user);
		jiraIssues.add(jiraIssue);

		return jiraIssues;
	}

	public static MutableIssue createJiraIssue(int id, IssueType issueType, Project project, String summary,
			ApplicationUser user) {
		MutableIssue issue = new MockIssue(id, project.getKey() + "-" + id);
		((MockIssue) issue).setProjectId(project.getId());
		issue.setProjectObject(project);
		issue.setIssueType(issueType);
		issue.setSummary(summary);
		issue.setDescription(summary);
		issue.setCreated(new Timestamp(System.currentTimeMillis()));
		issue.setResolutionDate(new Timestamp(System.currentTimeMillis() + 10000));
		Status status = new MockStatus("1", "Unresolved");
		issue.setStatus(status);
		((MockIssue) issue).setReporter(user);
		return issue;
	}

	public static Issue addComment(Issue issue) {
		List<PartOfJiraIssueText> comment = JiraIssues.getSentencesForCommentText("{issue} testobject {issue}");
		PartOfJiraIssueText sentence = comment.get(0);
		sentence.setJiraIssueId(issue.getId());
		KnowledgePersistenceManager.getOrCreate("TEST").getJiraIssueTextManager().insertKnowledgeElement(sentence,
				JiraUsers.SYS_ADMIN.getApplicationUser());
		GenericLinkManager.insertLink(new Link(new KnowledgeElement(issue), sentence),
				JiraUsers.SYS_ADMIN.getApplicationUser());
		return sentence.getJiraIssue();
	}

	public static Comment addCommentsToIssue(Issue issue, String comment) {
		ComponentAccessor.getCommentManager().deleteCommentsForIssue(issue);
		// Get the current logged in user
		ApplicationUser currentUser = JiraUsers.SYS_ADMIN.getApplicationUser();
		// Get access to the Jira comment and component manager
		CommentManager commentManager = ComponentAccessor.getCommentManager();
		// Get the last comment entered in on the issue to a String
		return commentManager.create(issue, currentUser, comment, true);
	}

	public static PartOfJiraIssueText addElementToDataBase() {
		return addElementToDataBase(12231, "Argument");
	}

	public static PartOfJiraIssueText addElementToDataBase(long id, String type) {
		PartOfJiraIssueText element = new PartOfJiraIssueText();
		element.setProject("TEST");
		element.setJiraIssueId(1);
		element.setId(id);
		element.setKey("TEST-" + id);
		element.setType(type);
		element.setDescription("Old");
		element.setDocumentationLocation(DocumentationLocation.JIRAISSUETEXT);
		element = (PartOfJiraIssueText) KnowledgePersistenceManager.getOrCreate("TEST").getJiraIssueTextManager()
				.insertKnowledgeElement(element, JiraUsers.SYS_ADMIN.getApplicationUser());
		return element;
	}

	public static List<PartOfJiraIssueText> getSentencesForCommentText(String text) {
		Issue issue = ComponentAccessor.getIssueManager().getIssueObject("TEST-30");
		ApplicationUser currentUser = JiraUsers.SYS_ADMIN.createApplicationUser();
		ComponentAccessor.getCommentManager().deleteCommentsForIssue(issue);
		Comment comment = ComponentAccessor.getCommentManager().create(issue, currentUser, text, true);
		List<PartOfJiraIssueText> sentences = JiraIssueTextPersistenceManager.insertPartsOfComment(comment);
		return sentences;
	}
}
