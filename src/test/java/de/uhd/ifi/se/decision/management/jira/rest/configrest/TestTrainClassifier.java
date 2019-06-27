package de.uhd.ifi.se.decision.management.jira.rest.configrest;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;

import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestTrainClassifier extends TestConfigSuper {
	private static final String INVALID_ARFF_FILE = "The classifier could not be trained since the ARFF file name is invalid.";
	private static final String INTERNAL_SERVER_ERROR = "The classifier could not be trained due to an internal server error.";
	
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
	
	@Test
	public void testRequestValidProjectKeyExistsArffFileNonExistent() {
		assertEquals(getBadRequestResponse(INTERNAL_SERVER_ERROR).getEntity(),
				configRest.trainClassifier(request, "TEST", "fake.arff").getEntity());
	}
}
