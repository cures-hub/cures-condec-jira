package de.uhd.ifi.se.decision.management.jira.extraction.view.reports;

import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.datetime.DateTimeFormatterFactory;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchProvider;
import com.atlassian.jira.jql.builder.JqlClauseBuilder;
import com.atlassian.jira.jql.builder.JqlQueryBuilder;
import com.atlassian.jira.plugin.report.impl.AbstractReport;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.util.ParameterUtils;
import com.atlassian.jira.web.action.ProjectActionSupport;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport;

import de.uhd.ifi.se.decision.management.jira.extraction.persistence.ActiveObjectsManager;
import de.uhd.ifi.se.decision.management.jira.extraction.view.reports.plotlib.Plotter;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

import org.apache.axis.utils.ByteArrayOutputStream;
import org.apache.commons.codec.binary.Base64;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

public class CreationReport extends AbstractReport {
	@JiraImport
	private final SearchProvider searchProvider;
	@JiraImport
	private final ProjectManager projectManager;

	private Long projectId;

	public CreationReport(SearchProvider searchProvider, ProjectManager projectManager,
			@JiraImport DateTimeFormatterFactory dateTimeFormatterFactory) {
		this.searchProvider = searchProvider;
		this.projectManager = projectManager;
	}
	
	public String generateReportHtml(ProjectActionSupport action, Map params) throws Exception {

		byte[] nrOfCommentsPerIssue = getNumberOfCommentsPerIssue(action.getLoggedInUser(), projectId);
		byte[] nrOfCommitsPerIssue = getNumberOfCommitsPerIssue(action.getLoggedInUser(), projectId);
		byte[] decKnowElement = getDecKnowElementsPerIssue(action.getLoggedInUser(), projectId);

		Map<String, Object> velocityParams = new HashMap<>();

		// velocityParams.put("projectName",
		// projectManager.getProjectObj(projectId).getName());
		velocityParams.put("nrOfCommentsPerIssue", new String(nrOfCommentsPerIssue));
		velocityParams.put("nrOfCommitsPerIssue", new String(nrOfCommitsPerIssue));
		velocityParams.put("decKnowElement", new String(decKnowElement));

		return descriptor.getHtml("view", velocityParams);
	}

	private byte[] getDecKnowElementsPerIssue(ApplicationUser loggedInUser, Long projectId2) throws SearchException {
		Map<String, Integer> dkeCount = new HashMap<String, Integer>();

		for(KnowledgeType type: KnowledgeType.getDefaulTypes()) {
		dkeCount.put(type.toString(),
				ActiveObjectsManager.getAllElementsFromAoByType(projectManager.getProjectObj(projectId).getKey(),
						type).size());
		}
		BufferedImage image = Plotter.getPieChart("Number of KnowledgeTypes per Issue", dkeCount);
		return createEncodedByteArray(image);
	}

	private byte[] getNumberOfCommitsPerIssue(ApplicationUser loggedInUser, Long projectId2) throws SearchException {
		JqlClauseBuilder jqlClauseBuilder = JqlQueryBuilder.newClauseBuilder();
		SearchService searchService = ComponentAccessor.getComponentOfType(SearchService.class);

		com.atlassian.query.Query query = jqlClauseBuilder.project(projectId2).buildQuery();
		com.atlassian.jira.issue.search.SearchResults searchResults = null;

		searchResults = searchService.search(loggedInUser, query, PagerFilter.getUnlimitedFilter());

		List<Integer> commentList = new ArrayList<>();
		for (Issue issue : searchResults.getIssues()) {
			commentList.add(ComponentAccessor.getCommentManager().getComments(issue).size());
		}
		BufferedImage image = Plotter.getBoxPlot("Number of Commits per Issue", "#Comments", commentList);
		return createEncodedByteArray(image);
	}

	private byte[] getNumberOfCommentsPerIssue(ApplicationUser user, Long projectId) throws SearchException {
		user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();
		JqlClauseBuilder jqlClauseBuilder = JqlQueryBuilder.newClauseBuilder();
		SearchService searchService = ComponentAccessor.getComponentOfType(SearchService.class);

		com.atlassian.query.Query query = jqlClauseBuilder.project(projectId).buildQuery();
		com.atlassian.jira.issue.search.SearchResults searchResults = null;

		searchResults = searchService.search(user, query, PagerFilter.getUnlimitedFilter());

		List<Integer> commentList = new ArrayList<>();
		for (Issue issue : searchResults.getIssues()) {
			commentList.add(ComponentAccessor.getCommentManager().getComments(issue).size());
		}
		BufferedImage image = Plotter.getBoxPlot("Number of Comments per Issue", "#Comments", commentList);
		return createEncodedByteArray(image);

	}

	private byte[] createEncodedByteArray(RenderedImage bimage) {
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		try {
			ImageIO.write(bimage, "JPG", bOut);
		} catch (IOException e) {
			return null;
		}
		byte[] imageBytes = bOut.toByteArray();
		// Encode data on your side using BASE64
		byte[] bytesEncoded = Base64.encodeBase64(imageBytes);

		return bytesEncoded;
	}

	public void validate(ProjectActionSupport action, Map params) {
		projectId = ParameterUtils.getLongParam(params, "selectedProjectId");
	}
}