package de.uhd.ifi.se.decision.management.jira.view.vis;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;

import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.mocks.MockUserManager;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.LinkInDatabase;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.PartOfJiraIssueTextInDatabase;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.jdbc.DatabaseUpdater;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(TestVisTimeLineNode.AoSentenceTestDatabaseUpdater.class)
public class TestVisTimeLineNode extends TestSetUpWithIssues {
	private EntityManager entityManager;
	private DecisionKnowledgeElement element;
	private VisTimeLineNode timeNode;
	private String createdString;
	private String closedString;

	private String createDateString(Date created) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(created);
		int year = calendar.get(Calendar.YEAR) + 1900;
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		return year + "-" + month + "-" + day;
	}

	@Before
	public void setUp() {
		initialization();
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockUserManager());
		element = new DecisionKnowledgeElementImpl(ComponentAccessor.getIssueManager().getIssueObject((long) 14));
		element.setCreated(new Date(System.currentTimeMillis() - 1000));
		element.setClosed(new Date(System.currentTimeMillis()));
		timeNode = new VisTimeLineNode(element);
		createdString = createDateString(element.getCreated());
		closedString = createDateString(element.getClosed());
	}

	public static final class AoSentenceTestDatabaseUpdater implements DatabaseUpdater {
		@SuppressWarnings("unchecked")
		@Override
		public void update(EntityManager entityManager) throws Exception {
			entityManager.migrate(PartOfJiraIssueTextInDatabase.class);
			entityManager.migrate(LinkInDatabase.class);
		}
	}

	@Test
	public void testConstructorNull() {
		VisTimeLineNode node = new VisTimeLineNode(null);
		assertEquals(0, node.getId());
	}

	@Test
	public void testConstructorFilled() {
		VisTimeLineNode node = new VisTimeLineNode(element);
		assertEquals(element.getId(), node.getId());
	}

	@Test
	public void testGetId() {
		assertEquals(element.getId(), timeNode.getId());
	}

	@Test
	public void testSetId() {
		VisTimeLineNode node = new VisTimeLineNode(element);
		node.setId(12345);
		assertEquals(12345, node.getId());
	}

	@Test
	public void testGetContent() {
		assertEquals(element.getKey(), timeNode.getContent());
	}

	@Test
	public void testSetContent() {
		timeNode.setContent("Test new Content");
		assertEquals("Test new Content", timeNode.getContent());
	}

	@Test
	public void testGetStart() {
		assertEquals(createdString, timeNode.getStart());
	}

	@Test
	public void testSetStart() {
		Date date = new Date(System.currentTimeMillis() - 1000);
		createdString = createDateString(date);
		timeNode.setStart(createdString);
		assertEquals(createdString, timeNode.getStart());
	}

	@Test
	public void testGetEnd() {
		assertEquals(closedString, timeNode.getEnd());
	}

	@Test
	public void testSetEnd() {
		Date date = new Date(System.currentTimeMillis() - 1000);
		closedString = createDateString(date);
		timeNode.setEnd(closedString);
		assertEquals(closedString, timeNode.getEnd());
	}
}
