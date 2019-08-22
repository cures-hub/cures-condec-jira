package de.uhd.ifi.se.decision.management.jira.view.vis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.*;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.component.ComponentAccessor;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.DecisionKnowledgeElementInDatabase;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.LinkInDatabase;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.PartOfJiraIssueTextInDatabase;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.DatabaseUpdater;

public class TestVisTimeLine extends TestSetUp {
	private VisTimeLine visTimeLine;

	@Before
	public void setUp() {
		init();
		List<DecisionKnowledgeElement> elementList = new ArrayList<>();
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl(
				ComponentAccessor.getIssueManager().getIssueObject((long) 14));
		element.setCreated(new Date(System.currentTimeMillis() - 1000));
		element.setClosed(new Date(System.currentTimeMillis()));
		elementList.add(element);
		visTimeLine = new VisTimeLine(elementList);
	}

	public static final class AoSentenceTestDatabaseUpdater implements DatabaseUpdater {
		@SuppressWarnings("unchecked")
		@Override
		public void update(EntityManager entityManager) throws Exception {
			entityManager.migrate(DecisionKnowledgeElementInDatabase.class);
			entityManager.migrate(PartOfJiraIssueTextInDatabase.class);
			entityManager.migrate(LinkInDatabase.class);
		}
	}

	@Test
	public void testConstEmpty() {
		VisTimeLine timeLine = new VisTimeLine("");
		assertEquals(0, timeLine.getElementList().size(), 0.0);
	}

	@Test
	public void testConstFilled() {
		VisTimeLine timeLine = new VisTimeLine("Test");
		assertNotNull(timeLine.getElementList());
	}

	@Test
	public void testConstListNull() {
		VisTimeLine timeLine = new VisTimeLine((List<DecisionKnowledgeElement>) null);
		assertNull(timeLine.getElementList());
	}

	@Test
	public void testConstListFilled() {
		assertEquals(1, visTimeLine.getElementList().size(), 0.0);
	}

	@Test
	public void testGetEvolutionData() {
		assertEquals(1, visTimeLine.getEvolutionData().size(), 0.0);
	}

	@Test
	public void testGetElementList() {
		assertEquals(1, visTimeLine.getElementList().size(), 0.0);
	}

	@Test
	public void testSetElementList() {
		List<DecisionKnowledgeElement> elementList = new ArrayList<>();
		DecisionKnowledgeElement element1 = new DecisionKnowledgeElementImpl(
				ComponentAccessor.getIssueManager().getIssueObject((long) 14));
		element1.setCreated(new Date(System.currentTimeMillis() - 1000));
		element1.setClosed(new Date(System.currentTimeMillis()));
		elementList.add(element1);
		DecisionKnowledgeElement element2 = new DecisionKnowledgeElementImpl(
				ComponentAccessor.getIssueManager().getIssueObject((long) 10));
		element2.setCreated(new Date(System.currentTimeMillis() - 1000));
		element2.setClosed(new Date(System.currentTimeMillis()));
		elementList.add(element2);
		visTimeLine.setElementList(elementList);
		assertEquals(2, visTimeLine.getElementList().size(), 0.0);
	}

	@Test
	public void testGetGroupSet(){
		assertEquals(1, visTimeLine.getGroupSet().size(), 0.0);
	}

	@Test
	public void testSetGroupSet(){
		HashSet<VisTimeLineGroup> groups = new HashSet<>();
		visTimeLine.setGroupSet(groups);
		assertEquals(0, visTimeLine.getGroupSet().size(), 0.0);
	}
}
