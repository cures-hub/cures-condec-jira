package de.uhd.ifi.se.decision.management.jira.extraction.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Ignore;
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
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.mocks.MockUserManager;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.persistence.GenericLinkManager;
import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssueCommentPersistenceManager;
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
		return new CommentImpl(comment1, true);
	}

	@Test
	@NonTransactional
	public void testElementExistingInAo() {
		Comment comment = getComment("first Comment");
		long id = ActiveObjectsManager.addNewSentenceintoAo(comment, comment.getIssueId(), 0);
		assertNotNull(new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id));
	}

	@Test
	@NonTransactional
	public void testElementInsertedTwice() {
		Comment comment = getComment("first Comment");
		long id = ActiveObjectsManager.addNewSentenceintoAo(comment, comment.getIssueId(), 0);
		long id2 = ActiveObjectsManager.addNewSentenceintoAo(comment, comment.getIssueId(), 0);
		assertNotNull(new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id));
		assertTrue(id == id2);
	}

	@Test
	@NonTransactional
	public void testUpdateSentenceElement() {
		Comment comment = getComment("first Comment");
		long id = ActiveObjectsManager.addNewSentenceintoAo(comment, comment.getIssueId(), 0);
		Sentence sentence = comment.getSentences().get(0);
		sentence.setType("ALTERNATIVE");
		ActiveObjectsManager.updateSentenceElement(sentence);
		Sentence element = (Sentence) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);
		assertTrue(element.getTypeAsString().equalsIgnoreCase("ALTERNATIVE"));
	}

	@Test
	@NonTransactional
	public void testUpdateKnowledgeType() {
		Comment comment = getComment("first Comment");
		long id = ActiveObjectsManager.addNewSentenceintoAo(comment, comment.getIssueId(), 0);
		Sentence sentence = comment.getSentences().get(0);
		sentence.setType(KnowledgeType.ALTERNATIVE);
		ActiveObjectsManager.updateSentenceElement(sentence);
		Sentence element = (Sentence) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);
		assertTrue(element.getTypeAsString().equalsIgnoreCase("ALTERNATIVE"));
	}

	@Test
	@NonTransactional
	public void testUpdateKnowledgeType2() {
		Comment comment = getComment("first Comment");
		long id = ActiveObjectsManager.addNewSentenceintoAo(comment, comment.getIssueId(), 0);
		Sentence sentence = comment.getSentences().get(0);
		sentence.setType(KnowledgeType.ALTERNATIVE);
		ActiveObjectsManager.setSentenceKnowledgeType(sentence);
		Sentence element = (Sentence) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);
		assertTrue(element.getTypeAsString().equalsIgnoreCase("ALTERNATIVE"));
	}

	@Test
	@NonTransactional
	public void testUpdateKnowledgeType3() {
		Comment comment = getComment("first Comment");
		long id = ActiveObjectsManager.addNewSentenceintoAo(comment, comment.getIssueId(), 0);

		Sentence oldElement = (Sentence) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);
		oldElement.setType(KnowledgeType.ALTERNATIVE);
		new JiraIssueCommentPersistenceManager("").updateDecisionKnowledgeElement(oldElement, null);
		Sentence element = (Sentence) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);
		assertTrue(element.getTypeAsString().equalsIgnoreCase("ALTERNATIVE"));
	}

	@Test
	@NonTransactional
	public void testUpdateKnowledgeTypeWithManualTagged() {
		Comment comment = getComment("{issue} testobject {issue}");
		long id = ActiveObjectsManager.addNewSentenceintoAo(comment, comment.getIssueId(), 0);

		Sentence oldElement = (Sentence) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);
		oldElement.setType(KnowledgeType.ALTERNATIVE);
		new JiraIssueCommentPersistenceManager("").updateDecisionKnowledgeElement(oldElement, null);
		Sentence element = (Sentence) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);
		// Important that sentence object has no tags
		assertEquals("testobject", element.getBody().trim());
		assertEquals(element.getTypeAsString(), KnowledgeType.ALTERNATIVE.toString());
		assertEquals("{Alternative} testobject {Alternative}",
				ComponentAccessor.getCommentManager().getMutableComment((long) 0).getBody());
	}

	@Test
	@NonTransactional
	public void testUpdateKnowledgeTypeWithManualTaggedAndMoreSentences() {
		Comment comment = getComment("some sentence in front. {issue} testobject {issue}");
		long id = ActiveObjectsManager.addNewSentenceintoAo(comment, comment.getIssueId(), 1);

		Sentence oldElement = (Sentence) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);
		oldElement.setType(KnowledgeType.ALTERNATIVE);
		new JiraIssueCommentPersistenceManager("").updateDecisionKnowledgeElement(oldElement, null);
		Sentence element = (Sentence) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);
		assertEquals(element.getBody().trim(), "testobject");
		assertEquals(element.getTypeAsString(), KnowledgeType.ALTERNATIVE.toString());
		assertEquals("some sentence in front. {Alternative} testobject {Alternative}",
				ComponentAccessor.getCommentManager().getMutableComment((long) 0).getBody());
	}

	@Test
	@NonTransactional
	public void testUpdateKnowledgeTypeWithManualTaggedAndMoreSentences2() {
		Comment comment = getComment("some sentence in front. {issue} testobject {issue} some sentence in the back.");
		long id = ActiveObjectsManager.addNewSentenceintoAo(comment, comment.getIssueId(), 1);
		Sentence oldElement = (Sentence) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);
		oldElement.setType(KnowledgeType.ALTERNATIVE);
		new JiraIssueCommentPersistenceManager("").updateDecisionKnowledgeElement(oldElement, null);
		
		Sentence element = (Sentence) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);
		assertEquals(element.getBody(), " testobject ");
		assertEquals(element.getTypeAsString(), KnowledgeType.ALTERNATIVE.toString());
		assertEquals("some sentence in front. {Alternative} testobject {Alternative} some sentence in the back.",
				ComponentAccessor.getCommentManager().getMutableComment((long) 0).getBody());
	}

	@Test
	@NonTransactional
	public void testUpdateKnowledgeTypeWithManualTaggedAndMoreSentences2AndArgument() {
		Comment comment = getComment("some sentence in front. {issue} testobject {issue} some sentence in the back.");
		long id = ActiveObjectsManager.addNewSentenceintoAo(comment, comment.getIssueId(), 1);
		Sentence oldElement = (Sentence) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);
		
		oldElement.setType(KnowledgeType.PRO);
		new JiraIssueCommentPersistenceManager("").updateDecisionKnowledgeElement(oldElement, null);
		Sentence element = (Sentence) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);
		assertEquals(" testobject ", element.getBody());
		assertEquals(element.getTypeAsString(), "Pro");
		assertEquals("some sentence in front. {Pro} testobject {Pro} some sentence in the back.",
				ComponentAccessor.getCommentManager().getMutableComment((long) 0).getBody());
	}

	@Test
	@NonTransactional
	public void testUpdateKnowledgeType3WithArgument() {
		Comment comment = getComment("first Comment");
		long id = ActiveObjectsManager.addNewSentenceintoAo(comment, comment.getIssueId(), 0);

		Sentence oldElement = (Sentence) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);
		oldElement.setType(KnowledgeType.PRO);
		new JiraIssueCommentPersistenceManager("").updateDecisionKnowledgeElement(oldElement, null);
		Sentence element = (Sentence) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);
		assertTrue(element.getTypeAsString().equalsIgnoreCase("Pro"));
	}

	@Test
	@NonTransactional
	public void testSetRelevantIntoAO() {
		Comment comment = getComment("first Comment");
		long id = ActiveObjectsManager.addNewSentenceintoAo(comment, comment.getIssueId(), 0);

		ActiveObjectsManager.setIsRelevantIntoAo(id, true);
		Sentence element = (Sentence) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);
		assertTrue(element.isRelevant());

		ActiveObjectsManager.setIsRelevantIntoAo(id, false);
		element = (Sentence) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);
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
	@Ignore
	@NonTransactional
	public void testCommentHasChanged() {
		Comment comment = getComment("first Comment");
		long id = ActiveObjectsManager.addNewSentenceintoAo(comment, comment.getIssueId(), 0);

		Comment comment2 = getComment("secondComment with more text");

		comment2.setJiraCommentId(comment.getJiraCommentId());

		// ActiveObjectsManager.deleteCommentsSentences(comment)

		Sentence element = (Sentence) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);

		assertNull(element);
	}

	@Test
	@NonTransactional
	public void testSetSentenceIrrlevant() {
		Comment comment = getComment("first Comment");
		long id = ActiveObjectsManager.addNewSentenceintoAo(comment, comment.getIssueId(), 0);

		ActiveObjectsManager.setIsRelevantIntoAo(id, true);
		Sentence element = (Sentence) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);
		assertTrue(element.isRelevant());

		ActiveObjectsManager.setSentenceIrrelevant(id, true);
		element = (Sentence) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);
		assertFalse(element.isRelevant());
		assertTrue(element.isTagged());
		assertTrue(element.getTypeAsString().equalsIgnoreCase("Other"));
	}

	@Test
	@NonTransactional
	public void testUpdateSentenceBodyWhenCommentChanged() {
		Comment comment = getComment("First sentences of two. Sencond sentences of two.");
		long id = ActiveObjectsManager.addNewSentenceintoAo(comment, comment.getIssueId(), 0);
		long id2 = ActiveObjectsManager.addNewSentenceintoAo(comment, comment.getIssueId(), 1);

		JiraIssueCommentPersistenceManager.updateSentenceBodyWhenCommentChanged(comment.getJiraCommentId(), id,
				"secondComment with more text");

		Sentence element = (Sentence) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id2);
		assertTrue(element.getEndSubstringCount() != comment.getEndSubstringCount().get(1));
	}

	@Test
	@NonTransactional
	public void testGetElementsForIssue() {
		Comment comment = getComment("some sentence in front. {issue} testobject {issue} some sentence in the back.");
		ActiveObjectsManager.addNewSentenceintoAo(comment, comment.getIssueId(), 1);

		List<DecisionKnowledgeElement> listWithObjects = ActiveObjectsManager.getElementsForIssue(comment.getIssueId(),
				"TEST");
		assertEquals(3, listWithObjects.size());
	}

	@Test
	@NonTransactional
	public void testGetAllElementsFromAoByType() {
		Comment comment = getComment("some sentence in front. {issue} testobject {issue} some sentence in the back.");
		ActiveObjectsManager.addNewSentenceintoAo(comment, comment.getIssueId(), 1);

		List<DecisionKnowledgeElement> listWithObjects = ActiveObjectsManager.getAllElementsFromAoByType("TEST",
				KnowledgeType.ISSUE);
		assertEquals(1, listWithObjects.size());
	}

	@Test
	@NonTransactional
	public void testGetAllElementsFromAoByArgumentType() {
		Comment comment = getComment("some sentence in front. {pro} testobject {pro} some sentence in the back.");
		ActiveObjectsManager.addNewSentenceintoAo(comment, comment.getIssueId(), 1);

		List<DecisionKnowledgeElement> listWithObjects = ActiveObjectsManager.getAllElementsFromAoByType("TEST",
				KnowledgeType.ARGUMENT);
		assertEquals(1, listWithObjects.size());
	}

	@Test
	@NonTransactional
	public void testGetAllElementsFromAoByEmptyType() {
		Comment comment = getComment("some sentence in front.  {pro} testobject {pro} some sentence in the back.");
		ActiveObjectsManager.addNewSentenceintoAo(comment, comment.getIssueId(), 1);

		List<DecisionKnowledgeElement> listWithObjects = ActiveObjectsManager.getAllElementsFromAoByType("TEST",
				KnowledgeType.OTHER);
		assertEquals(0, listWithObjects.size());
	}

	@Test
	@NonTransactional
	public void testCleanSentenceDatabaseForProject() {
		Comment comment = getComment("some sentence in front.  {pro} testobject {pro} some sentence in the back.");
		long id = ActiveObjectsManager.addNewSentenceintoAo(comment, comment.getIssueId(), 1);

		MutableComment comment2 = ComponentAccessor.getCommentManager().getMutableComment(comment.getIssueId());
		ComponentAccessor.getCommentManager().delete(comment2);

		ActiveObjectsManager.cleanSentenceDatabaseForProject("TEST");

		Sentence element = (Sentence) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);
		assertNull(element);
	}

	@Test
	@NonTransactional
	public void testLinkAllUnlikedSentence() {
		Comment comment = getComment("some sentence in front.  {pro} testobject {pro} some sentence in the back.");
		long id = ActiveObjectsManager.addNewSentenceintoAo(comment, comment.getIssueId(), 1);
		assertEquals(1, GenericLinkManager.getLinksForElement("s" + id).size());
		GenericLinkManager.deleteLinksForElement("s" + id);
		assertEquals(0, GenericLinkManager.getLinksForElement("s" + id).size());
		ActiveObjectsManager.createLinksForNonLinkedElementsForProject("TEST");
		assertEquals(1, GenericLinkManager.getLinksForElement("s" + id).size());
	}

	@Test
	@NonTransactional
	public void testSmartLinkingForProAlternative() {
		Comment comment = getComment("{alternative}first sentence{alternative} {pro}second sentence{pro}");
		Link sentenceLink = GenericLinkManager.getLinksForElement("s" + comment.getSentences().get(1).getId()).get(0);
		assertEquals(sentenceLink.getOppositeElement(comment.getSentences().get(0)).getId(),
				comment.getSentences().get(1).getId());
	}

	@Test
	@NonTransactional
	public void testSmartLinkingForConAlternative() {
		Comment comment = getComment("{alternative}first sentence{alternative} {con}second sentence{con}");
		Link sentenceLink = GenericLinkManager.getLinksForElement("s" + comment.getSentences().get(1).getId()).get(0);
		assertEquals(sentenceLink.getOppositeElement(comment.getSentences().get(0)).getId(),
				comment.getSentences().get(1).getId());
	}

	@Test
	@NonTransactional
	public void testSmartLinkingForProDecision() {
		Comment comment = getComment("{decision}first sentence{decision} {pro}second sentence{pro}");
		Link sentenceLink = GenericLinkManager.getLinksForElement("s" + comment.getSentences().get(1).getId()).get(0);
		assertEquals(sentenceLink.getOppositeElement(comment.getSentences().get(0)).getId(),
				comment.getSentences().get(1).getId());
	}

	@Test
	@NonTransactional
	public void testSmartLinkingForConDecision() {
		Comment comment = getComment("{decision}first sentence{decision} {con}second sentence{con}");
		Link sentenceLink = GenericLinkManager.getLinksForElement("s" + comment.getSentences().get(1).getId()).get(0);
		assertEquals(sentenceLink.getOppositeElement(comment.getSentences().get(0)).getId(),
				comment.getSentences().get(1).getId());
	}

	@Test
	@NonTransactional
	public void testSmartLinkingForAlternativeIssue() {
		Comment comment = getComment("{issue}first sentence{issue} {alternative}second sentence{alternative}");
		Link sentenceLink = GenericLinkManager.getLinksForElement("s" + comment.getSentences().get(1).getId()).get(0);
		assertEquals(sentenceLink.getOppositeElement(comment.getSentences().get(0)).getId(),
				comment.getSentences().get(1).getId());
	}

	@Test
	@NonTransactional
	public void testSmartLinkingForDecisionIssue() {
		Comment comment = getComment("{issue}first sentence{issue} {decision}second sentence{decision}");
		Link sentenceLink = GenericLinkManager.getLinksForElement("s" + comment.getSentences().get(1).getId()).get(0);
		assertEquals(sentenceLink.getOppositeElement(comment.getSentences().get(0)).getId(),
				comment.getSentences().get(1).getId());
	}

	@Test
	@NonTransactional
	public void testSmartLinkingForBoringNonSmartLink() {
		Comment comment = getComment("{issue}first sentence{issue} {pro}second sentence{pro}");
		Link sentenceLink = GenericLinkManager.getLinksForElement("s" + comment.getSentences().get(1).getId()).get(0);
		assertEquals("s2 to i30", sentenceLink.toString());
	}
}
