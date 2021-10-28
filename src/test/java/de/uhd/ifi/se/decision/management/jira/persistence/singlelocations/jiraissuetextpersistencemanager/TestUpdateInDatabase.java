package de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.jiraissuetextpersistencemanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssueTextPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

public class TestUpdateInDatabase extends TestSetUp {

	protected static JiraIssueTextPersistenceManager manager;
	protected static ApplicationUser user;

	@Before
	public void setUp() {
		init();
		manager = KnowledgePersistenceManager.getInstance("TEST").getJiraIssueTextManager();
		user = JiraUsers.SYS_ADMIN.getApplicationUser();
	}

	@Test
	@NonTransactional
	public void testUpdateSentenceElement() {
		PartOfJiraIssueText sentence = JiraIssues.getIrrelevantSentence();
		sentence.setType(KnowledgeType.ALTERNATIVE);
		manager.updateInDatabase(sentence);
		PartOfJiraIssueText element = (PartOfJiraIssueText) manager.getKnowledgeElement(sentence);
		assertEquals(KnowledgeType.ALTERNATIVE, element.getType());
	}

	@Test
	@NonTransactional
	public void testUpdateKnowledgeType() {
		PartOfJiraIssueText sentence = JiraIssues.getIrrelevantSentence();
		sentence.setType(KnowledgeType.ALTERNATIVE);
		manager.updateInDatabase(sentence);
		PartOfJiraIssueText element = (PartOfJiraIssueText) manager.getKnowledgeElement(sentence);
		assertEquals(KnowledgeType.ALTERNATIVE, element.getType());
	}

	@Test
	@NonTransactional
	public void testSetSentenceRelevant() {
		PartOfJiraIssueText element = JiraIssues.getIrrelevantSentence();
		element.setRelevant(true);
		element.setValidated(true);
		manager.updateInDatabase(element);
		element = (PartOfJiraIssueText) manager.getKnowledgeElement(element);
		assertTrue(element.isRelevant());
		assertTrue(element.isValidated());
		assertEquals(KnowledgeType.OTHER, element.getType());
	}
}
