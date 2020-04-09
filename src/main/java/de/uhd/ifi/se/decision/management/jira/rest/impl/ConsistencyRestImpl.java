package de.uhd.ifi.se.decision.management.jira.rest.impl;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.MutableIssue;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.rest.ConsistencyRest;
import scala.util.parsing.combinator.testing.Str;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.*;

/**
 * REST resource for plug-in configuration
 */

@Path("/consistency")
public class ConsistencyRestImpl implements ConsistencyRest {

	@Override
	@Path("/getRelatedIssues")
	@GET
	public Response setActivated(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey,
								 @QueryParam("issueKey") String issueKey) {
		//System.out.println(issueKey);
		MutableIssue issue = ComponentAccessor.getIssueManager().getIssueObject(issueKey);
		/*Set<String> issueKeys  = ComponentAccessor.getIssueManager().getIssueObject(issueKey);
		Iterator<String> iter =  issueKeys.iterator();
		List<Issue> issues = new ArrayList<Issue>();
		while (iter.hasNext()){
			issues.add(ComponentAccessor.getIssueManager().getIssueByCurrentKey(iter.next()));
		}
*/
		HashMap<String, Object> result = new HashMap<String, Object>();
		result.put("relatedIssues", List.of(this.issueToJsonMap(issue)));

		return Response.ok(result).build();
	}

	private Map<String, String> issueToJsonMap(Issue issue) {
		Map jsonMap =  new HashMap<>();
		jsonMap.put("key", issue.getKey());
		jsonMap.put("summary", issue.getSummary());
		jsonMap.put("id", issue.getId());

		return jsonMap;
	}

}
