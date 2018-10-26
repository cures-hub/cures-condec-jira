package de.uhd.ifi.se.decision.management.jira.extraction.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.comments.CommentManager;
import com.atlassian.jira.issue.comments.MutableComment;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.issuetype.MockIssueType;
import com.atlassian.jira.mock.issue.MockIssue;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.extraction.model.Comment;
import de.uhd.ifi.se.decision.management.jira.extraction.model.Sentence;
import de.uhd.ifi.se.decision.management.jira.extraction.model.impl.CommentImpl;
import de.uhd.ifi.se.decision.management.jira.extraction.model.impl.SentenceImpl;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.mocks.MockUserManager;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.jdbc.NonTransactional;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(TestSetUpWithIssues.AoSentenceTestDatabaseUpdater.class)
public class TestActiveObjectsManager extends TestSetUpWithIssues {

	private EntityManager entityManager;
	private MockIssue issue;

	private com.atlassian.jira.issue.comments.Comment comment1;

	@Before
	public void setUp() {
		initialization();
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockUserManager());
		createLocalIssue();
	}

	private void createLocalIssue() {
		Project project = ComponentAccessor.getProjectManager().getProjectByCurrentKey("TEST");
		issue = new MockIssue(30, "TEST-" + 30);
		((MockIssue) issue).setProjectId(project.getId());
		issue.setProjectObject(project);
		IssueType issueType = new MockIssueType(1, KnowledgeType.DECISION.toString().toLowerCase(Locale.ENGLISH));
		issue.setIssueType(issueType);
		issue.setSummary("Test");
	}

	private void addCommentsToIssue(String comment) {

		ComponentAccessor.getCommentManager().deleteCommentsForIssue(issue);
		// Get the current logged in user
		ApplicationUser currentUser = ComponentAccessor.getUserManager().getUserByName("NoFails");
		// Get access to the Jira comment and component manager
		CommentManager commentManager = ComponentAccessor.getCommentManager();
		// Get the last comment entered in on the issue to a String
		comment1 = commentManager.create(issue, currentUser, comment, true);
	}

	private CommentImpl getComment(String text) {
		createLocalIssue();

		addCommentsToIssue(text);
		return new CommentImpl(comment1);
	}

	@Test
	@NonTransactional
	public void testElementExistingInAo() {
		Comment comment = getComment("first Comment");
		long id = ActiveObjectsManager.addNewSentenceintoAo(comment, comment.getIssueId(), 0);
		assertNotNull(ActiveObjectsManager.getElementFromAO(id));
	}

	@Test
	@NonTransactional
	public void testElementInsertedTwice() {
		Comment comment = getComment("first Comment");
		long id = ActiveObjectsManager.addNewSentenceintoAo(comment, comment.getIssueId(), 0);
		long id2 = ActiveObjectsManager.addNewSentenceintoAo(comment, comment.getIssueId(), 0);
		assertNotNull(ActiveObjectsManager.getElementFromAO(id));
		assertTrue(id == id2);
	}

	@Test
	@NonTransactional
	public void testUpdateSentenceElement() {
		Comment comment = getComment("first Comment");
		long id = ActiveObjectsManager.addNewSentenceintoAo(comment, comment.getIssueId(), 0);
		Sentence sentence = comment.getSentences().get(0);
		sentence.setKnowledgeTypeString("ALTERNATIVE");
		ActiveObjectsManager.updateSentenceElement(sentence);
		DecisionKnowledgeInCommentEntity element = ActiveObjectsManager.getElementFromAO(id);
		assertTrue(element.getKnowledgeTypeString().equalsIgnoreCase("ALTERNATIVE"));
	}

	@Test
	@NonTransactional
	public void testUpdateKnowledgeType() {
		Comment comment = getComment("first Comment");
		long id = ActiveObjectsManager.addNewSentenceintoAo(comment, comment.getIssueId(), 0);
		Sentence sentence = comment.getSentences().get(0);
		sentence.setType(KnowledgeType.ALTERNATIVE);
		ActiveObjectsManager.updateSentenceElement(sentence);
		DecisionKnowledgeInCommentEntity element = ActiveObjectsManager.getElementFromAO(id);
		assertTrue(element.getKnowledgeTypeString().equalsIgnoreCase("ALTERNATIVE"));
	}

	@Test
	@NonTransactional
	public void testUpdateKnowledgeType2() {
		Comment comment = getComment("first Comment");
		long id = ActiveObjectsManager.addNewSentenceintoAo(comment, comment.getIssueId(), 0);
		Sentence sentence = comment.getSentences().get(0);
		sentence.setType(KnowledgeType.ALTERNATIVE);
		ActiveObjectsManager.setSentenceKnowledgeType(sentence);
		DecisionKnowledgeInCommentEntity element = ActiveObjectsManager.getElementFromAO(id);
		assertTrue(element.getKnowledgeTypeString().equalsIgnoreCase("ALTERNATIVE"));
	}

	@Test
	@NonTransactional
	public void testUpdateKnowledgeType3() {
		Comment comment = getComment("first Comment");
		long id = ActiveObjectsManager.addNewSentenceintoAo(comment, comment.getIssueId(), 0);

		ActiveObjectsManager.updateKnowledgeTypeOfSentence(id, KnowledgeType.ALTERNATIVE, "");
		DecisionKnowledgeInCommentEntity element = ActiveObjectsManager.getElementFromAO(id);
		assertTrue(element.getKnowledgeTypeString().equalsIgnoreCase("ALTERNATIVE"));
	}

	@Test
	@NonTransactional
	public void testUpdateKnowledgeTypeWithManualTagged() {
		Comment comment = getComment("[issue] testobject [/issue]");
		long id = ActiveObjectsManager.addNewSentenceintoAo(comment, comment.getIssueId(), 0);

		ActiveObjectsManager.updateKnowledgeTypeOfSentence(id, KnowledgeType.ALTERNATIVE, "");
		DecisionKnowledgeInCommentEntity element = ActiveObjectsManager.getElementFromAO(id);
		assertEquals(new SentenceImpl(element.getId()).getBody().trim(), "testobject");
		assertEquals(element.getKnowledgeTypeString(), KnowledgeType.ALTERNATIVE.toString());
		assertEquals("[Alternative] testobject [/Alternative]",
				ComponentAccessor.getCommentManager().getMutableComment((long) 0).getBody());
	}

	@Test
	@NonTransactional
	public void testUpdateKnowledgeTypeWithManualTaggedAndMoreSentences() {
		Comment comment = getComment("some sentence in front. [issue] testobject [/issue]");
		long id = ActiveObjectsManager.addNewSentenceintoAo(comment, comment.getIssueId(), 1);

		ActiveObjectsManager.updateKnowledgeTypeOfSentence(id, KnowledgeType.ALTERNATIVE, "");
		DecisionKnowledgeInCommentEntity element = ActiveObjectsManager.getElementFromAO(id);
		assertEquals(new SentenceImpl(element.getId()).getBody().trim(), "testobject");
		assertEquals(element.getKnowledgeTypeString(), KnowledgeType.ALTERNATIVE.toString());
		assertEquals("some sentence in front. [Alternative] testobject [/Alternative]",
				ComponentAccessor.getCommentManager().getMutableComment((long) 0).getBody());
	}

	@Test
	@NonTransactional
	public void testUpdateKnowledgeTypeWithManualTaggedAndMoreSentences2() {
		Comment comment = getComment("some sentence in front. [issue] testobject [/issue] some sentence in the back.");
		long id = ActiveObjectsManager.addNewSentenceintoAo(comment, comment.getIssueId(), 1);

		ActiveObjectsManager.updateKnowledgeTypeOfSentence(id, KnowledgeType.ALTERNATIVE, "");
		DecisionKnowledgeInCommentEntity element = ActiveObjectsManager.getElementFromAO(id);
		assertEquals(new SentenceImpl(element.getId()).getBody().trim(), "testobject");
		assertEquals(element.getKnowledgeTypeString(), KnowledgeType.ALTERNATIVE.toString());
		assertEquals("some sentence in front. [Alternative] testobject [/Alternative] some sentence in the back.",
				ComponentAccessor.getCommentManager().getMutableComment((long) 0).getBody());
	}

	@Test
	@NonTransactional
	public void testUpdateKnowledgeTypeWithManualTaggedAndMoreSentences2AndArgument() {
		Comment comment = getComment("some sentence in front. [issue] testobject [/issue] some sentence in the back.");
		long id = ActiveObjectsManager.addNewSentenceintoAo(comment, comment.getIssueId(), 1);
		ActiveObjectsManager.updateKnowledgeTypeOfSentence(id, KnowledgeType.OTHER, "Pro");
		DecisionKnowledgeInCommentEntity element = ActiveObjectsManager.getElementFromAO(id);
		assertEquals(new SentenceImpl(element.getId()).getBody().trim(), "testobject");
		assertEquals(element.getKnowledgeTypeString(), "Pro");
		assertEquals("some sentence in front. [Pro] testobject [/Pro] some sentence in the back.",
				ComponentAccessor.getCommentManager().getMutableComment((long) 0).getBody());
	}

	@Test
	@NonTransactional
	public void testUpdateKnowledgeType3WithArgument() {
		Comment comment = getComment("first Comment");
		long id = ActiveObjectsManager.addNewSentenceintoAo(comment, comment.getIssueId(), 0);

		ActiveObjectsManager.updateKnowledgeTypeOfSentence(id, KnowledgeType.ARGUMENT, "Pro");
		DecisionKnowledgeInCommentEntity element = ActiveObjectsManager.getElementFromAO(id);
		assertTrue(element.getKnowledgeTypeString().equalsIgnoreCase("Pro"));
		assertTrue(element.getArgument().equalsIgnoreCase("Pro"));
	}

	@Test
	@NonTransactional
	public void testSetRelevantIntoAO() {
		Comment comment = getComment("first Comment");
		long id = ActiveObjectsManager.addNewSentenceintoAo(comment, comment.getIssueId(), 0);

		ActiveObjectsManager.setIsRelevantIntoAo(id, true);
		DecisionKnowledgeInCommentEntity element = ActiveObjectsManager.getElementFromAO(id);
		assertTrue(element.isRelevant());

		ActiveObjectsManager.setIsRelevantIntoAo(id, false);
		element = ActiveObjectsManager.getElementFromAO(id);
		assertFalse(element.isRelevant());
	}

	@Test
	@NonTransactional
	public void testSetRelevantIntoAOForNonExistingElement() {
		Comment comment = getComment("first Comment");
		long id = ActiveObjectsManager.addNewSentenceintoAo(comment, comment.getIssueId(), 0);

		assertFalse(ActiveObjectsManager.setIsRelevantIntoAo(id + 2, true));

	}

	@Test
	@NonTransactional
	public void testCommentHasChanged() {
		Comment comment = getComment("first Comment");
		long id = ActiveObjectsManager.addNewSentenceintoAo(comment, comment.getIssueId(), 0);

		Comment comment2 = getComment("secondComment with more text");

		comment2.setJiraCommentId(comment.getJiraCommentId());

		ActiveObjectsManager.checkIfCommentBodyHasChangedOutsideOfPlugin(comment2);

		DecisionKnowledgeInCommentEntity element = ActiveObjectsManager.getElementFromAO(id);
		// Check if new SentenceInstance is returned
		assertTrue(element.getEndSubstringCount() == 0);
	}

	@Test
	@NonTransactional
	public void testSetSentenceIrrlevant() {
		Comment comment = getComment("first Comment");
		long id = ActiveObjectsManager.addNewSentenceintoAo(comment, comment.getIssueId(), 0);

		ActiveObjectsManager.setIsRelevantIntoAo(id, true);
		DecisionKnowledgeInCommentEntity element = ActiveObjectsManager.getElementFromAO(id);
		assertTrue(element.isRelevant());

		ActiveObjectsManager.setSentenceIrrelevant(id, true);
		element = ActiveObjectsManager.getElementFromAO(id);
		assertTrue(element.getArgument().equals(""));
		assertFalse(element.isRelevant());
		assertTrue(element.isTaggedManually());
		assertTrue(element.getKnowledgeTypeString().equalsIgnoreCase("Other"));
	}

	@Test
	@NonTransactional
	public void testUpdateSentenceBodyWhenCommentChanged() {
		Comment comment = getComment("First sentences of two. Sencond sentences of two.");
		long id = ActiveObjectsManager.addNewSentenceintoAo(comment, comment.getIssueId(), 0);
		long id2 = ActiveObjectsManager.addNewSentenceintoAo(comment, comment.getIssueId(), 1);

		ActiveObjectsManager.updateSentenceBodyWhenCommentChanged(comment.getJiraCommentId(), id,
				"secondComment with more text");

		DecisionKnowledgeInCommentEntity element = ActiveObjectsManager.getElementFromAO(id2);
		assertTrue(element.getEndSubstringCount() != comment.getEndSubstringCount().get(1));
	}

	@Test
	@NonTransactional
	public void testGetElementsForIssue() {
		Comment comment = getComment("some sentence in front. [issue] testobject [/issue] some sentence in the back.");
		ActiveObjectsManager.addNewSentenceintoAo(comment, comment.getIssueId(), 1);

		List<DecisionKnowledgeElement> listWithObjects = ActiveObjectsManager.getElementsForIssue(comment.getIssueId(),
				"TEST");
		assertEquals(3, listWithObjects.size());
	}

	@Test
	@NonTransactional
	public void testGetAllElementsFromAoByType() {
		Comment comment = getComment("some sentence in front. [issue] testobject [/issue] some sentence in the back.");
		ActiveObjectsManager.addNewSentenceintoAo(comment, comment.getIssueId(), 1);

		List<DecisionKnowledgeElement> listWithObjects = ActiveObjectsManager.getAllElementsFromAoByType("TEST",
				KnowledgeType.ISSUE);
		assertEquals(1, listWithObjects.size());
	}

	@Test
	@NonTransactional
	public void testGetAllElementsFromAoByArgumentType() {
		Comment comment = getComment("some sentence in front. [pro] testobject [/pro] some sentence in the back.");
		ActiveObjectsManager.addNewSentenceintoAo(comment, comment.getIssueId(), 1);

		List<DecisionKnowledgeElement> listWithObjects = ActiveObjectsManager.getAllElementsFromAoByType("TEST",
				KnowledgeType.ARGUMENT);
		assertEquals(1, listWithObjects.size());
	}

	@Test
	@NonTransactional
	public void testGetAllElementsFromAoByEmptyType() {
		Comment comment = getComment("some sentence in front. [pro] testobject [/pro] some sentence in the back.");
		ActiveObjectsManager.addNewSentenceintoAo(comment, comment.getIssueId(), 1);

		List<DecisionKnowledgeElement> listWithObjects = ActiveObjectsManager.getAllElementsFromAoByType("TEST",
				KnowledgeType.OTHER);
		assertEquals(0, listWithObjects.size());
	}

	@Test
	@NonTransactional
	public void testCleanSentenceDatabaseForProject() {
		Comment comment = getComment("some sentence in front. [pro] testobject [/pro] some sentence in the back.");
		long id = ActiveObjectsManager.addNewSentenceintoAo(comment, comment.getIssueId(), 1);

		MutableComment comment2 = ComponentAccessor.getCommentManager().getMutableComment(comment.getIssueId());
		ComponentAccessor.getCommentManager().delete(comment2);

		ActiveObjectsManager.cleanSentenceDatabaseForProject("TEST");

		DecisionKnowledgeInCommentEntity dBElement = ActiveObjectsManager.getElementFromAO(id);
		assertNotNull(dBElement);
		// Is unequal because new empty sentence is returned
		assertFalse(dBElement.getId() == id);
	}

}
