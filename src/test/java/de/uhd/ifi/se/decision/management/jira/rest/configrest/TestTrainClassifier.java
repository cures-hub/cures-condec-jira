package de.uhd.ifi.se.decision.management.jira.rest.configrest;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;

import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestTrainClassifier extends TestConfigSuper {
	private static final String INVALID_ARFF_FILE = "Classifier could not be trained.";
	
	@Test
	public void testRequestNullProjectKeyNullArffFileNull() {
		assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
				configRest.trainClassifier(null, null, null).getEntity());
	}

	@Test
	public void testRequestNullProjectKeyNullArffFileProvided() {
		assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
				configRest.trainClassifier(null, null, "arffFile.arff").getEntity());
	}
	
	@Test
	public void testRequestValidProjectKeyExistsArffFileNull() {
		assertEquals(getBadRequestResponse(INVALID_ARFF_FILE).getEntity(),
				configRest.trainClassifier(request, "TEST", null).getEntity());
	}
	
	@Test
	public void testRequestValidProjectKeyExistsArffFileEmpty() {
		assertEquals(getBadRequestResponse(INVALID_ARFF_FILE).getEntity(),
				configRest.trainClassifier(request, "TEST", "").getEntity());
	}
}
