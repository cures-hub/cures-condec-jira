package de.uhd.ifi.se.decision.management.jira.releasenotes.impl;

import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.comments.CommentManager;
import com.atlassian.jira.issue.priority.Priority;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.jql.builder.JqlQueryBuilder;
import com.atlassian.jira.user.ApplicationUser;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.releasenotes.ReleaseNoteIssueProposal;
import de.uhd.ifi.se.decision.management.jira.releasenotes.TaskCriteriaPrioritisation;
import org.codehaus.jackson.annotate.JsonProperty;

import javax.xml.bind.annotation.XmlElement;
import java.util.EnumMap;
import java.util.HashMap;


/**
 * Model class Release Note Issue Proposal
 * It saves the decision knowledge element, the final rating and the task criteria prioritisation metrics.
 */
public class ReleaseNoteIssueProposalImpl implements ReleaseNoteIssueProposal {

	private DecisionKnowledgeElement decisionKnowledgeElement;

	private EnumMap<TaskCriteriaPrioritisation, Integer> taskCriteriaPrioritisation;
	private double rating;

	/**
	 * Constructer to initialize default values and add count of DK
	 *
	 * @param decisionKnowledgeElement
	 * @param countDecisionKnowledge
	 */
	public ReleaseNoteIssueProposalImpl(DecisionKnowledgeElement decisionKnowledgeElement, int countDecisionKnowledge) {
		this.decisionKnowledgeElement = decisionKnowledgeElement;
		//set default values
		this.taskCriteriaPrioritisation = TaskCriteriaPrioritisation.toIntegerEnumMap();
		this.taskCriteriaPrioritisation.put(TaskCriteriaPrioritisation.COUNT_DECISION_KNOWLEDGE, countDecisionKnowledge);

	}

	/**
	 * @return decisionKnowledgeElement of the ReleaseNoteIssueProposal
	 */
	@Override
	@XmlElement(name = "decisionKnowledgeElement")
	public DecisionKnowledgeElement getDecisionKnowledgeElement() {
		return this.decisionKnowledgeElement;
	}

	/**
	 * @param decisionKnowledgeElement of the ReleaseNoteIssueProposal.
	 */
	@Override
	@JsonProperty("decisionKnowledgeElement")
	public void setDecisionKnowledgeElement(DecisionKnowledgeElement decisionKnowledgeElement) {
		this.decisionKnowledgeElement = decisionKnowledgeElement;
	}

	/**
	 * @return rating of the ReleaseNoteIssueProposal
	 */
	@Override
	@XmlElement(name = "rating")
	public double getRating() {
		return this.rating;
	}

	/**
	 * @param rating of the ReleaseNoteIssueProposal.
	 */
	@Override
	@JsonProperty("rating")
	public void setRating(double rating) {
		this.rating = rating;
	}

	/**
	 * Get criteria Prioritisation of the ReleaseNoteIssueProposal.
	 *
	 * @return taskCriteriaPrioritisation of the ReleaseNoteIssueProposal.
	 */
	@Override
	@XmlElement(name = "taskCriteriaPrioritisation")
	public EnumMap<TaskCriteriaPrioritisation, Integer> getTaskCriteriaPrioritisation() {
		return this.taskCriteriaPrioritisation;
	}

	/**
	 * set criteria Prioritisation of the ReleaseNoteIssueProposal.
	 *
	 * @param taskCriteriaPrioritisation of the ReleaseNoteIssueProposal.
	 */
	@Override
	public void setTaskCriteriaPrioritisation(EnumMap<TaskCriteriaPrioritisation, Integer> taskCriteriaPrioritisation) {
		this.taskCriteriaPrioritisation = taskCriteriaPrioritisation;
	}
	/**
	 * Gets the priority of the issue and sets the priority criteria of the ReleaseNoteIssueProposal
	 * @param issue of the associated DecisionKnowledgeElement
	 */
	public void getAndSetPriority(Issue issue) {
		Priority priority = issue.getPriority();
		if (priority != null) {
			int sequence = Math.toIntExact(priority.getSequence());
			this.getTaskCriteriaPrioritisation().put(TaskCriteriaPrioritisation.PRIORITY, sequence);
		} else {
			//set medium value for DK elements for priority
			this.getTaskCriteriaPrioritisation().put(TaskCriteriaPrioritisation.PRIORITY, 3);
		}
	}
	/**
	 * Gets the amount of comments of the issue and sets the count comment criteria of the ReleaseNoteIssueProposal
	 * @param issue of the associated DecisionKnowledgeElement
	 */
	public void getAndSetCountOfComments(Issue issue) {
		CommentManager commentManager = ComponentAccessor.getCommentManager();
		int countComments = commentManager.getComments(issue).size();
		this.getTaskCriteriaPrioritisation().put(TaskCriteriaPrioritisation.COUNT_COMMENTS, countComments);
	}
	/**
	 * Gets the size of the summary and sets the size summary criteria of the ReleaseNoteIssueProposal
	 */
	public void getAndSetSizeOfSummary() {
		int sizeSummary = countWordsUsingSplit(this.getDecisionKnowledgeElement().getSummary());
		this.getTaskCriteriaPrioritisation().put(TaskCriteriaPrioritisation.SIZE_SUMMARY, sizeSummary);
	}
	/**
	 * Gets the size of the description and sets the size description criteria of the ReleaseNoteIssueProposal
	 */
	public void getAndSetSizeOfDescription() {
		int sizeDescription = countWordsUsingSplit(this.getDecisionKnowledgeElement().getDescription());
		this.getTaskCriteriaPrioritisation().put(TaskCriteriaPrioritisation.SIZE_DESCRIPTION, sizeDescription);
	}
	/**
	 * Gets the days to completion of the issue and sets the days to completion criteria of the ReleaseNoteIssueProposal
	 * @param issue of the associated DecisionKnowledgeElement
	 */
	public void getAndSetDaysToCompletion( Issue issue) {
		Long created = issue.getCreated().getTime();
		Long resolved = issue.getResolutionDate().getTime();
		Long diff = resolved - created;
		int days = (int) Math.floor(diff / (1000 * 60 * 60 * 24));
		this.getTaskCriteriaPrioritisation().put(TaskCriteriaPrioritisation.DAYS_COMPLETION, days);
	}
	/**
	 * Gets the total count of created issues of the issue reporter and sets the experienceReporter criteria
	 * of the ReleaseNoteIssueProposal. The existing Reporter count HashMap is used to avoid duplicated equal JQL queries.
	 * The result may differ, depending of the logged-in user and his permissions.
	 * @param issue of the associated DecisionKnowledgeElement
	 * @param existingReporterCount HashMap to save JQL results
	 * @param user Application user which makes the request
	 */
	public void getAndSetExperienceReporter(Issue issue, HashMap<String, Integer> existingReporterCount, ApplicationUser user) {
		//first check if user was already checked
		SearchService searchProvider = ComponentAccessor.getComponentOfType(SearchService.class);
		String reporterId = issue.getReporterId();
		if (reporterId == null) {
			reporterId = issue.getReporter().getKey();
		}
		Integer reporterExistingCount = existingReporterCount.get(reporterId);
		Integer countReporter = 0;

		if (reporterExistingCount != null) {
			countReporter = reporterExistingCount;
		} else {
			JqlQueryBuilder builder = JqlQueryBuilder.newBuilder();
			builder
					.where()
					.reporterUser(reporterId);
			try {
				countReporter = Math.toIntExact(searchProvider.searchCount(user, builder.buildQuery()));
			} catch (SearchException e) {
				e.printStackTrace();
			}
			existingReporterCount.put(reporterId, countReporter);
		}
		this.getTaskCriteriaPrioritisation().put(TaskCriteriaPrioritisation.EXPERIENCE_REPORTER, (int) countReporter);
	}

	/**
	 * Gets the total count of resolved issues of the issue resolver and sets the experienceResolver criteria
	 * of the ReleaseNoteIssueProposal. The existing Resolver count HashMap is used to avoid duplicated equal JQL queries.
	 * The result may differ, depending of the logged-in user and his permissions.
	 * @param issue of the associated DecisionKnowledgeElement
	 * @param existingResolverCount HashMap to save JQL results
	 * @param user Application user which makes the request
	 */
	public void getAndSetExperienceResolver(Issue issue, HashMap<String, Integer> existingResolverCount, ApplicationUser user) {
		//the resolver is most of the times the last assigned user
		JqlQueryBuilder builderResolver = JqlQueryBuilder.newBuilder();
		SearchService searchProvider = ComponentAccessor.getComponentOfType(SearchService.class);

		String assigneeId = issue.getAssigneeId();
		//not all issues have assigneeId, if it is null use the reporterId
		if (assigneeId == null) {
			assigneeId = issue.getReporterId();
			if(assigneeId== null){
				assigneeId=issue.getReporter().getKey();
			}
		}
		//first check if user was already checked
		Integer resolverExistingCount = existingResolverCount.get(assigneeId);
		int countResolver = 0;

		if (resolverExistingCount != null) {
			countResolver = resolverExistingCount;
		} else {
			builderResolver
					.where()
					.status("resolved")
					.and()
					.assigneeUser(assigneeId);
			try {
				countResolver = Math.toIntExact(searchProvider.searchCount(user, builderResolver.buildQuery()));
			} catch (SearchException e) {
				e.printStackTrace();
			}
			existingResolverCount.put(assigneeId, countResolver);
		}
		this.getTaskCriteriaPrioritisation().put(TaskCriteriaPrioritisation.EXPERIENCE_RESOLVER, (int) countResolver);

	}



	/**
	 * Count words of a string
	 *
	 * @param input of string with space separated words
	 * @return count of words
	 */
	private int countWordsUsingSplit(String input) {
		if (input == null || input.isEmpty()) {
			return 0;
		}

		String[] words = input.split("\\s+");
		return words.length;
	}




}