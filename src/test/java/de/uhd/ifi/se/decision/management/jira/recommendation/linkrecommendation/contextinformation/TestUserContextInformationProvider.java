package de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.contextinformation;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

public class TestUserContextInformationProvider extends TestSetUp {

	private ContextInformationProvider userContextInformationProvider;

	@Before
	public void setUp() {
		init();
		userContextInformationProvider = new UserContextInformationProvider();
	}

	@Test
	public void testSameCreatorAndReporter() {
		assertEquals(0.9,
				userContextInformationProvider
						.assessRelation(KnowledgeElements.getAlternative(), KnowledgeElements.getDecision()).getValue(),
				0.1);
	}

	@Test
	public void testDifferentUser() {
		KnowledgeElement element = new KnowledgeElement();
		element.setProject("TEST");
		assertEquals(0.75,
				userContextInformationProvider.assessRelation(element, KnowledgeElements.getDecision()).getValue(), 0);
	}

	@Test
	public void testJiraIssuesUnknown() {
		KnowledgeElement element = new KnowledgeElement();
		element.setProject("TEST");
		assertEquals(0.75,
				userContextInformationProvider.assessRelation(KnowledgeElements.getDecision(), element).getValue(), 0);
	}

	@Test
	public void testIsApplicationUserEqual() {
		assertEquals(0., UserContextInformationProvider.isApplicationUserEqual(null, null), 0);
		assertEquals(1.0, UserContextInformationProvider.isApplicationUserEqual(
				JiraUsers.SYS_ADMIN.getApplicationUser(), JiraUsers.SYS_ADMIN.getApplicationUser()), 0);
		assertEquals(0., UserContextInformationProvider.isApplicationUserEqual(JiraUsers.SYS_ADMIN.getApplicationUser(),
				JiraUsers.BLACK_HEAD.getApplicationUser()), 0);
	}
}