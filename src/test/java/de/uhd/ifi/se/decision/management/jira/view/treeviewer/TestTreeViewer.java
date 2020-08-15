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

	private boolean multiple;
	private boolean checkCallback;
	private Map<String, Boolean> themes;
	private Set<Data> data;
	private TreeViewer treeViewer;
	private FilterSettings filterSettings;

	@Before
	public void setUp() {
		init();
		multiple = false;
		checkCallback = true;
		themes = new HashMap<>();
		themes.put("Test", false);
		data = new HashSet<>();
		data.add(new Data());
		treeViewer = new TreeViewer("TEST", KnowledgeType.DECISION);
		treeViewer.setMultiple(multiple);
		treeViewer.setCheckCallback(checkCallback);
		treeViewer.setThemes(themes);
		treeViewer.setData(data);
		persistenceManager = KnowledgePersistenceManager.getOrCreate("TEST").getJiraIssueManager();
		filterSettings = new FilterSettings("TEST", "");
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
		KnowledgeElement element = new KnowledgeElement();
		assertNotNull(treeViewer.getDataStructure(element));
	}

	@Test
	@NonTransactional
	public void testGetDataStructureFilled() {
		KnowledgeElement element = persistenceManager.getKnowledgeElement(14);
		assertNotNull(element);
		assertEquals(14, element.getId());
		assertEquals("TEST-14", element.getKey());
		assertTrue(treeViewer.getDataStructure(element).getId().endsWith("tv14"));
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
		KnowledgeElement element = persistenceManager.getKnowledgeElement(14);
		assertEquals("tv14", tree.getDataStructure(element).getId());
	}

	@Test
	@NonTransactional
	public void testTreeViewerWithComment() {
		List<PartOfJiraIssueText> comment = JiraIssues
				.getSentencesForCommentText("{alternative} This would be a great solution option! {alternative}");
		PartOfJiraIssueText sentence = comment.get(0);
		TreeViewer tree = new TreeViewer(sentence.getProject().getProjectKey(), KnowledgeType.DECISION);
		assertNotNull(tree.getDataStructure(sentence));
	}

	@Test
	@NonTransactional
	public void testTreeViewerCalledFromTabpanel() {
		// 1) Check if Tree Element has no Children - Important!
		KnowledgeElement element = persistenceManager.getKnowledgeElement(14);
		filterSettings.setSelectedElement(element);
		TreeViewer tv = new TreeViewer(filterSettings);
		assertNotNull(tv);
		assertEquals(0, tv.getDataStructure(element).getChildren().size());

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
		tv = new TreeViewer(filterSettings);

		// 4) Check if TreeViewer has one element
		assertNotNull(tv);
		assertEquals(1, tv.getData().size());

		ComponentAccessor.getCommentManager().deleteCommentsForIssue(issue);
	}

	@Test
	@NonTransactional
	public void testTreeViewerCalledFromTabpanelNullData() {
		TreeViewer tv = new TreeViewer(filterSettings);
		assertNotNull(tv);
		assertEquals(tv.getData(), null);
	}

	@Test
	@NonTransactional
	public void testTreeViewerCalledFromTabpanelEmptyData() {
		filterSettings.setSelectedElement("");
		TreeViewer tv = new TreeViewer(filterSettings);
		assertNotNull(tv);
	}

	@Test
	public void testSecondConstructorWithProjectKeyNull() {
		TreeViewer newTreeViewer = new TreeViewer((String) null);
		assertNotNull(newTreeViewer);
	}

	@Test
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
		TreeViewer newTreeViewer = new TreeViewer("TEST");
		assertNotNull(newTreeViewer);
		assertNotNull(newTreeViewer.getData());
	}

}
