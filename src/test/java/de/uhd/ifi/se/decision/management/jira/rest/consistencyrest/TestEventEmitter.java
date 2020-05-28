package de.uhd.ifi.se.decision.management.jira.rest.consistencyrest;

import de.uhd.ifi.se.decision.management.jira.eventlistener.implementation.ConsistencyCheckEventListenerSingleton;
import de.uhd.ifi.se.decision.management.jira.rest.EventEmitter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.Assert.assertTrue;

public class TestEventEmitter extends Mockito {

	private HttpServletRequest request;
	private HttpServletResponse response;
	private EventEmitter emitter;

	@Before
	public void setUp() {
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		emitter = new EventEmitter();
	}

	@Test
	public void testDoGet() throws Exception{

		StringWriter stringWriter = new StringWriter();
		PrintWriter writer = new PrintWriter(stringWriter);
		when(response.getWriter()).thenReturn(writer);

		emitter.doGet(request, response);

		writer.flush(); // it may not have been flushed yet...
		assertTrue(stringWriter.toString().isEmpty());
	}

	@After
	public void cleanUp(){
		ConsistencyCheckEventListenerSingleton.getInstance().resetSubscribers();

	}

}
