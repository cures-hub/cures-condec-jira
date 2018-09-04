package de.uhd.ifi.se.decision.management.jira.extraction.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.exception.CreateException;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.extraction.persistence.DecisionKnowledgeInCommentEntity;
import de.uhd.ifi.se.decision.management.jira.extraction.persistence.LinksInSentencesEntity;
import de.uhd.ifi.se.decision.management.jira.mocks.MockDefaultUserManager;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProjectImpl;
import de.uhd.ifi.se.decision.management.jira.model.Graph;
import de.uhd.ifi.se.decision.management.jira.model.GraphImpl;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.LinkImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.IssueStrategy;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.jdbc.DatabaseUpdater;
import net.java.ao.test.jdbc.NonTransactional;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;




@RunWith(ActiveObjectsJUnitRunner.class)
@Data(TestComment.AoSentenceTestDatabaseUpdater.class) 
public class TestSentenceExtractionGraph extends TestSetUp{
	

	private EntityManager entityManager;
	private Graph graph;
	private DecisionKnowledgeElement element;

	@Before
	public void setUp() throws CreateException {
		initialization();
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockDefaultUserManager());
		element = new DecisionKnowledgeElementImpl(ComponentAccessor.getIssueManager().getIssueObject((long) 14));
		element.setProject(new DecisionKnowledgeProjectImpl("Test"));
		graph = new SentenceExtractionGraphImpl(element.getProject().getProjectKey(), element.getKey());
		
		ComponentGetter.getActiveObjects().migrate(LinksInSentencesEntity.class);
	}

	@Test
	public void testProjectKeyConstructor() {
		Graph graphRoot = new GraphImpl(element.getProject().getProjectKey());
		assertNotNull(graphRoot);
	}

	@Test
	public void testRootElementLinkDistConstructor() {
		Graph graphRoot = new GraphImpl(element.getProject().getProjectKey(), element.getKey());
		assertNotNull(graphRoot);
	}

	@Test
	public void testRootElementConstructor(){
		Graph graphRoot = new GraphImpl(element);
		assertNotNull(graphRoot);
	}

	@Test
	public void testGetLinkedElementsNull() {
		assertEquals(0, graph.getLinkedElements(null).size());
	}

	@Test
	public void testGetLinkedElementsEmpty() {
		DecisionKnowledgeElement emptyElement = new DecisionKnowledgeElementImpl();
		assertEquals(0, graph.getLinkedElements(emptyElement).size());
	}

	@Test
	public void testGetLinkedElementsAndLinksNull(){
		assertEquals(0, graph.getLinkedElementsAndLinks(null).size());
	}

	@Test
	@NonTransactional
	public void testGetLinkedElementsAndLinksEmpty() {
		DecisionKnowledgeElement emptyElement = new DecisionKnowledgeElementImpl();
		assertEquals(0, graph.getLinkedElements(emptyElement).size());
	}

	@Test
	public void testSetRootElement() {
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl();
		element.setId(123);
		element.setSummary("Test New Element");
		graph.setRootElement(element);
		assertEquals(element.getSummary(), graph.getRootElement().getSummary());
	}

	@Test
	public void testSetGetProject(){
		DecisionKnowledgeProject project = new DecisionKnowledgeProjectImpl("TEST-Set");
		graph.setProject(project);
		assertEquals("TEST-Set", graph.getProject().getProjectKey());
	}
	
	

	public static final class AoSentenceTestDatabaseUpdater implements DatabaseUpdater // (2)
    {
        @SuppressWarnings("unchecked")
		@Override
        public void update(EntityManager entityManager) throws Exception
        {
            entityManager.migrate(LinksInSentencesEntity.class);
            entityManager.migrate(DecisionKnowledgeInCommentEntity.class);
        }
    }

}
