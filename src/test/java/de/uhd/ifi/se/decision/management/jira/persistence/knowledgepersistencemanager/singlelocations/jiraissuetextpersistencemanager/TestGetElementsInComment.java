package de.uhd.ifi.se.decision.management.jira.persistence.knowledgepersistencemanager.singlelocations.jiraissuetextpersistencemanager;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssueTextPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import net.java.ao.test.jdbc.NonTransactional;

public class TestGetElementsInComment extends TestSetUp {

	protected static JiraIssueTextPersistenceManager manager;

	@Before
	public void setUp() {
		init();
		manager = new JiraIssueTextPersistenceManager("TEST");
	}

	@Test
	@NonTransactional
	public void testGetElementsInComment() {
		List<PartOfJiraIssueText> comment = JiraIssues.getSentencesForCommentText(
				"some sentence in front. {issue} testobject {issue} some sentence in the back.");
		long commentId = comment.get(0).getCommentId();
		List<PartOfJiraIssueText> listWithObjects = manager.getElementsInComment(commentId);
		assertEquals(3, listWithObjects.size());
	}

}
