package de.uhd.ifi.se.decision.documentation.jira.persistence;

import de.uhd.ifi.se.decision.documentation.jira.TestSetUp;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestStrategyProvider extends TestSetUp {
    private  StrategyProvider provider;

    @Before
    public void setUp(){
        initialisation();
        provider = new StrategyProvider();
    }

    @Test
    public void testProjectKeyNull(){
        assertNull(provider.getStrategy(null));
    }

    @Test
    public void testProjectKeyNotExist(){
        provider.getStrategy("TESTNOT");
    }

    @Test
    public void testProjectKeyExists(){
        provider.getStrategy("TEST");
    }
}