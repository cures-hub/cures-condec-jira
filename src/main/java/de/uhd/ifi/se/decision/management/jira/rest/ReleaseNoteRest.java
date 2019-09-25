package de.uhd.ifi.se.decision.management.jira.rest;

import com.atlassian.jira.user.ApplicationUser;
import com.google.common.collect.ImmutableMap;
import de.uhd.ifi.se.decision.management.jira.config.AuthenticationManager;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterExtractor;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.ReleaseNotesPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.releasenotes.*;
import de.uhd.ifi.se.decision.management.jira.releasenotes.impl.ReleaseNoteImpl;
import de.uhd.ifi.se.decision.management.jira.releasenotes.ReleaseNotesCreator;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;


/**
 * REST resource for Release Note
 */
@Path("/release-note")
public class ReleaseNoteRest {

	@Path("/getProposedIssues")
	@POST
	@Produces({MediaType.APPLICATION_JSON})
	public Response getProposedIssues(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey, ReleaseNoteConfiguration releaseNoteConfiguration) {

		ApplicationUser user = AuthenticationManager.getUser(request);
		List<DecisionKnowledgeElement> queryResult = new ArrayList<DecisionKnowledgeElement>();
		String query = "?jql=project=" + projectKey + " && resolved >= " + releaseNoteConfiguration.getStartDate() + " && resolved <= " + releaseNoteConfiguration.getEndDate();
		FilterExtractor extractor = new FilterExtractor(projectKey, user, query);
		List<DecisionKnowledgeElement> elementsMatchingQuery = new ArrayList<DecisionKnowledgeElement>();
		elementsMatchingQuery = extractor.getAllElementsMatchingQuery();
		if(elementsMatchingQuery.size()==0){
			return Response.status(Response.Status.BAD_REQUEST).entity(ImmutableMap.of("error",
					"No resolved issues were found in this date range!"))
					.build();
		}
		ReleaseNotesCreator releaseNotesCreator = new ReleaseNotesCreator(elementsMatchingQuery, releaseNoteConfiguration, user);
		HashMap<String, ArrayList<ReleaseNoteIssueProposal>> mappedProposals = releaseNotesCreator.getMappedProposals();

		if(mappedProposals==null){
			return Response.status(Response.Status.BAD_REQUEST).entity(ImmutableMap.of("error",
					"No issues with the mapped types are resolved in this date range!"))
					.build();
		}
		HashMap<String,Object> result=new HashMap<String,Object>();
		result.put("proposals",mappedProposals);
		result.put("additionalConfiguration",releaseNoteConfiguration.getAdditionalConfiguration());
		result.put("title",releaseNoteConfiguration.getTitle());
		result.put("startDate",releaseNoteConfiguration.getStartDate());
		result.put("endDate",releaseNoteConfiguration.getEndDate());
		return Response.ok(result).build();
	}

	@Path("/postProposedKeys")
	@POST
	@Produces({MediaType.APPLICATION_JSON})
	public Response postProposedKeys(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey, HashMap<String,HashMap<String,ArrayList<String>>> postObject) {
		ApplicationUser user = AuthenticationManager.getUser(request);
		HashMap<String,ArrayList<String>> keysForContent = postObject.get("selectedKeys");
		String title= postObject.get("title").get("id").get(0);
		ArrayList<String> additionalConfiguration = postObject.get("additionalConfiguration").get("id");
		MarkdownCreator markdownCreator = new MarkdownCreator(user, projectKey, keysForContent,title,additionalConfiguration);

		//generate text string
		String markDownString = markdownCreator.getMarkdownString();
		//return text string
		HashMap<String, String> result = new HashMap<String, String>();
		result.put("markdown", markDownString);
		return Response.ok(result).build();
	}


	@Path("/createReleaseNote")
	@POST
	@Produces({MediaType.APPLICATION_JSON})
	public Response createReleaseNote(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey, HashMap<String,String> postObject) {
		ApplicationUser user = AuthenticationManager.getUser(request);
		String title = postObject.get("title");
		String startDate = postObject.get("startDate");
		String endDate = postObject.get("endDate");
		String releaseNoteContent=postObject.get("content");

		ReleaseNoteImpl releaseNote = new ReleaseNoteImpl(title, releaseNoteContent, projectKey,startDate,endDate);
		long id = ReleaseNotesPersistenceManager.createReleaseNotes(releaseNote, user);

		return Response.ok(id).build();
	}
	@Path("/updateReleaseNote")
	@POST
	@Produces({MediaType.APPLICATION_JSON})
	public Response updateReleaseNote(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey, ReleaseNote releaseNote) {
		ApplicationUser user = AuthenticationManager.getUser(request);

		boolean updated = ReleaseNotesPersistenceManager.updateReleaseNotes(releaseNote, user);

		return Response.ok(updated).build();
	}

	@Path("/getReleaseNote")
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public Response getReleaseNote(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey, @QueryParam("id") long id) {
		ReleaseNote releaseNote= ReleaseNotesPersistenceManager.getReleaseNotes(id);
		return Response.ok(releaseNote).build();
	}

	@Path("/getAllReleaseNotes")
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public Response getAllReleaseNotes(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey,@QueryParam("query") String query) {
		List<ReleaseNote> releaseNotes= ReleaseNotesPersistenceManager.getAllReleaseNotes(projectKey,query);
		return Response.ok(releaseNotes).build();
	}
	@Path("/deleteReleaseNote")
	@DELETE
	@Produces({MediaType.APPLICATION_JSON})
	public Response deleteReleaseNote(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey, @QueryParam("id") long id) {
		ApplicationUser user = AuthenticationManager.getUser(request);
		boolean deleted= ReleaseNotesPersistenceManager.deleteReleaseNotes(id,user);
		return Response.ok(deleted).build();
	}

}
