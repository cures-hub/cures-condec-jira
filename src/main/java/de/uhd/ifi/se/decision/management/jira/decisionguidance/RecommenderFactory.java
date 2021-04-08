package de.uhd.ifi.se.decision.management.jira.decisionguidance;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.projectsource.ProjectSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.projectsource.ProjectSourceRecommender;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.rdfsource.RDFSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.rdfsource.RDFSourceRecommender;

/**
 * Determines the Recommender based on the {@link RecommenderType}.
 */
public class RecommenderFactory {

	public static Recommender getRecommender(String projectKey, KnowledgeSource knowledgeSource) {
		if (knowledgeSource instanceof ProjectSource) {
			return new ProjectSourceRecommender(projectKey, (ProjectSource) knowledgeSource);
		}
		return new RDFSourceRecommender(projectKey, (RDFSource) knowledgeSource);
	}
}
