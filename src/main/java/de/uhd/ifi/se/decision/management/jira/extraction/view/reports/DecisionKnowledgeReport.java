package de.uhd.ifi.se.decision.management.jira.extraction.view.reports;

import com.atlassian.jira.bc.JiraServiceContext;
import com.atlassian.jira.bc.JiraServiceContextImpl;
import com.atlassian.jira.bc.filter.SearchRequestService;
import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.datetime.DateTimeFormatter;
import com.atlassian.jira.datetime.DateTimeFormatterFactory;
import com.atlassian.jira.datetime.DateTimeStyle;
import com.atlassian.jira.exception.PermissionException;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.IssueFactory;
import com.atlassian.jira.issue.fields.FieldManager;
import com.atlassian.jira.issue.index.IssueIndexManager;
import com.atlassian.jira.issue.search.ReaderCache;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchProvider;
import com.atlassian.jira.issue.search.SearchRequest;
import com.atlassian.jira.issue.statistics.FilterStatisticsValuesGenerator;
import com.atlassian.jira.issue.statistics.StatisticsMapper;
import com.atlassian.jira.issue.statistics.StatsGroup;
import com.atlassian.jira.issue.statistics.util.OneDimensionalDocIssueHitCollector;
import com.atlassian.jira.plugin.report.impl.AbstractReport;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.util.SimpleErrorCollection;
import com.atlassian.jira.web.FieldVisibilityManager;
import com.atlassian.jira.web.action.ProjectActionSupport;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport;
import com.atlassian.util.profiling.UtilTimerStack;
import com.google.common.collect.ImmutableMap;
import com.opensymphony.util.TextUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.search.Collector;

import java.util.Arrays;
import java.util.Map;

public class DecisionKnowledgeReport extends AbstractReport {
    private static final Logger log = Logger.getLogger(DecisionKnowledgeReport.class);

    @JiraImport
    private final SearchProvider searchProvider;
    @JiraImport
    private final SearchRequestService searchRequestService;
    @JiraImport
    private final IssueFactory issueFactory;
    @JiraImport
    private final CustomFieldManager customFieldManager;
    @JiraImport
    private final IssueIndexManager issueIndexManager;
    @JiraImport
    private final SearchService searchService;
    @JiraImport
    private final FieldVisibilityManager fieldVisibilityManager;
    @JiraImport
    private final FieldManager fieldManager;
    @JiraImport
    private final ProjectManager projectManager;
    @JiraImport
    private final ReaderCache readerCache;
    private final DateTimeFormatter formatter;

    public DecisionKnowledgeReport(final SearchProvider searchProvider,
                                            final SearchRequestService searchRequestService, final IssueFactory issueFactory,
                                            final CustomFieldManager customFieldManager, final IssueIndexManager issueIndexManager,
                                            final SearchService searchService, final FieldVisibilityManager fieldVisibilityManager,
                                            final ReaderCache readerCache,
                                            final FieldManager fieldManager,
                                            final ProjectManager projectManager,
                                            @JiraImport DateTimeFormatterFactory dateTimeFormatterFactory) {
        this.searchProvider = searchProvider;
        this.searchRequestService = searchRequestService;
        this.issueFactory = issueFactory;
        this.customFieldManager = customFieldManager;
        this.issueIndexManager = issueIndexManager;
        this.searchService = searchService;
        this.fieldVisibilityManager = fieldVisibilityManager;
        this.readerCache = readerCache;
        this.fieldManager = fieldManager;
        this.projectManager = projectManager;
        this.formatter = dateTimeFormatterFactory.formatter().withStyle(DateTimeStyle.DATE).forLoggedInUser();
    }

    public StatsGroup getOptions(SearchRequest sr, ApplicationUser user, StatisticsMapper mapper) throws PermissionException {
        try {
            return searchMapIssueKeys(sr, user, mapper);
        } catch (SearchException e) {
            log.error("Exception rendering " + this.getClass().getName() + ".  Exception \n" + Arrays.toString(e.getStackTrace()));
            return null;
        }
    }

    public StatsGroup searchMapIssueKeys(SearchRequest request, ApplicationUser searcher, StatisticsMapper mapper) throws SearchException {
        try {
            UtilTimerStack.push("Search Count Map");
            StatsGroup statsGroup = new StatsGroup(mapper);
            Collector hitCollector = new OneDimensionalDocIssueHitCollector(mapper.getDocumentConstant(), statsGroup,
                    issueIndexManager.getIssueSearcher().getIndexReader(), issueFactory,
                    fieldVisibilityManager, readerCache, fieldManager, projectManager);
            searchProvider.searchAndSort((request != null) ? request.getQuery() : null, searcher, hitCollector, PagerFilter.getUnlimitedFilter());
            return statsGroup;
        } finally {
            UtilTimerStack.pop("Search Count Map");
        }
    }

    public String generateReportHtml(ProjectActionSupport action, Map params) throws Exception {

        String filterId = (String) params.get("filterid");
        if (filterId == null) {
            log.info("Single Level Group By Report run without a project selected (JRA-5042): params=" + params);
            return "<span class='errMsg'>No search filter has been selected. Please "
                    + "<a href=\"IssueNavigator.jspa?reset=Update&pid="
                    + TextUtils.htmlEncode((String) params.get("selectedProjectId"))
                    + "\">create one</a>, and re-run this report.";
        }
        String mapperName = (String) params.get("mapper");
        final StatisticsMapper mapper = new FilterStatisticsValuesGenerator().getStatsMapper(mapperName);
        final JiraServiceContext ctx = new JiraServiceContextImpl(action.getLoggedInUser());
        final SearchRequest request = searchRequestService.getFilter(ctx, new Long(filterId));

        try {
            final Map startingParams = ImmutableMap.builder()
                    .put("action", action)
                    .put("statsGroup", getOptions(request, action.getLoggedInUser(), mapper))
                    .put("searchRequest", request)
                    .put("mapperType", mapperName)
                    .put("customFieldManager", customFieldManager)
                    .put("fieldVisibility", fieldVisibilityManager)
                    .put("searchService", searchService)
                    .put("portlet", this)
                    .put("formatter", formatter).build();

            return descriptor.getHtml("view", startingParams);

        } catch (PermissionException e) {
            log.error(e.getStackTrace());
            return null;
        }
    }

    public void validate(ProjectActionSupport action, Map params) {
        super.validate(action, params);
        String filterId = (String) params.get("filterid");
        if (StringUtils.isEmpty(filterId)) {
            action.addError("filterid", action.getText("report.singlelevelgroupby.filter.is.required"));
        } else {
            validateFilterId(action, filterId);
        }
    }

    private void validateFilterId(ProjectActionSupport action, String filterId) {
        try {
            JiraServiceContextImpl serviceContext = new JiraServiceContextImpl(
                    action.getLoggedInUser(), new SimpleErrorCollection());
            SearchRequest searchRequest = searchRequestService.getFilter(serviceContext, new Long(filterId));
            if (searchRequest == null) {
                action.addErrorMessage(action.getText("report.error.no.filter"));
            }
        } catch (NumberFormatException nfe) {
            action.addError("filterId", action.getText("report.error.filter.id.not.a.number", filterId));
        }
    }
}