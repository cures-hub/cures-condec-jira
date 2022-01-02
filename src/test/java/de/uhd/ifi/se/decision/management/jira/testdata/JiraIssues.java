package de.uhd.ifi.se.decision.management.jira.testdata;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.atlassian.jira.bc.project.component.MutableProjectComponent;
import com.atlassian.jira.bc.project.component.ProjectComponent;
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
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.GenericLinkManager;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssueTextPersistenceManager;

/**
 * Creates Jira issues used in unit tests.
 */
public class JiraIssues {

	public static List<Issue> jiraIssues = new ArrayList<Issue>();

	public static List<Issue> getTestJiraIssues() {
		if (jiraIssues == null || jiraIssues.isEmpty()) {
			jiraIssues = createJiraIssues(JiraProjects.TEST.createJiraProject(1));
		}
		return jiraIssues;
	}

	public static int getTestJiraIssueCount() {
		return getTestJiraIssues().size();
	}

	public static List<Issue> createJiraIssues(Project project) {
		List<Issue> jiraIssues = new ArrayList<Issue>();

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
		jiraIssue = addTestComponentToIssue(jiraIssue);
		jiraIssues.add(jiraIssue);
		jiraIssue = createJiraIssue(31, jiraIssueTypes.get(0), project, "WI: Deal with the drunken sailor", user);
		jiraIssues.add(jiraIssue);

		// Issues (= decision problems)
		jiraIssue = createJiraIssue(2, jiraIssueTypes.get(1), project, "How can we implement the feature?", user);
		jiraIssue.setStatus(new MockStatus("2", "resolved"));

		jiraIssues.add(jiraIssue);
		jiraIssue = createJiraIssue(12, jiraIssueTypes.get(1), project, "How can we implement the new get function?",
				user);
		jiraIssues.add(jiraIssue);
		jiraIssue = createJiraIssue(32, jiraIssueTypes.get(1), project, "What shall we do with the drunken sailor?",
				user);
		jiraIssues.add(jiraIssue);

		// Alternative
		jiraIssue = createJiraIssue(3, jiraIssueTypes.get(2), project, "We could do it like this!", user);
		jiraIssues.add(jiraIssue);
		jiraIssue = createJiraIssue(33, jiraIssueTypes.get(2), project, "Put him in the long boat till he's sober!",
				user);
		jiraIssues.add(jiraIssue);
		jiraIssue = createJiraIssue(36, jiraIssueTypes.get(2), project,
				"Put him in the scuppers with a hose-pipe on him!", user);
		jiraIssues.add(jiraIssue);

		// Decision
		jiraIssue = createJiraIssue(4, jiraIssueTypes.get(3), project, "We will do it like this!", user);
		jiraIssues.add(jiraIssue);

		// Pro-Argument for the decision
		jiraIssue = createJiraIssue(5, jiraIssueTypes.get(4), project, "This is a great solution.", user);
		jiraIssues.add(jiraIssue);
		jiraIssue = createJiraIssue(34, jiraIssueTypes.get(4), project,
				"After this procedure, the sailor will be sober.", user);
		jiraIssues.add(jiraIssue);
		jiraIssue = createJiraIssue(37, jiraIssueTypes.get(4), project,
				"After this procedure, the sailor will probably not get drunk again.", user);
		jiraIssues.add(jiraIssue);

		// Con-Argument
		jiraIssue = createJiraIssue(6, jiraIssueTypes.get(4), project, "This sucks!", user);
		jiraIssues.add(jiraIssue);
		jiraIssue = createJiraIssue(35, jiraIssueTypes.get(4), project,
				"This procedure endangers the availability of the long boat in a case of shipwrecking or maritime emergency.",
				user);
		jiraIssues.add(jiraIssue);
		jiraIssue = createJiraIssue(38, jiraIssueTypes.get(4), project,
				"This procedure is in violation of the United Nations Convention against Torture and Other Cruel, Inhuman or Degrading Treatment or Punishment.",
				user);
		jiraIssues.add(jiraIssue);

		// Non-functional requirement (used as criteria in decision table)
		jiraIssue = createJiraIssue(7, jiraIssueTypes.get(5), project, "NFR: Usability", user);
		jiraIssues.add(jiraIssue);

		return jiraIssues;
	}

	public static Issue getJiraIssueByKey(String key) {
		for (Issue issue : jiraIssues) {
			if (issue.getKey().equalsIgnoreCase(key)) {
				return issue;
			}
		}
		return null;
	}

	public static MutableIssue createJiraIssue(int id, IssueType issueType, Project project, String summary,
			ApplicationUser user) {
		MutableIssue issue = new MockIssue(id, project.getKey() + "-" + id);
		issue.setProjectId(project.getId());
		issue.setProjectObject(project);
		issue.setIssueType(issueType);
		issue.setSummary(summary);
		issue.setDescription(summary);
		issue.setCreated(new Timestamp(System.currentTimeMillis()));
		issue.setUpdated(issue.getCreated());
		issue.setResolutionDate(new Timestamp(System.currentTimeMillis() + 10000));
		Status status = new MockStatus("1", "Unresolved");
		issue.setStatus(status);
		issue.setReporter(user);
		issue.setReporterId(user.getUsername());
		issue.setAssignee(user);
		return issue;
	}

	public static Issue addComment(Issue issue) {
		List<PartOfJiraIssueText> comment = JiraIssues.getSentencesForCommentText("{issue} testobject {issue}");
		PartOfJiraIssueText sentence = comment.get(0);
		sentence.setJiraIssue(issue.getId());
		KnowledgePersistenceManager.getInstance("TEST").getJiraIssueTextManager().insertKnowledgeElement(sentence,
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
		return addElementToDataBase(12231, KnowledgeType.ARGUMENT);
	}

	public static PartOfJiraIssueText addNonValidatedElementToDataBase(long id, KnowledgeType type) {
		PartOfJiraIssueText element = new PartOfJiraIssueText();
		element.setProject("TEST");
		element.setJiraIssue(1);
		element.setSummary("We could do X!");
		element.setId(id);
		element.setKey("TEST-" + id);
		element.setType(type);
		element.setRelevant(type != KnowledgeType.OTHER);
		element.setValidated(false);
		element.setDescription("Old");
		element.setDocumentationLocation(DocumentationLocation.JIRAISSUETEXT);
		element = (PartOfJiraIssueText) KnowledgePersistenceManager.getInstance("TEST").getJiraIssueTextManager()
				.insertKnowledgeElement(element, JiraUsers.SYS_ADMIN.getApplicationUser());
		return element;
	}

	public static PartOfJiraIssueText addElementToDataBase(long id, KnowledgeType type) {
		PartOfJiraIssueText element = new PartOfJiraIssueText();
		element.setProject("TEST");
		element.setJiraIssue(1);
		element.setId(id);
		element.setKey("TEST-" + id);
		element.setType(type);
		element.setRelevant(type != KnowledgeType.OTHER);
		element.setValidated(true);
		element.setDescription("Old");
		element.setDocumentationLocation(DocumentationLocation.JIRAISSUETEXT);
		element = (PartOfJiraIssueText) KnowledgePersistenceManager.getInstance("TEST").getJiraIssueTextManager()
				.insertKnowledgeElement(element, JiraUsers.SYS_ADMIN.getApplicationUser());
		return element;
	}

	public static List<PartOfJiraIssueText> getSentencesForCommentText(String text) {
		Issue issue = ComponentAccessor.getIssueManager().getIssueObject("TEST-30");
		ApplicationUser currentUser = JiraUsers.SYS_ADMIN.createApplicationUser();
		ComponentAccessor.getCommentManager().deleteCommentsForIssue(issue);
		Comment comment = ComponentAccessor.getCommentManager().create(issue, currentUser, text, true);
		JiraIssueTextPersistenceManager persistenceManager = KnowledgePersistenceManager.getInstance("TEST")
				.getJiraIssueTextManager();
		List<PartOfJiraIssueText> sentences = persistenceManager.updateElementsOfCommentInDatabase(comment);
		return sentences;
	}

	public static PartOfJiraIssueText getIrrelevantSentence() {
		List<PartOfJiraIssueText> sentences = getSentencesForCommentText("This is a test sentence.");
		return sentences.get(0);
	}

	public static boolean getNonValidatedSentence() {
		List<PartOfJiraIssueText> sentences = getSentencesForCommentText("This is a test sentence.");
		return sentences.get(0).isValidated();
	}

	public static MutableIssue addTestComponentToIssue(MutableIssue issue) {
		MutableProjectComponent component = new MutableProjectComponent((long) 0, "Feature", "Do something", "FEATURE", (long) 0, issue.getProjectId(), false);
		Collection<ProjectComponent> components = new ArrayList<ProjectComponent>();
		components.add(component);
		issue.setComponent(components);
		return issue;
	}
}
