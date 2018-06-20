package de.uhd.ifi.se.decision.documentation.jira.view.treeviewer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import de.uhd.ifi.se.decision.documentation.jira.persistence.AbstractPersistenceStrategy;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.test.TestActiveObjects;

import de.uhd.ifi.se.decision.documentation.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.documentation.jira.TestSetUp;
import de.uhd.ifi.se.decision.documentation.jira.mocks.MockDefaultUserManager;
import de.uhd.ifi.se.decision.documentation.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.documentation.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.documentation.jira.model.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.documentation.jira.persistence.StrategyProvider;
import net.java.ao.EntityManager;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestTreeViewer extends TestSetUp {
	private EntityManager entityManager;
	private AbstractPersistenceStrategy abstractPersistenceStrategy;

	private boolean multiple;
	private boolean checkCallback;
	private Map<String, Boolean> themes;
	private HashSet<Data> data;
	private TreeViewer treeViewer;

	@Before
	public void setUp() {
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

		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockDefaultUserManager());
		initialization();
		StrategyProvider strategyProvider = new StrategyProvider();
		abstractPersistenceStrategy = strategyProvider.getStrategy("TEST");
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
	public void testsetMultiple() {
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

	@Test
	public void testGetDataStructureEmpty() {
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl();
		System.out.println(treeViewer.getDataStructure(element));
	}

	@Test
	public void testGetDataStructureFilled() {
		DecisionKnowledgeElement element = abstractPersistenceStrategy.getDecisionKnowledgeElement((long) 14);
		assertEquals("14", treeViewer.getDataStructure(element).getId());
	}

	@Test
	public void testEmptyConstructor() {
		assertNotNull(new TreeViewer());
	}

	@Test
	public void testEmptyGraphGetDataStructure() {
		TreeViewer tree = new TreeViewer();
		DecisionKnowledgeElement element = abstractPersistenceStrategy.getDecisionKnowledgeElement((long) 14);
		assertEquals("14", tree.getDataStructure(element).getId());
	}

}
