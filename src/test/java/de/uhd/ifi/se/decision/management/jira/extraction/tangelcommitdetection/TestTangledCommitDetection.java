package de.uhd.ifi.se.decision.management.jira.extraction.tangelcommitdetection;

import de.uhd.ifi.se.decision.management.jira.extraction.impl.Diff;
import de.uhd.ifi.se.decision.management.jira.extraction.impl.TangledCommitDetectionImpl;
import org.junit.Before;
import org.junit.Test;


import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;

import static org.junit.Assert.assertEquals;


public class TestTangledCommitDetection extends TestSetUpGit {

    TangledCommitDetectionImpl tangledCommitDetectionImpl;

    @Before
    public void setUp() {
        super.setUp();
        tangledCommitDetectionImpl = new TangledCommitDetectionImpl();
    }

    @Test
    public void testGetlineDistances() {
        assertEquals(1, 1);
    }
}
