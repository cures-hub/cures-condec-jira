package de.uhd.ifi.se.decision.management.jira.view.treant;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.test.TestActiveObjects;

import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.mocks.MockDefaultUserManager;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.LinkImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.AbstractPersistenceStrategy;
import de.uhd.ifi.se.decision.management.jira.persistence.StrategyProvider;
import net.java.ao.EntityManager;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestTreant extends TestSetUp {
	private EntityManager entityManager;
	private Chart chart;
	private Node nodeStructure;
	private Treant treant;
	private AbstractPersistenceStrategy persistenceStrategy;

	@Before
	public void setUp() {
		this.chart = new Chart();
		this.nodeStructure = new Node();
		this.treant = new Treant();
		this.treant.setChart(chart);
		this.treant.setNodeStructure(nodeStructure);
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockDefaultUserManager());
		initialization();
		StrategyProvider strategyProvider = new StrategyProvider();
		persistenceStrategy = strategyProvider.getPersistenceStrategy("TEST");
	}

	@Test
	public void testGetChart() {
		assertEquals(this.chart, this.treant.getChart());
	}

	@Test
	public void testGetNodeStructure() {
		assertEquals(this.nodeStructure, this.treant.getNodeStructure());
	}

	@Test
	public void testSetChart() {
		Chart newChart = new Chart();
		this.treant.setChart(newChart);
		assertEquals(newChart, this.treant.getChart());
	}

	@Test
	public void testSetNodeStructure() {
		Node newNode = new Node();
		this.treant.setNodeStructure(newNode);
		assertEquals(newNode, this.treant.getNodeStructure());
	}

	@Test
	public void testConstructor() {
		this.treant = new Treant("TEST", "14", 3);
		assertNotNull(this.treant);
	}

	@Test
	public void testCreateNodeStructureNullNullZeroZero() {
		assertEquals(Node.class, treant.createNodeStructure(null, null, 0, 0).getClass());
	}

	@Test(expected = NullPointerException.class)
	public void testCreateNodeStructureEmptyNullZeroZero() {
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl();
		assertEquals(Node.class, treant.createNodeStructure(element, null, 0, 0).getClass());
	}

	@Test
	public void testCreateNodeStructureFilledNullZeroZero() {
		DecisionKnowledgeElement element = persistenceStrategy.getDecisionKnowledgeElement(14);
		assertEquals(Node.class, treant.createNodeStructure(element, null, 0, 0).getClass());
	}

	@Test
	public void testCreateNodeStructureNullNullFilledFilled() {
		assertEquals(Node.class, treant.createNodeStructure(null, null, 4, 0).getClass());
	}

	@Test(expected = NullPointerException.class)
	public void testCreateNodeStructureEmptyNullFilledFilled() {
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl();
		assertEquals(Node.class, treant.createNodeStructure(element, null, 4, 0).getClass());
	}

	@Test
	public void testCreateNodeStructureFilledNullFilledFilled() {
		DecisionKnowledgeElement element = persistenceStrategy.getDecisionKnowledgeElement(14);
		assertEquals(Node.class, treant.createNodeStructure(element, null, 4, 0).getClass());
	}

	@Test
	public void testCreateNodeStructureFilledFilledFilledFilled() {
		DecisionKnowledgeElement element = persistenceStrategy.getDecisionKnowledgeElement(14);
		Link link = new LinkImpl();
		link.setLinkType("support");
		link.setSourceElement(10);
		link.setDestinationElement(14);
		link.setDestinationElement(persistenceStrategy.getDecisionKnowledgeElement(14));
		link.setSourceElement(persistenceStrategy.getDecisionKnowledgeElement(10));
		link.setId((long) 23);
		assertEquals(Node.class, treant.createNodeStructure(element, link, 4, 0).getClass());
	}
}
