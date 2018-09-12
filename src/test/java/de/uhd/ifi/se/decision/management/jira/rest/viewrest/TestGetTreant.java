package de.uhd.ifi.se.decision.management.jira.rest.viewrest;
import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ofbiz.core.entity.GenericEntityException;

import com.atlassian.activeobjects.test.TestActiveObjects;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.extraction.persistence.DecisionKnowledgeInCommentEntity;
import de.uhd.ifi.se.decision.management.jira.extraction.persistence.LinkBetweenDifferentEntitiesEntity;
import de.uhd.ifi.se.decision.management.jira.mocks.MockDefaultUserManager;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.rest.ViewRest;
import de.uhd.ifi.se.decision.management.jira.view.treant.TestTreant;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.jdbc.DatabaseUpdater;
import net.java.ao.test.jdbc.NonTransactional;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(TestTreant.AoSentenceTestDatabaseUpdater.class) 
public class TestGetTreant extends TestSetUp {
	private EntityManager entityManager;

	private ViewRest viewRest;

    private static final String INVALID_PROJECTKEY = "Decision knowledge elements cannot be shown since project key is invalid.";
	private static final String INVALID_ELEMETNS = "Treant cannot be shown since element key is invalid.";
	private static final String INVALID_DEPTH = "Treant cannot be shown since depth of Tree is NaN";

	@Before
	public void setUp() {
		viewRest = new ViewRest();
		initialization();
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockDefaultUserManager());
	}

	@Test
    public void testElementKeyNullDepthNull(){
        assertEquals(Response.status(Status.INTERNAL_SERVER_ERROR)
                .entity(ImmutableMap.of("error", INVALID_ELEMETNS))
                .build().getEntity(), viewRest.getTreant(null,null).getEntity());
    }


	@Test
	public void testElementNotExistsDepthNull() throws GenericEntityException {
		assertEquals(Response.status(Status.INTERNAL_SERVER_ERROR)
				.entity(ImmutableMap.of("error", INVALID_PROJECTKEY))
				.build().getEntity(), viewRest.getTreant("NotTEST", null).getEntity());
	}

	@Test
	public void testElementNotExistsDepthFilled() throws GenericEntityException {
		assertEquals(Response.status(Status.INTERNAL_SERVER_ERROR)
				.entity(ImmutableMap.of("error", INVALID_PROJECTKEY))
				.build().getEntity(), viewRest.getTreant("NotTEST", "3").getEntity());
	}

	@Test
    public void testElementExistsDepthNaN(){
        assertEquals(Response.status(Status.INTERNAL_SERVER_ERROR)
                .entity(ImmutableMap.of("error", INVALID_DEPTH))
                .build().getEntity(),viewRest.getTreant("TEST-12","test").getEntity());
    }

    @Test
    @NonTransactional
    public void testElemetExistsDepthNumber(){
        assertEquals(200,viewRest.getTreant("TEST-12", "3").getStatus());
    }
    
    
	public static final class AoSentenceTestDatabaseUpdater implements DatabaseUpdater {
        @SuppressWarnings("unchecked")
		@Override
        public void update(EntityManager entityManager) throws Exception
        {
            entityManager.migrate(DecisionKnowledgeInCommentEntity.class);
            entityManager.migrate(LinkBetweenDifferentEntitiesEntity.class);
        }
    }

}
