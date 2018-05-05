package de.uhd.ifi.se.decision.documentation.jira.view.treants;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.test.TestActiveObjects;

import de.uhd.ifi.se.decision.documentation.jira.ComponentGetter;
import de.uhd.ifi.se.decision.documentation.jira.TestSetUp;
import de.uhd.ifi.se.decision.documentation.jira.mocks.MockDefaultUserManager;
import de.uhd.ifi.se.decision.documentation.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.documentation.jira.view.treant.Chart;
import de.uhd.ifi.se.decision.documentation.jira.view.treant.Node;
import de.uhd.ifi.se.decision.documentation.jira.view.treant.Treant;
import net.java.ao.EntityManager;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestTreant extends TestSetUp {

	private EntityManager entityManager;

	private Chart chart;
	private Node nodeStructure;
	private Treant treant;

	@Before
	public void setUp() {
		this.chart = new Chart();
		this.nodeStructure = new Node();
		this.treant = new Treant();
		this.treant.setChart(chart);
		this.treant.setNodeStructure(nodeStructure);
		new ComponentGetter().init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockDefaultUserManager());
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
		initialization();
		this.treant = new Treant("TEST", "14", 3);
	}
}
