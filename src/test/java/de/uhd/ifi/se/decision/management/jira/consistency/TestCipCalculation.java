package de.uhd.ifi.se.decision.management.jira.consistency;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.mock.issue.MockIssue;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.user.MockApplicationUser;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.consistency.implementation.TracingCIP;
import de.uhd.ifi.se.decision.management.jira.consistency.implementation.UserCIP;
import de.uhd.ifi.se.decision.management.jira.mocks.MockComponentAccessor;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraProjects;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import org.junit.BeforeClass;
import org.junit.Test;
import org.ofbiz.core.entity.GenericEntityException;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class TestCipCalculation extends TestSetUp {

	private static List<MutableIssue> testIssues;

	@BeforeClass
	public static void setUp() {
		TestSetUp.init();
		Project project = JiraProjects.getTestProject();
		TestCipCalculation.testIssues = JiraIssues.createJiraIssues(project);
	}

	@Test
	public void testCIP() {
		Issue baseIssue = TestCipCalculation.testIssues.get(0);
		ContextInformation contextInformation = new ContextInformation(baseIssue);
		try {
			Collection<LinkSuggestion> linkSuggestions = contextInformation.getLinkSuggestions();
			List<LinkSuggestion> sortedLinkSuggestions = linkSuggestions
				.stream()
				.sorted((LinkSuggestion::compareTo))
				.collect(Collectors.toList());
			LinkSuggestion identicalIssueSuggestion = sortedLinkSuggestions.get(sortedLinkSuggestions.size() - 1);
			assertEquals("The baseIssue should be the most similar to itself.", baseIssue.getKey(),
				identicalIssueSuggestion.getTargetIssue().getKey());
			assertNotNull(identicalIssueSuggestion.getScore().getScores());

			assertEquals("The baseIssue should be set correctly.", baseIssue.getKey(),
				identicalIssueSuggestion.getBaseIssue().getKey());

		} catch (NullPointerException | GenericEntityException e) {
			System.err.println("ERROR:");
			e.printStackTrace();
			assertNull(e);
		}
	}

	@Test
	public void testUserCIP() {
		ContextInformationProvider userCIP = new UserCIP();
		assertEquals("UserCIP_equalCreatorOrEqualAssignee", userCIP.getId());

		MockIssue i1 = new MockIssue(999, "TST-999");
		i1.setCreatorId(JiraUsers.SYS_ADMIN.createApplicationUser().getKey());
		i1.setAssignee(new MockApplicationUser("TESTUSER"));

		MockIssue i2 = new MockIssue(9999, "TST-9999");
		i2.setCreatorId(JiraUsers.SYS_ADMIN.createApplicationUser().getKey());
		i2.setAssignee(new MockApplicationUser("TESTUSER"));
		assertEquals(2., userCIP.assessRelation(i1, i2), 0);

		i2.setAssignee(new MockApplicationUser("NOT_TESTUSER"));
		assertEquals(1., userCIP.assessRelation(i1, i2), 0);

		i2.setAssignee(new MockApplicationUser("TESTUSER"));
		i2.setCreatorId(JiraUsers.BLACK_HEAD.createApplicationUser().getKey());
		assertEquals(1., userCIP.assessRelation(i1, i2), 0);

		i2.setAssignee(new MockApplicationUser("NOT_TESTUSER"));
		assertEquals(0., userCIP.assessRelation(i1, i2), 0);

		i2.setAssignee(null);
		i2.setCreatorId(null);
		assertEquals(0., userCIP.assessRelation(i1, i2), 0);

		i1.setAssignee(null);
		i1.setCreatorId(null);
		assertEquals(0., userCIP.assessRelation(i1, i2), 0);

	}


	@Test
	public void testTracingCIP() {
		ContextInformationProvider tracingCIP = new TracingCIP(MockComponentAccessor.getIssueLinkManager());
		assertEquals("TracingCIP_BFS", tracingCIP.getId());


		Issue i0 = testIssues.get(0);
		Issue i1 = testIssues.get(1);
		Issue i2 = testIssues.get(2);


		assertEquals(0.5, tracingCIP.assessRelation(i0, i1), 0);
		//Score is 1/3
		assertEquals(0.33, tracingCIP.assessRelation(i0, i2), 0.01);


	}

	@Test
	public void testLinkSuggestion() {
		LinkSuggestion linkSuggestion1 = new LinkSuggestion(testIssues.get(0), testIssues.get(1));
		linkSuggestion1.addToScore(0.5, "test");
		assertEquals(-1, linkSuggestion1.compareTo(null));


		LinkSuggestion linkSuggestion2 = new LinkSuggestion(testIssues.get(0), testIssues.get(1));
		linkSuggestion2.addToScore(0.5, "test");
		assertEquals(-1, linkSuggestion1.compareTo(linkSuggestion2));

		linkSuggestion2.addToScore(0.5, "test1");
		assertEquals(-1, linkSuggestion1.compareTo(linkSuggestion2));

		linkSuggestion1.addToScore(1., "test1");
		assertEquals(1, linkSuggestion1.compareTo(linkSuggestion2));
	}
}
