package de.uhd.ifi.se.decision.management.jira.rest.configrest;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.classification.OnlineTrainer;
import de.uhd.ifi.se.decision.management.jira.classification.TestOnlineTrainer;
import de.uhd.ifi.se.decision.management.jira.classification.implementation.OnlineFileTrainerImpl;

public class TestEvaluateModel extends TestConfigSuper {

	@Override
	@Before
	public void setUp() {
		super.setUp();
	}

	@Test
	public void testRequestNullProjectKeyNull() {
		assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
			configRest.evaluateModel(null, null).getEntity());
	}

	@Test
	public void testRequestValidProjectKeyExistsUntrainedClassifier() {
		try{
			configRest.evaluateModel(request, "TEST");
		}catch (Exception exception) {
			assertEquals("null value in entry: error=null", exception.getMessage());
		}
	}



	@Test
	public void testRequestValidProjectKeyExistsTrainedClassifier() {

		OnlineTrainer trainer = new OnlineFileTrainerImpl("TEST", TestOnlineTrainer.getTrainingData());
		trainer.train();
		assertEquals(200, configRest.evaluateModel(request, "TEST").getStatus());
	}
}
