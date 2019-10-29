package de.uhd.ifi.se.decision.management.jira.rest;

import com.sun.jersey.spi.inject.Errors;
import de.uhd.ifi.se.decision.management.jira.config.PluginInitializer;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.releasenotes.ReleaseNoteCategory;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Set;

@Api(value = "/Config")
@RequestMapping(value = "/pet")
public interface ConfigRest {

	@ApiOperation(value = "Find pet by ID")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "SUCCESS", response =  Response.class),
			@ApiResponse(code = 500, message = "Project key is invalid.", response = Errors.ErrorMessage.class)
	})
	@RequestMapping(value = "/{isIssueStrategy}", method = RequestMethod.GET)
	Response isIssueStrategy(@PathVariable("projectKey") String projectKey);

	@Path("/isKnowledgeTypeEnabled")
	@GET
	Response isKnowledgeTypeEnabled(@QueryParam("projectKey") final String projectKey,
	                                       @QueryParam("knowledgeType") String knowledgeType);

	@Path("/getKnowledgeTypes")
	@GET
	Response getKnowledgeTypes(@QueryParam("projectKey") final String projectKey);

	@Path("/getLinkTypes")
	@GET
	Response getLinkTypes(@QueryParam("projectKey") final String projectKey);

	@Path("/getReleaseNoteMapping")
	@GET
	Response getReleaseNoteMapping(@Context HttpServletRequest request,
	                                      @QueryParam("projectKey") final String projectKey);

	@Path("/setActivated")
	@POST
	Response setActivated(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey,
	                      @QueryParam("isActivated") String isActivatedString);

	@Path("/setIssueStrategy")
	@POST
	Response setIssueStrategy(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey,
	                          @QueryParam("isIssueStrategy") String isIssueStrategyString);

	@Path("/setKnowledgeExtractedFromGit")
	@POST
	Response setKnowledgeExtractedFromGit(@Context HttpServletRequest request,
	                                             @QueryParam("projectKey") String projectKey,
	                                             @QueryParam("isKnowledgeExtractedFromGit") String isKnowledgeExtractedFromGit);

	@Path("/setGitUri")
	@POST
	Response setGitUri(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey,
	                          @QueryParam("gitUri") String gitUri);

	@Path("/setKnowledgeExtractedFromIssues")
	@POST
	Response setKnowledgeExtractedFromIssues(@Context HttpServletRequest request,
	                                                @QueryParam("projectKey") String projectKey,
	                                                @QueryParam("isKnowledgeExtractedFromIssues") String isKnowledgeExtractedFromIssues);

	@Path("/setKnowledgeTypeEnabled")
	@POST
	Response setKnowledgeTypeEnabled(@Context HttpServletRequest request,
	                                        @QueryParam("projectKey") String projectKey,
	                                        @QueryParam("isKnowledgeTypeEnabled") String isKnowledgeTypeEnabledString,
	                                        @QueryParam("knowledgeType") String knowledgeType);

	@Path("/setWebhookEnabled")
	@POST
	Response setWebhookEnabled(@Context HttpServletRequest request,
	                                  @QueryParam("projectKey") final String projectKey,
	                                  @QueryParam("isActivated") final String isActivatedString);


	@Path("/setWebhookData")
	@POST
	Response setWebhookData(@Context HttpServletRequest request,
	                               @QueryParam("projectKey") final String projectKey, @QueryParam("webhookUrl") final String webhookUrl,
	                               @QueryParam("webhookSecret") final String webhookSecret);

	@Path("/setWebhookType")
	@POST
	Response setWebhookType(@Context HttpServletRequest request,
	                               @QueryParam("projectKey") final String projectKey, @QueryParam("webhookType") final String webhookType,
	                               @QueryParam("isWebhookTypeEnabled") final boolean isWebhookTypeEnabled);

	@Path("/setReleaseNoteMapping")
	@POST
	Response setReleaseNoteMapping(@Context HttpServletRequest request,
	                                      @QueryParam("projectKey") final String projectKey,
	                                      @QueryParam("releaseNoteCategory") final ReleaseNoteCategory category,
	                                      List<String> selectedIssueNames);

	@Path("/clearSentenceDatabase")
	@POST
	Response clearSentenceDatabase(@Context HttpServletRequest request,
	                                      @QueryParam("projectKey") final String projectKey);

	@Path("/classifyWholeProject")
	@POST
	Response classifyWholeProject(@Context HttpServletRequest request,
	                                     @QueryParam("projectKey") final String projectKey);

	@Path("/trainClassifier")
	@POST
	Response trainClassifier(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey,
	                                @QueryParam("arffFileName") String arffFileName);
	@Path("/evaluateModel")
	@POST
	Response evaluateModel(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey);


	@Path("/saveArffFile")
	@POST
	Response saveArffFile(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey,
	                             @QueryParam("useOnlyValidatedData") boolean useOnlyValidatedData);

	@Path("/setIconParsing")
	@POST
	Response setIconParsing(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey,
	                               @QueryParam("isActivatedString") String isActivatedString);

	@Path("/setUseClassifierForIssueComments")
	@POST
	Response setUseClassifierForIssueComments(@Context HttpServletRequest request,
	                                                 @QueryParam("projectKey") String projectKey,
	                                                 @QueryParam("isClassifierUsedForIssues") String isActivatedString);

	static void manageDefaultIssueTypes(String projectKey, boolean isIssueStrategy) {
		Set<KnowledgeType> defaultKnowledgeTypes = KnowledgeType.getDefaultTypes();
		for (KnowledgeType knowledgeType : defaultKnowledgeTypes) {
			if (isIssueStrategy) {
				ConfigPersistenceManager.setKnowledgeTypeEnabled(projectKey, knowledgeType.toString(), true);
				PluginInitializer.createIssueType(knowledgeType.toString());
				PluginInitializer.addIssueTypeToScheme(knowledgeType.toString(), projectKey);
			} else {
				PluginInitializer.removeIssueTypeFromScheme(knowledgeType.toString(), projectKey);
			}
		}
	}
}
