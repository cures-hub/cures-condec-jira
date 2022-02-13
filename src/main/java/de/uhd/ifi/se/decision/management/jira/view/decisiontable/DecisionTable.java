package de.uhd.ifi.se.decision.management.jira.view.decisiontable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.filtering.JiraQueryHandler;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.SolutionOption;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;

/**
 * Builds a criteria matrix/decision table for one selected decision problem.
 * The rows of the table are the {@link SolutionOption}s for the decision
 * problem. The columns of the table are criteria, e.g. non-functional
 * requirements such as performance. The cells of the table comprise
 * {@link Argument}s.
 */
public class DecisionTable {

	private String projectKey;
	private List<SolutionOption> alternatives;
	private Set<KnowledgeElement> criteria;

	private static final Logger LOGGER = LoggerFactory.getLogger(DecisionTable.class);

	public DecisionTable(String projectKey) {
		LOGGER.info("Decision table");
		this.projectKey = projectKey;
	}

	/**
	 * @param decisionProblem
	 *            decision problem as a {@link KnowledgeElement} object.
	 */
	public DecisionTable(KnowledgeElement decisionProblem) {
		this(decisionProblem.getProject().getProjectKey());
		alternatives = decisionProblem.getLinkedSolutionOptions();
		criteria = alternatives.stream().flatMap(alternative -> alternative.getArguments().stream())
				.flatMap(argument -> argument.getCriteria().stream()).collect(Collectors.toSet());
	}

	/**
	 * @param user
	 *            authenticated Jira {@link ApplicationUser}.
	 * @return all available criteria (e.g. quality requirements, non-functional
	 *         requirements) for a project. This can be much more criteria than the
	 *         criteria used for one specific decision problem.
	 */
	public List<KnowledgeElement> getAllDecisionTableCriteriaForProject(ApplicationUser user) {
		List<KnowledgeElement> criteria = new ArrayList<>();
		String query = getCriteriaQuery(projectKey);
		JiraQueryHandler queryHandler = new JiraQueryHandler(user, projectKey, "?jql=" + query);
		for (Issue jiraIssue : queryHandler.getJiraIssuesFromQuery()) {
			criteria.add(new KnowledgeElement(jiraIssue));
		}
		return criteria;
	}

	public static String getCriteriaQuery(String projectKey) {
		return ConfigPersistenceManager.getBasicConfiguration(projectKey).getCriteriaJiraQuery();
	}

	/**
	 * @return all solution options for a specific decision problem (=issue).
	 */
	@XmlElement
	public List<SolutionOption> getAlternatives() {
		return alternatives;
	}

	/**
	 * @return all criteria used in this decision table, i.e. the columns. Criteria
	 *         can be non-functional requirements such as performance.
	 */
	@XmlElement
	public Set<KnowledgeElement> getCriteria() {
		return criteria;
	}
}