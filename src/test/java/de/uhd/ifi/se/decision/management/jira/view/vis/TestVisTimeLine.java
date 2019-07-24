package de.uhd.ifi.se.decision.management.jira.view.vis;

import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;
import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.mocks.MockUserManager;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.DecisionKnowledgeElementInDatabase;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.LinkInDatabase;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.PartOfJiraIssueTextInDatabase;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.jdbc.DatabaseUpdater;
import net.java.ao.test.jdbc.NonTransactional;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(TestVisTimeLine.AoSentenceTestDatabaseUpdater.class)
public class TestVisTimeLine extends TestSetUpWithIssues {
	private EntityManager entityManager;
	private VisTimeLine visTimeLine;

	@Before
	public void setUp() {
		initialization();
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(), new MockUserManager());
		List<DecisionKnowledgeElement> elementList = new ArrayList<>();
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl(ComponentAccessor.getIssueManager().getIssueObject((long) 14));
		element.setCreated(new Date(System.currentTimeMillis()-1000));
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
	public void testConstEmpty(){
		VisTimeLine timeLine = new VisTimeLine("");
		assertEquals(0,timeLine.getElementList().size(),0.0);
	}

	@Test
	public void testConstFilled(){
		VisTimeLine timeLine = new VisTimeLine("Test");
		assertNotNull(timeLine.getElementList());
	}

	@Test
	public void testConstListNull(){
		VisTimeLine timeLine = new VisTimeLine((List<DecisionKnowledgeElement>)null);
		assertNull(timeLine.getElementList());
	}

	@Test
	public void testConstListFilled(){
		assertEquals(1, visTimeLine.getElementList().size(),0.0);
	}

	@Test
	public void testGetEvolutionData(){
		assertEquals(1, visTimeLine.getEvolutionData().size(),0.0);
	}

	@Test
	public void testGetElementList(){
		assertEquals(1, visTimeLine.getElementList().size(),0.0);
	}

	@Test
	public void testSetElementList(){
		List<DecisionKnowledgeElement> elementList = new ArrayList<>();
		DecisionKnowledgeElement element1 = new DecisionKnowledgeElementImpl(ComponentAccessor.getIssueManager().getIssueObject((long) 14));
		element1.setCreated(new Date(System.currentTimeMillis()-1000));
		element1.setClosed(new Date(System.currentTimeMillis()));
		elementList.add(element1);
		DecisionKnowledgeElement element2 = new DecisionKnowledgeElementImpl(ComponentAccessor.getIssueManager().getIssueObject((long) 10));
		element2.setCreated(new Date(System.currentTimeMillis()-1000));
		element2.setClosed(new Date(System.currentTimeMillis()));
		elementList.add(element2);
		visTimeLine.setElementList(elementList);
		assertEquals(2, visTimeLine.getElementList().size(),0.0);
	}
}
