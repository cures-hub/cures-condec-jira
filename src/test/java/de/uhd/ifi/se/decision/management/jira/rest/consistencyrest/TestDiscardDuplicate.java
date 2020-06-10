package de.uhd.ifi.se.decision.management.jira.rest.consistencyrest;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestDiscardDuplicate extends TestConsistencyRestSuper {

	@Test
	public void testDiscardDetectedDuplicate() {
		assertEquals("Request should be valid.", 200, consistencyRest.discardDetectedDuplicate(request, project.getKey(), issues.get(0).getKey(), issues.get(1).getKey()).getStatus());
		assertEquals("Request should be invalid.", 400, consistencyRest.discardDetectedDuplicate(request, project.getKey(), "null", issues.get(1).getKey()).getStatus());
		assertEquals("Request should be invalid.", 500, consistencyRest.discardDetectedDuplicate(request, project.getKey(), null, issues.get(1).getKey()).getStatus());
		assertEquals("Request should be invalid.", 400, consistencyRest.discardDetectedDuplicate(request, project.getKey(), "null", "null").getStatus());

		assertEquals("Request should be invalid.", 500, consistencyRest.discardDetectedDuplicate(request, null,  issues.get(0).getKey(), issues.get(1).getKey()).getStatus());
	}

}
