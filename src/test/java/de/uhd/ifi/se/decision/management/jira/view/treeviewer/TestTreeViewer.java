package de.uhd.ifi.se.decision.management.jira.view.treeviewer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import net.java.ao.test.jdbc.NonTransactional;

public class TestTreeViewer extends TestSetUp {

	private TreeViewer treeViewer;
	private FilterSettings filterSettings;

	@Before
	public void setUp() {
		init();
		filterSettings = new FilterSettings("TEST", "");
		treeViewer = new TreeViewer(filterSettings);
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
	public void testGetGetTreeViewerNodeWithChildrenRootElementNull() {
		assertNull(treeViewer.getTreeViewerNodeWithChildren(null).getElement());
	}

	@Test
	@NonTransactional
	public void testGetDataStructureFilled() {
		KnowledgeElement rootElement = KnowledgeElements.getTestKnowledgeElement();
		filterSettings.setSelectedElementObject(rootElement);
		treeViewer = new TreeViewer(filterSettings);
		assertTrue(treeViewer.getTreeViewerNodeWithChildren(rootElement).getId().endsWith("tv1"));
	}

	@Test
	public void testEmptyConstructor() {
		assertEquals(0, new TreeViewer().getNodes().size());
	}

	@Test
	public void testConstructorFilterSettingsNull() {
		assertEquals(0, new TreeViewer(null).getNodes().size());
	}

	@Test
	@NonTransactional
	public void testTreeViewerWithComment() {
		List<PartOfJiraIssueText> comment = JiraIssues
				.getSentencesForCommentText("{alternative} This would be a great solution option! {alternative}");
		PartOfJiraIssueText sentence = comment.get(0);
		filterSettings.setSelectedElementObject(sentence);
		treeViewer = new TreeViewer(filterSettings);
		assertEquals(1, treeViewer.getNodes().size());
	}

	@Test
	@NonTransactional
	public void testTreeViewerForSingleKnowledgeElement() {
		KnowledgeElement rootElement = KnowledgeElements.getTestKnowledgeElement();
		filterSettings.setSelectedElementObject(rootElement);
		treeViewer = new TreeViewer(filterSettings);
		assertEquals(5, treeViewer.getTreeViewerNodeWithChildren(rootElement).getChildren().size());
	}

	@Test
	@NonTransactional
	public void testSelectedElementInvalid() {
		filterSettings.setSelectedElement("");
		treeViewer = new TreeViewer(filterSettings);
		assertEquals(1, treeViewer.getNodes().size());
	}

	@Test
	@NonTransactional
	public void testLinkDistanceZero() {
		filterSettings.setLinkDistance(0);
		filterSettings.setSelectedElementObject((KnowledgeElement) null);
		treeViewer = new TreeViewer(filterSettings);
		assertEquals(JiraIssues.getTestJiraIssueCount(), treeViewer.getNodes().size());
	}

}
