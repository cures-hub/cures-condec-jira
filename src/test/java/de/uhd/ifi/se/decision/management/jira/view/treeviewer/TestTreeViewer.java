package de.uhd.ifi.se.decision.management.jira.view.treeviewer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.issue.comments.CommentManager;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.extraction.TestTextSplitter;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.impl.KnowledgeGraphImpl;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.AbstractPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssueTextPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

public class TestTreeViewer extends TestSetUp {
	private AbstractPersistenceManager persistenceStrategy;

	private boolean multiple;
	private boolean checkCallback;
	private Map<String, Boolean> themes;
	private Set<Data> data;
	private TreeViewer treeViewer;
	private Boolean[] selectedKnowledgeTypes = { true, true, true, true, true };

	@Before
	public void setUp() {
		init();
		multiple = false;
		checkCallback = true;
		themes = new HashMap<>();
		themes.put("Test", false);
		data = new HashSet<Data>();
		data.add(new Data());
		treeViewer = new TreeViewer("TEST");
		treeViewer.setMultiple(multiple);
		treeViewer.setCheckCallback(checkCallback);
		treeViewer.setThemes(themes);
		treeViewer.setData(data);
		persistenceStrategy = AbstractPersistenceManager.getDefaultPersistenceStrategy("TEST");
	}

	@Test
	@NonTransactional
	public void testIsMultiple() {
		assertEquals(treeViewer.isMultiple(), multiple);
	}

	@Test
	@NonTransactional
	public void testisCheckCallBack() {
		assertEquals(treeViewer.isCheckCallback(), checkCallback);
	}

	@Test
	@NonTransactional
	public void testGetThemes() {
		assertEquals(treeViewer.getThemes(), themes);
	}

	@Test
	@NonTransactional
	public void testGetData() {
		assertEquals(treeViewer.getData(), data);
	}

	@Test
	@NonTransactional
	public void testSetMultiple() {
		treeViewer.setMultiple(true);
		assertEquals(treeViewer.isMultiple(), true);
	}

	@Test
	@NonTransactional
	public void testSetCheckCallback() {
		treeViewer.setCheckCallback(true);
		assertEquals(treeViewer.isCheckCallback(), true);
	}

	@Test
	@NonTransactional
	public void testSetThemes() {
		Map<String, Boolean> newThemes = new ConcurrentHashMap<>();
		treeViewer.setThemes(newThemes);
		assertEquals(treeViewer.getThemes(), newThemes);
	}

	@Test
	@NonTransactional
	public void testSetData() {
		HashSet<Data> newData = new HashSet<Data>();
		treeViewer.setData(newData);
		assertEquals(treeViewer.getData(), newData);
	}

	@Test
	@NonTransactional
	public void testGetDataStructureNull() {
		assertEquals(Data.class, treeViewer.getDataStructure(null).getClass());
	}

	@Test(expected = NullPointerException.class)
	@NonTransactional
	public void testGetDataStructureEmpty() {
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl();
		System.out.println(treeViewer.getDataStructure(element));
	}

	@Test
	@NonTransactional
	public void testGetDataStructureFilled() {
		DecisionKnowledgeElement element = persistenceStrategy.getDecisionKnowledgeElement(14);
		assertNotNull(element);
		assertEquals(14, element.getId());
		assertEquals("TEST-14", element.getKey());
		assertEquals("tv14", treeViewer.getDataStructure(element).getId());
	}

	@Test
	@NonTransactional
	public void testEmptyConstructor() {
		assertNotNull(new TreeViewer());
	}

	@Test
	@NonTransactional
	public void testEmptyGraphGetDataStructure() {
		TreeViewer tree = new TreeViewer();
		DecisionKnowledgeElement element = persistenceStrategy.getDecisionKnowledgeElement(14);
		assertEquals("tv14", tree.getDataStructure(element).getId());
	}

	@Test
	@NonTransactional
	public void testTreeViewerWithComment() {
		List<PartOfJiraIssueText> comment = TestTextSplitter
				.getSentencesForCommentText("This is a testcomment with some text");
		PartOfJiraIssueText sentence = comment.get(0);

		sentence.setType(KnowledgeType.ALTERNATIVE);
		sentence.setRelevant(true);
		String projectKey = sentence.getProject().getProjectKey();
		assertEquals("TEST", projectKey);
		DecisionKnowledgeElement element = (DecisionKnowledgeElement) sentence;
		assertTrue(((PartOfJiraIssueText) element).isRelevant());

		AbstractPersistenceManager jiraIssueCommentPersistenceManager = new JiraIssueTextPersistenceManager(projectKey);
		assertTrue(jiraIssueCommentPersistenceManager.getDecisionKnowledgeElements().contains(sentence));

		KnowledgeGraph graph = new KnowledgeGraphImpl(projectKey);
		assertTrue(graph.containsVertex(sentence));

		TreeViewer tree = new TreeViewer(projectKey, KnowledgeType.ALTERNATIVE);
		assertNotNull(tree.getDataStructure((DecisionKnowledgeElement) sentence));
	}

	@Test
	@NonTransactional
	public void testTreeViewerCalledFromTabpanel() {
		// 1) Check if Tree Element has no Children - Important!
		DecisionKnowledgeElement element = persistenceStrategy.getDecisionKnowledgeElement(14);
		TreeViewer tv = new TreeViewer(element.getKey(), selectedKnowledgeTypes);
		assertNotNull(tv);
		assertEquals(0, tv.getDataStructure(element).getChildren().size());

		// 2) Add comment to issue
		MutableIssue issue = ComponentAccessor.getIssueManager().getIssueByCurrentKey("TEST-14");
		ComponentAccessor.getCommentManager().deleteCommentsForIssue(issue);
		ApplicationUser currentUser = JiraUsers.SYS_ADMIN.getApplicationUser();
		CommentManager commentManager = ComponentAccessor.getCommentManager();
		Comment comment1 = commentManager.create(issue, currentUser, "This is a testsentence for test purposes", true);

		// 3) Manipulate Sentence object so it will be shown in the tree viewer
		List<PartOfJiraIssueText> sentences = JiraIssueTextPersistenceManager.getPartsOfComment(comment1);
		// JiraIssueComment comment = new JiraIssueCommentImpl(comment1);
		sentences.get(0).setRelevant(true);
		sentences.get(0).setType(KnowledgeType.ALTERNATIVE);
		element = persistenceStrategy.getDecisionKnowledgeElement(14);
		tv = new TreeViewer(element.getKey(), selectedKnowledgeTypes);

		// 4) Check if TreeViewer has one element
		assertNotNull(tv);
		assertEquals(1, tv.getData().size());

		ComponentAccessor.getCommentManager().deleteCommentsForIssue(issue);
	}

	@Test
	@NonTransactional
	public void testTreeViewerCalledFromTabpanelNullData() {
		TreeViewer tv = new TreeViewer(null, selectedKnowledgeTypes);
		assertNotNull(tv);
		assertEquals(tv.getData(), null);
	}

	@Test
	@NonTransactional
	public void testTreeViewerCalledFromTabpanelEmptyData() {
		TreeViewer tv = new TreeViewer("", selectedKnowledgeTypes);
		assertNotNull(tv);
	}

}
