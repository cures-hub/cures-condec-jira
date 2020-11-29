package de.uhd.ifi.se.decision.management.jira.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.atlassian.jira.issue.comments.Comment;

public class TestOrigin {

	@Test
	public void testDeterminOriginNull() {
		assertEquals(Origin.DOCUMENTATION_LOCATION, Origin.determineOrigin((Comment) null));
		assertEquals(Origin.DOCUMENTATION_LOCATION, Origin.determineOrigin((String) null));
	}

	@Test
	public void testDeterminOriginCommit() {
		assertEquals(Origin.COMMIT, Origin.determineOrigin("Hash: 123"));
	}

	@Test
	public void testDeterminOriginSameAsDocumentationLocation() {
		assertEquals(Origin.DOCUMENTATION_LOCATION, Origin.determineOrigin("No commit SHA"));
	}

}
