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
import com.atlassian.jira.mock.issue.MockIssue;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssueTextPersistenceManager;

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
		MutableIssue issue = createJiraIssue(1, jiraIssueTypes.get(0), project, "WI: Implement feature", user);
		jiraIssues.add(issue);
		issue = createJiraIssue(14, jiraIssueTypes.get(0), project, "WI: Yet another work item", user);
		jiraIssues.add(issue);
		issue = createJiraIssue(30, jiraIssueTypes.get(0), project, "WI: Do an interesting task", user);
		jiraIssues.add(issue);
		// Issue
		issue = createJiraIssue(2, jiraIssueTypes.get(1), project, "How can we implement the feature?", user);
		jiraIssues.add(issue);
		issue = createJiraIssue(12, jiraIssueTypes.get(1), project, "How can we implement the new get function?", user);
		jiraIssues.add(issue);
		// Alternative
		issue = createJiraIssue(3, jiraIssueTypes.get(2), project, "We could do it like this!", user);
		jiraIssues.add(issue);
		// Decision
		issue = createJiraIssue(4, jiraIssueTypes.get(3), project, "We will do it like this!", user);
		jiraIssues.add(issue);
		// Pro-Argument for the decision
		issue = createJiraIssue(5, jiraIssueTypes.get(4), project, "This is a great solution.", user);
		jiraIssues.add(issue);

		return jiraIssues;
	}

	private static MutableIssue createJiraIssue(int id, IssueType issueType, Project project, String summary,
			ApplicationUser user) {
		MutableIssue issue = new MockIssue(id, project.getKey() + "-" + id);
		((MockIssue) issue).setProjectId(project.getId());
		issue.setProjectObject(project);
		issue.setIssueType(issueType);
		issue.setSummary(summary);
		issue.setDescription(summary);
		issue.setCreated(new Timestamp(System.currentTimeMillis()));
		issue.setResolutionDate(new Timestamp(System.currentTimeMillis() + 10000));
		((MockIssue) issue).setReporter(user);
		return issue;
	}

	public static Issue addComment(Issue issue) {
		List<PartOfJiraIssueText> comment = JiraIssues.getSentencesForCommentText("{issue} testobject {issue}");
		PartOfJiraIssueText sentence = comment.get(0);
		sentence.setJiraIssueId(issue.getId());
		KnowledgePersistenceManager.getOrCreate("TEST").insertKnowledgeElement(sentence,
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
		ApplicationUser currentUser = JiraUsers.SYS_ADMIN.getApplicationUser();
		ComponentAccessor.getCommentManager().deleteCommentsForIssue(issue);
		Comment comment = ComponentAccessor.getCommentManager().create(issue, currentUser, text, true);
		List<PartOfJiraIssueText> sentences = JiraIssueTextPersistenceManager.insertPartsOfComment(comment);
		return sentences;
	}
}
