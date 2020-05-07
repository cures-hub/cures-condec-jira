package de.uhd.ifi.se.decision.management.jira.consistency;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.exception.CreateException;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.link.IssueLinkType;
import com.atlassian.jira.issue.link.IssueLinkTypeManager;
import com.atlassian.jira.mock.issue.MockIssue;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.MockUserManager;
import com.atlassian.jira.user.util.UserManager;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraProjects;
import org.junit.BeforeClass;
import org.junit.Test;
import org.ofbiz.core.entity.GenericEntityException;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TestCipCalculation extends TestSetUp {

	private static Issue baseIssue;
	private static List<MutableIssue> testIssues;
	private static ApplicationUser user1, user2;

	@BeforeClass
	public static void setUp() throws GenericEntityException {
		Project project = JiraProjects.getTestProject();

		UserManager mockUserManager = new MockUserManager();
		user1 = mockUserManager.getUserByName("User1");
		user2 = mockUserManager.getUserByName("User2");

		testIssues = JiraIssues.createJiraIssues(project);

		TestCipCalculation.baseIssue = testIssues.get(0);
		System.out.println(TestCipCalculation.baseIssue);
		System.out.println(ComponentAccessor.getIssueLinkManager());


	}

	private static Issue generateMockIssue(Project project, String description, String key) {
		return generateMockIssue(project, description, key, user1, user1, new Timestamp(new Date().getTime()));
	}

	private static void linkMockIssues(Issue issue, Issue linkedIssue, ApplicationUser linkCreator) throws CreateException {
		ComponentAccessor
			.getIssueLinkManager()
			.createIssueLink(issue.getId(), linkedIssue.getId(), getRandomLinkId(), (long) 0, linkCreator);
	}

	private static Issue generateMockIssue(Project project, String description, String key, ApplicationUser assignee, ApplicationUser creator, Timestamp created) {
		MutableIssue issue = ComponentAccessor.getIssueManager().getIssueByCurrentKey(key);
		//issue.setKey(key);
		issue.setProjectObject(project);
		issue.setDescription(description);
		issue.setCreated(created);
		issue.setAssignee(assignee);
		//issue.setCreatorId(creator.getKey());
		return issue;
	}

	private static long getRandomLinkId() {
		long issueLinkTypeId = 0;
		IssueLinkTypeManager issueLinkTypeManager = ComponentAccessor.getComponent(IssueLinkTypeManager.class);
		Collection<IssueLinkType> issueLinkTypes = issueLinkTypeManager.getIssueLinkTypes(false); //b = excludeSystemLinks
		int randomIndex = new Random().nextInt(issueLinkTypes.size());
		int counter = 0;
		for (IssueLinkType issueLinkType : issueLinkTypes) {
			if (counter == randomIndex) {
				issueLinkTypeId = issueLinkType.getId();
			}
			counter++;
		}
		return issueLinkTypeId;
	}

	@Test
	public void testCIP() {
		ContextInformation contextInformation = new ContextInformation(TestCipCalculation.baseIssue);

		try {
			Collection<LinkSuggestion> linkSuggestions = contextInformation.getLinkSuggestions();
			List<LinkSuggestion> sortedLinkSuggestions = linkSuggestions
				.stream()
				.sorted((LinkSuggestion::compareTo))
				.collect(Collectors.toList());
			assertEquals("The baseIssue should be the most similar to itself.", baseIssue.getKey(),
				sortedLinkSuggestions.get(sortedLinkSuggestions.size()-1).getTargetIssue().getKey());
		} catch (NullPointerException | GenericEntityException e) {
			System.out.println("ERROR:");
			e.printStackTrace();
			assertNull(e);
		}

	}
}
