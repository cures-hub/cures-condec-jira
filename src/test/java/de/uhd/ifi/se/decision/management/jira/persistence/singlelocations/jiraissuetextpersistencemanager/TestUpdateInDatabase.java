package de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.jiraissuetextpersistencemanager;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

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
		manager = KnowledgePersistenceManager.getOrCreate("TEST").getJiraIssueTextManager();
		user = JiraUsers.SYS_ADMIN.getApplicationUser();
	}

	@Test
	@NonTransactional
	public void testUpdateSentenceElement() {
		List<PartOfJiraIssueText> comment = JiraIssues.getSentencesForCommentText("first Comment");
		PartOfJiraIssueText sentence = comment.get(0);
		sentence.setType(KnowledgeType.ALTERNATIVE);
		JiraIssueTextPersistenceManager.updateInDatabase(sentence);
		PartOfJiraIssueText element = (PartOfJiraIssueText) manager.getKnowledgeElement(sentence);
		assertEquals(KnowledgeType.ALTERNATIVE, element.getType());
	}

	@Test
	@NonTransactional
	public void testUpdateKnowledgeType() {
		List<PartOfJiraIssueText> comment = JiraIssues.getSentencesForCommentText("first Comment");
		PartOfJiraIssueText sentence = comment.get(0);
		sentence.setType(KnowledgeType.ALTERNATIVE);
		JiraIssueTextPersistenceManager.updateInDatabase(sentence);
		PartOfJiraIssueText element = (PartOfJiraIssueText) manager.getKnowledgeElement(sentence);
		assertEquals(KnowledgeType.ALTERNATIVE, element.getType());
	}

	@Test
	@NonTransactional
	public void testSetSentenceIrrlevant() {
		List<PartOfJiraIssueText> comment = JiraIssues.getSentencesForCommentText("first Comment");
		PartOfJiraIssueText element = comment.get(0);

		element.setRelevant(false);
		element.setValidated(true);
		JiraIssueTextPersistenceManager.updateInDatabase(element);
		element = (PartOfJiraIssueText) manager.getKnowledgeElement(element);
		assertFalse(element.isRelevant());
		assertTrue(element.isValidated());
		assertEquals(KnowledgeType.OTHER, element.getType());
	}
}
