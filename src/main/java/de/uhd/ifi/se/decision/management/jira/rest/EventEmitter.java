package de.uhd.ifi.se.decision.management.jira.rest;


import de.uhd.ifi.se.decision.management.jira.eventlistener.Subscriber;
import de.uhd.ifi.se.decision.management.jira.eventlistener.implementation.ConsistencyCheckEventListenerSingleton;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicInteger;


public class EventEmitter extends HttpServlet implements Subscriber {
	private static AtomicInteger ID = new AtomicInteger();

	private HttpServletResponse response;

	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		ConsistencyCheckEventListenerSingleton.getInstance().register(this);

		this.response = response;
		//content type must be set to text/event-stream
		response.setContentType("text/event-stream");
		//cache must be set to no-cache
		response.setHeader("Cache-Control", "no-cache");
		//encoding is set to UTF-8
		response.setCharacterEncoding("UTF-8");

	}


	@Override
	public void update() {

		PrintWriter writer = null;
		try {
			writer = response.getWriter();
			writer.write("event: message" + "\n");
			writer.write("data: activate" + "\r\n");
			writer.write("id: " + ID.get() + "\r\n");
			ID.incrementAndGet();
			writer.write("\r\n");
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			ConsistencyCheckEventListenerSingleton.getInstance().unregister(this);
			writer.close();
		}
	}
}
