package ut.db.strategy.impl.AoStrategy;


import com.atlassian.DecisionDocumentation.rest.Decisions.model.DecisionRepresentation;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.MockApplicationUser;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author Tim Kuchenbuch
 */
@RunWith(ActiveObjectsJUnitRunner.class)
public class TestCreateDecisionComponentTest extends AoStrategyTestSetUp {

    @Test
    public void testRepresNullUserNull(){
        assertNull(aoStrategy.createDecisionComponent(null,null));
    }

    @Test
    public void testRepresFilledUserNull(){
        DecisionRepresentation dec = new DecisionRepresentation();
        assertNull(aoStrategy.createDecisionComponent(dec,null));
    }

    //TODO Fixing the Test Problems (Closed Connection)
    @Ignore
    public void testRepresFilledUserNoFails(){
        DecisionRepresentation dec = new DecisionRepresentation();
        dec.setProjectKey("TEST");
        dec.setType("Solution");
        ApplicationUser user = new MockApplicationUser("NoFails");
        assertNotNull(aoStrategy.createDecisionComponent(dec,user));
    }

    //TODO Fixing the Test Problems (Closed Connection)
    @Ignore
    public void testRepresFilledUserWithFails(){
        DecisionRepresentation dec = new DecisionRepresentation();
        dec.setProjectKey("TEST");
        dec.setType("Solution");
        ApplicationUser user = new MockApplicationUser("WithFails");
        assertNull(aoStrategy.createDecisionComponent(dec, user));
    }
}
