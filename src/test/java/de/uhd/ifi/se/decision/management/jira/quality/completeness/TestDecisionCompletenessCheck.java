package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import java.util.List;
import java.util.Set;

import com.atlassian.jira.user.ApplicationUser;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import net.java.ao.test.jdbc.NonTransactional;

import static org.junit.Assert.*;

public class TestDecisionCompletenessCheck extends TestSetUp {
	private List<KnowledgeElement> elements;
	private KnowledgeElement decision;
	private ApplicationUser user;
	private DecisionCompletenessCheck decisionCompletenessCheck;

	@Before
	public void setUp() {
		init();
		user = JiraUsers.SYS_ADMIN.getApplicationUser();
		elements = KnowledgeElements.getTestKnowledgeElements();
		decision = elements.get(6);
		decisionCompletenessCheck = new DecisionCompletenessCheck();
	}

	@Test
	@NonTransactional
	public void testIsLinkedToIssue() {
		assertEquals(KnowledgeType.DECISION, decision.getType());
		assertEquals(4, decision.getId());
		KnowledgeElement issue = elements.get(3);
		assertEquals(KnowledgeType.ISSUE, issue.getType());
		assertEquals(2, issue.getId());
		assertNotNull(decision.getLink(issue));
		assertTrue(decisionCompletenessCheck.execute(decision));
	}

	@Test
	@NonTransactional
	public void testIsNotLinkedToIssue() {
		assertEquals(KnowledgeType.DECISION, decision.getType());
		assertEquals(4, decision.getId());

		Set<Link> links = decision.getLinks();
		for (Link link : links) {
			if (link.getOppositeElement(decision).getType() == KnowledgeType.ISSUE) {
				KnowledgePersistenceManager.getOrCreate("TEST").deleteLink(link, user);
			}
		}
		assertFalse(decisionCompletenessCheck.execute(decision));
	}

	@Test
	@NonTransactional
	public void testIsCompleteAccordingToSettings() {
		// set criteria "decision has to be linked to pro-argument" in definition of done
		DefinitionOfDone definitionOfDone = new DefinitionOfDone();
		definitionOfDone.setDecisionLinkedToPro(true);
		ConfigPersistenceManager.setDefinitionOfDone("TEST", definitionOfDone);
		assertFalse(decisionCompletenessCheck.execute(decision));
		// create link between decision and pro-argument
		KnowledgeElement pro = elements.get(7);
		pro.setType(KnowledgeType.PRO);
		KnowledgePersistenceManager.getOrCreate("TEST").insertKnowledgeElement(pro, user);
		assertSame(pro.getType(), KnowledgeType.PRO);
		KnowledgePersistenceManager.getOrCreate("TEST").insertLink(decision, pro, user);
		assertNotNull(decision.getLink(pro));
		assertTrue(decisionCompletenessCheck.execute(decision));
	}
}
