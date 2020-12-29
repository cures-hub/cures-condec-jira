package de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.jiraissuetextpersistencemanager;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssueTextPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

public class TestDeleteElementsOfProject extends TestSetUp {

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
	public void testCommentFilledAndElementsInDatabase() {
		List<PartOfJiraIssueText> comment = JiraIssues.getSentencesForCommentText(
				"some sentence in front. {issue} testobject {issue} some sentence in the back.");
		PartOfJiraIssueText sentence = comment.get(1);
		sentence.setId(4);
		sentence.setCommentId(0);
		manager.insertKnowledgeElement(sentence, user);

		int numberOfNodesInGraph = KnowledgeGraph.getOrCreate("TEST").vertexSet().size();
		assertTrue(manager.getKnowledgeElements().size() > 0);
		assertTrue(manager.deleteElementsOfProject());
		assertEquals(0, manager.getKnowledgeElements().size());
		assertTrue(KnowledgeGraph.getOrCreate("TEST").vertexSet().size() < numberOfNodesInGraph);
	}
}
