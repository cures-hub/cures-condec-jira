package de.uhd.ifi.se.decision.management.jira.view.treeviewer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElementImpl;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.test.TestActiveObjects;

import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.mocks.MockDefaultUserManager;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.AbstractPersistenceStrategy;
import de.uhd.ifi.se.decision.management.jira.persistence.StrategyProvider;
import net.java.ao.EntityManager;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestTreeViewer extends TestSetUp {
	private EntityManager entityManager;
	private AbstractPersistenceStrategy abstractPersistenceStrategy;

	private boolean multiple;
	private boolean checkCallback;
	private Map<String, Boolean> themes;
	private Set<Data> data;
	private TreeViewer treeViewer;

	@Before
	public void setUp() {
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockDefaultUserManager());
		initialization();
		multiple = false;
		checkCallback = true;
		themes = new HashMap<>();
		themes.put("Test", false);
		data = new HashSet<>();
		data.add(new Data());
		treeViewer = new TreeViewer("TEST");
		treeViewer.setMultiple(multiple);
		treeViewer.setCheckCallback(checkCallback);
		treeViewer.setThemes(themes);
		treeViewer.setData(data);
		StrategyProvider strategyProvider = new StrategyProvider();
		abstractPersistenceStrategy = strategyProvider.getPersistenceStrategy("TEST");
	}

	@Test
	public void testIsMultiple() {
		assertEquals(treeViewer.isMultiple(), multiple);
	}

	@Test
	public void testisCheckCallBack() {
		assertEquals(treeViewer.isCheckCallback(), checkCallback);
	}

	@Test
	public void testGetThemes() {
		assertEquals(treeViewer.getThemes(), themes);
	}

	@Test
	public void testGetData() {
		assertEquals(treeViewer.getData(), data);
	}

	@Test
	public void testSetMultiple() {
		treeViewer.setMultiple(true);
		assertEquals(treeViewer.isMultiple(), true);
	}

	@Test
	public void testSetCheckCallback() {
		treeViewer.setCheckCallback(true);
		assertEquals(treeViewer.isCheckCallback(), true);
	}

	@Test
	public void testSetThemes() {
		Map<String, Boolean> newThemes = new ConcurrentHashMap<>();
		treeViewer.setThemes(newThemes);
		assertEquals(treeViewer.getThemes(), newThemes);
	}

	@Test
	public void testSetData() {
		HashSet<Data> newData = new HashSet<>();
		treeViewer.setData(newData);
		assertEquals(treeViewer.getData(), newData);
	}

	@Test
	public void testGetDataStructureNull() {
		assertEquals(Data.class, treeViewer.getDataStructure(null).getClass());
	}

	 @Test (expected = NullPointerException.class)
	 public void testGetDataStructureEmpty() {
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl();
	    System.out.println(treeViewer.getDataStructure(element));
	 }

	@Test
	public void testGetDataStructureFilled() {
        DecisionKnowledgeElement element =
        abstractPersistenceStrategy.getDecisionKnowledgeElement((long) 14);
        assertEquals("14", treeViewer.getDataStructure(element).getId());
	}

	@Test
	public void testEmptyConstructor() {
		assertNotNull(new TreeViewer());
	}

    @Test
    public void testEmptyGraphGetDataStructure() {
	    TreeViewer tree = new TreeViewer();
	    DecisionKnowledgeElement element =
	    abstractPersistenceStrategy.getDecisionKnowledgeElement((long) 14);
	    assertEquals("14", tree.getDataStructure(element).getId());
	}

	@Test
	public void  testConstIssueNullShowFalse(){
		TreeViewer tree = new TreeViewer(null, false);
		assertEquals(0, tree.getIds().size(), 0.0);
	}

	@Test
	public void  testConstIssueNullShowTrue(){
		TreeViewer tree = new TreeViewer(null, true);
		assertEquals(0, tree.getIds().size(), 0.0);
	}

	@Test
	public void  testConstIssueWrongShowFalse(){
		TreeViewer tree = new TreeViewer("false", false);
		assertEquals(0, tree.getIds().size(), 0.0);
	}

	@Test
	public void  testConstIssueWrongShowTrue(){
		TreeViewer tree = new TreeViewer("false", true);
		assertEquals(0, tree.getIds().size(), 0.0);
	}

	//TODO MockCommentManager needs to be implemented
	@Ignore
	public void  testConstIssueFilledShowFalse(){
		TreeViewer tree = new TreeViewer("14", false);
		assertEquals(1, tree.getIds().size(), 0.0);
	}

	//TODO MockCommentManager needs to be implemented
	@Ignore
	public void  testConstIssueFilledShowTrue(){
		TreeViewer tree = new TreeViewer("14", true);
		assertEquals(1, tree.getIds().size(), 0.0);
	}

}
