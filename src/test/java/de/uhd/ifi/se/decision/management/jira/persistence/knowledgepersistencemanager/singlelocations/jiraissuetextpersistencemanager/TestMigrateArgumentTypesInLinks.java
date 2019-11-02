package de.uhd.ifi.se.decision.management.jira.persistence.knowledgepersistencemanager.singlelocations.jiraissuetextpersistencemanager;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.comments.MutableComment;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.JiraIssueTextPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

public class TestMigrateArgumentTypesInLinks extends TestSetUp {

	protected static JiraIssueTextPersistenceManager manager;
	protected static ApplicationUser user;

	@Before
	public void setUp() {
		init();
		manager = new JiraIssueTextPersistenceManager("TEST");
		user = JiraUsers.SYS_ADMIN.getApplicationUser();
	}

	@Test
	@NonTransactional
	public void testProjectKeyInvalid() {
		assertFalse(JiraIssueTextPersistenceManager.migrateArgumentTypesInLinks(null));
		assertFalse(JiraIssueTextPersistenceManager.migrateArgumentTypesInLinks(""));
	}

	@Test
	@NonTransactional
	public void testProjectKeyFilled() {
		assertTrue(JiraIssueTextPersistenceManager.migrateArgumentTypesInLinks("TEST"));
	}

	@Test
	@NonTransactional
	public void testCleanSentenceDatabaseForProject() {
		List<PartOfJiraIssueText> partsOfText = JiraIssues.getSentencesForCommentText(
				"some sentence in front.  {pro} testobject {pro} some sentence in the back.");
		long id = manager.insertDecisionKnowledgeElement(partsOfText.get(1), null).getId();

		MutableComment comment = ComponentAccessor.getCommentManager()
				.getMutableComment(partsOfText.get(1).getCommentId());
		ComponentAccessor.getCommentManager().delete(comment);

		JiraIssueTextPersistenceManager.cleanSentenceDatabase("TEST");

		PartOfJiraIssueText element = (PartOfJiraIssueText) new JiraIssueTextPersistenceManager("")
				.getDecisionKnowledgeElement(id);
		assertNull(element);
	}
}
