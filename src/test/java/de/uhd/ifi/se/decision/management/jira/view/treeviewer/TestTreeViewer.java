package de.uhd.ifi.se.decision.management.jira.view.treeviewer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.issue.comments.CommentManager;
import com.atlassian.jira.user.ApplicationUser;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.AbstractPersistenceManagerForSingleLocation;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.CodeClassPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssueTextPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

public class TestTreeViewer extends TestSetUp {
	private AbstractPersistenceManagerForSingleLocation persistenceManager;

	private TreeViewer treeViewer;
	private FilterSettings filterSettings;

	@Before
	public void setUp() {
		init();
		filterSettings = new FilterSettings("TEST", "");
		filterSettings.setLinkDistance(0);
		Set<String> types = new HashSet<>();
		types.add("Decision");
		filterSettings.setKnowledgeTypes(types);

		treeViewer = new TreeViewer(filterSettings);

		persistenceManager = KnowledgePersistenceManager.getOrCreate("TEST").getJiraIssueManager();
	}

	@Test
	@NonTransactional
	public void testIsMultiple() {
		assertEquals(false, treeViewer.isMultiple());
	}

	@Test
	@NonTransactional
	public void testIsCheckCallBack() {
		assertEquals(true, treeViewer.isCheckCallback());
	}

	@Test
	@NonTransactional
	public void testGetThemes() {
		assertEquals(ImmutableMap.of("icons", true), treeViewer.getThemes());
	}

	@Test
	@NonTransactional
	public void testGetNodes() {
		assertTrue(treeViewer.getNodes().size() > 0);
	}

	@Test
	@NonTransactional
	public void testSetNodes() {
		HashSet<TreeViewerNode> newNodes = new HashSet<TreeViewerNode>();
		treeViewer.setData(newNodes);
		assertEquals(newNodes, treeViewer.getNodes());
	}

	@Test
	@NonTransactional
	public void testGetDataStructureNull() {
		assertEquals(TreeViewerNode.class, treeViewer.getTreeViewerNodeWithChildren(null).getClass());
	}

	@Test(expected = NullPointerException.class)
	@NonTransactional
	public void testGetDataStructureEmpty() {
		KnowledgeElement element = new KnowledgeElement();
		assertNotNull(treeViewer.getTreeViewerNodeWithChildren(element));
	}

	@Test
	@NonTransactional
	public void testGetDataStructureFilled() {
		KnowledgeElement element = persistenceManager.getKnowledgeElement(14);
		assertNotNull(element);
		assertEquals(14, element.getId());
		assertEquals("TEST-14", element.getKey());
		filterSettings.setSelectedElement(element);
		treeViewer = new TreeViewer(filterSettings);
		assertTrue(treeViewer.getTreeViewerNodeWithChildren(element).getId().endsWith("tv14"));
	}

	@Test
	@NonTransactional
	public void testEmptyConstructor() {
		assertNotNull(new TreeViewer());
	}

	@Test
	@NonTransactional
	public void testEmptyTreeViewer() {
		TreeViewer tree = new TreeViewer();
		assertNotNull(tree);
	}

	@Test
	@NonTransactional
	public void testTreeViewerWithComment() {
		List<PartOfJiraIssueText> comment = JiraIssues
				.getSentencesForCommentText("{alternative} This would be a great solution option! {alternative}");
		PartOfJiraIssueText sentence = comment.get(0);
		filterSettings.setSelectedElement(sentence);
		TreeViewer tree = new TreeViewer(filterSettings);
		assertNotNull(tree.getTreeViewerNodeWithChildren(sentence));
	}

	@Test
	@NonTransactional
	public void testTreeViewerForSingleKnowledgeElement() {
		// 1) Check if Tree Element has no Children - Important!
		KnowledgeElement element = persistenceManager.getKnowledgeElement("TEST-14");
		filterSettings.setSelectedElement(element);
		TreeViewer treeViewer = new TreeViewer(filterSettings);
		assertNotNull(treeViewer);
		assertEquals(0, treeViewer.getTreeViewerNodeWithChildren(element).getChildren().size());

		// 2) Add comment to issue
		MutableIssue issue = ComponentAccessor.getIssueManager().getIssueByCurrentKey("TEST-14");
		ComponentAccessor.getCommentManager().deleteCommentsForIssue(issue);
		ApplicationUser currentUser = JiraUsers.SYS_ADMIN.getApplicationUser();
		CommentManager commentManager = ComponentAccessor.getCommentManager();
		Comment comment1 = commentManager.create(issue, currentUser, "This is a testsentence for test purposes", true);

		// 3) Manipulate Sentence object so it will be shown in the tree viewer
		List<PartOfJiraIssueText> sentences = JiraIssueTextPersistenceManager.insertPartsOfComment(comment1);
		// JiraIssueComment comment = new JiraIssueCommentImpl(comment1);
		sentences.get(0).setRelevant(true);
		sentences.get(0).setType(KnowledgeType.ALTERNATIVE);
		element = persistenceManager.getKnowledgeElement(14);
		filterSettings.setSelectedElement(element);
		treeViewer = new TreeViewer(filterSettings);

		// 4) Check if TreeViewer has one element
		assertNotNull(treeViewer);
		assertEquals(1, treeViewer.getNodes().size());

		ComponentAccessor.getCommentManager().deleteCommentsForIssue(issue);
	}

	@Test
	@NonTransactional
	public void testTreeViewerCalledFromTabpanelEmptyData() {
		filterSettings.setSelectedElement("");
		TreeViewer tv = new TreeViewer(filterSettings);
		assertNotNull(tv);
	}

	@Test
	@NonTransactional
	public void testSecondConstructorWithProjectKeyValid() {
		KnowledgeElement classElement;
		CodeClassPersistenceManager ccManager = new CodeClassPersistenceManager("Test");
		classElement = new KnowledgeElement();
		classElement.setProject("TEST");
		classElement.setType("Other");
		classElement.setDescription("TEST-1;");
		classElement.setSummary("TestClass.java");
		ApplicationUser user = JiraUsers.SYS_ADMIN.getApplicationUser();
		classElement = ccManager.insertKnowledgeElement(classElement, user);

		filterSettings.setSelectedElement((KnowledgeElement) null);
		assertNull(filterSettings.getSelectedElement());

		Set<String> types = new HashSet<>();
		types.add("codeClass");
		filterSettings.setKnowledgeTypes(types);
		assertEquals(1, filterSettings.getKnowledgeTypes().size());
		assertEquals("codeClass", filterSettings.getKnowledgeTypes().iterator().next());
		filterSettings.setLinkDistance(0);
		TreeViewer newTreeViewer = new TreeViewer(filterSettings);
		assertNotNull(newTreeViewer);
		assertNotNull(newTreeViewer.getNodes());
	}

}
