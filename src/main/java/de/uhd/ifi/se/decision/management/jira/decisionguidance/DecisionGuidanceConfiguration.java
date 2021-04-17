package de.uhd.ifi.se.decision.management.jira.decisionguidance;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.codehaus.jackson.annotate.JsonProperty;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.Project;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.projectsource.ProjectSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.rdfsource.RDFSource;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;

/**
 * Contains the configuration details for the recommendation of knowledge
 * elements from external knowledge sources (i.e. decision guidance) for one
 * Jira project (see {@link DecisionKnowledgeProject}).
 * 
 * For example, specifies the {@link RDFSource}s and {@link ProjectSource}s that
 * are used as external knowledge sources.
 */
public class DecisionGuidanceConfiguration {

	private boolean isRecommendationAddedToKnowledgeGraph;
	private int maxNumberOfRecommendations;
	private double similarityThreshold;
	private String irrelevantWords;
	private List<RDFSource> rdfKnowledgeSources;
	private List<ProjectSource> projectKnowledgeSources;

	/**
	 * Constructs an object with default values.
	 */
	public DecisionGuidanceConfiguration() {
		this.setRecommendationAddedToKnowledgeGraph(false);
		this.setMaxNumberOfRecommendations(100);
		this.setSimilarityThreshold(0.85);
		this.setIrrelevantWords("");
		this.rdfKnowledgeSources = new ArrayList<>();
		this.projectKnowledgeSources = new ArrayList<>();
	}

	/**
	 * @return true if all recommendations for a decision problem are directly added
	 *         to the knowledge graph. Currently, new Jira issue comments are
	 *         created for every recommendation!
	 */
	public boolean isRecommendationAddedToKnowledgeGraph() {
		return isRecommendationAddedToKnowledgeGraph;
	}

	/**
	 * @param isRecommendationAddedToKnowledgeGraph
	 *            true if all recommendations for a decision problem are directly
	 *            added to the knowledge graph.
	 */
	@JsonProperty
	public void setRecommendationAddedToKnowledgeGraph(boolean isRecommendationAddedToKnowledgeGraph) {
		this.isRecommendationAddedToKnowledgeGraph = isRecommendationAddedToKnowledgeGraph;
	}

	/**
	 * @return maximum number of recommendations from an external knowledge source
	 *         that is shown to the user.
	 */
	public int getMaxNumberOfRecommendations() {
		return maxNumberOfRecommendations;
	}

	/**
	 * @param maxNumberOfRecommendations
	 *            maximum number of recommendations from an external knowledge
	 *            source that is shown to the user.
	 */
	@JsonProperty
	public void setMaxNumberOfRecommendations(int maxNumberOfRecommendations) {
		this.maxNumberOfRecommendations = maxNumberOfRecommendations;
	}

	/**
	 * @return similarity score for textual similarity.
	 */
	public double getSimilarityThreshold() {
		return similarityThreshold;
	}

	/**
	 * @param similarityThreshold
	 *            similarity score for textual similarity.
	 */
	@JsonProperty
	public void setSimilarityThreshold(double similarityThreshold) {
		this.similarityThreshold = similarityThreshold;
	}

	/**
	 * @return words that should not be involved in the score calculation.
	 */
	public String getIrrelevantWords() {
		return irrelevantWords;
	}

	/**
	 * @param irrelevantWords
	 *            that should not be involved in the score calculation.
	 */
	@JsonProperty
	public void setIrrelevantWords(String irrelevantWords) {
		this.irrelevantWords = irrelevantWords;
	}

	/**
	 * @return known {@link RDFSource}s that are either activated or deactivated.
	 * @see RDFSource#isActivated()
	 */
	public List<RDFSource> getRDFKnowledgeSources() {
		return rdfKnowledgeSources;
	}

	/**
	 * @param rdfKnowledgeSources
	 *            {@link RDFSource}s, either activated or deactivated.
	 */
	@JsonProperty
	public void setRDFKnowledgeSources(List<RDFSource> rdfKnowledgeSources) {
		this.rdfKnowledgeSources = rdfKnowledgeSources;
	}

	/**
	 * @param rdfSource
	 *            {@link RDFSource}, either activated or deactivated.
	 */
	public void addRDFKnowledgeSource(RDFSource rdfSource) {
		if (rdfSource == null) {
			return;
		}
		rdfSource.setActivated(true); // default: activated
		rdfKnowledgeSources.add(rdfSource);
	}

	public boolean containsRDFKnowledgeSource(String rdfSourceName) {
		for (RDFSource rdfSourceCheck : rdfKnowledgeSources) {
			if (rdfSourceCheck.getName().equals(rdfSourceName))
				return true;
		}
		return false;
	}

	/**
	 * @param knowledgeSourceName
	 *            of an {@link RDFSource}.
	 */
	public void deleteRDFKnowledgeSource(String knowledgeSourceName) {
		rdfKnowledgeSources.removeIf(rdfSource -> knowledgeSourceName.equals(rdfSource.getName()));
	}

	/**
	 * @param knowledgeSourceName
	 *            of an existing {@link RDFSource}.
	 * @param rdfSource
	 *            updated {@link RDFSource} object.
	 */
	public void updateRDFKnowledgeSource(String knowledgeSourceName, RDFSource rdfSource) {
		for (int i = 0; i < rdfKnowledgeSources.size(); ++i) {
			if (rdfKnowledgeSources.get(i).getName().equals(knowledgeSourceName)) {
				rdfKnowledgeSources.set(i, rdfSource);
				break;
			}
		}
	}

	/**
	 * @param rdfSourceName
	 *            of an existing {@link RDFSource}.
	 * @param isActivated
	 *            true if {@link RDFSource} is activated.
	 * @see RDFSource#isActivated()
	 */
	public void setRDFKnowledgeSourceActivation(String rdfSourceName, boolean isActivated) {
		for (int i = 0; i < rdfKnowledgeSources.size(); ++i) {
			if (rdfSourceName.equals(rdfKnowledgeSources.get(i).getName())) {
				rdfKnowledgeSources.get(i).setActivated(isActivated);
				break;
			}
		}
	}

	/**
	 * @return all possible {@link ProjectSource}s that are either activated or
	 *         deactivated.
	 */
	public List<ProjectSource> getProjectKnowledgeSources() {
		List<ProjectSource> projectSources = new ArrayList<>();
		for (Project project : ComponentAccessor.getProjectManager().getProjects()) {
			DecisionKnowledgeProject jiraProject = new DecisionKnowledgeProject(project);
			if (!jiraProject.isActivated()) {
				continue;
			}
			ProjectSource projectSource = getProjectSource(jiraProject.getProjectKey());
			if (projectSource == null) {
				projectSource = new ProjectSource(jiraProject);
			}
			projectSources.add(projectSource);
		}
		projectKnowledgeSources = projectSources;
		return projectSources;
	}

	/**
	 * @param projectKnowledgeSources
	 *            {@link ProjectSource}s that are either activated or deactivated.
	 */
	@JsonProperty
	public void setProjectKnowledgeSources(List<ProjectSource> projectKnowledgeSources) {
		this.projectKnowledgeSources = projectKnowledgeSources;
	}

	/**
	 * @param projectSourceKey
	 *            Jira project key.
	 * @return either the existing {@link ProjectSource} object or null if not
	 *         existing.
	 */
	public ProjectSource getProjectSource(String projectSourceKey) {
		for (ProjectSource projectSource : projectKnowledgeSources) {
			if (projectSource.getProjectKey().equals(projectSourceKey)) {
				return projectSource;
			}
		}
		return null;
	}

	/**
	 * @param projectSourceKey
	 *            Jira project key.
	 * @param isActivated
	 *            true if {@link ProjectSource} should be activated.
	 */
	public void setProjectKnowledgeSource(String projectSourceKey, boolean isActivated) {
		for (ProjectSource projectSource : projectKnowledgeSources) {
			if (projectSource.getProjectKey().equalsIgnoreCase(projectSourceKey)) {
				projectSource.setActivated(isActivated);
				return;
			}
		}
		projectKnowledgeSources.add(new ProjectSource(projectSourceKey, isActivated));
	}

	public List<KnowledgeSource> getAllKnowledgeSources() {
		List<KnowledgeSource> knowledgeSources = new ArrayList<>();
		knowledgeSources.addAll(rdfKnowledgeSources);
		knowledgeSources.addAll(getProjectKnowledgeSources());
		// New KnowledgeSources could be added here.
		return knowledgeSources;
	}

	public List<KnowledgeSource> getAllActivatedKnowledgeSources() {
		List<KnowledgeSource> knowledgeSources = getAllKnowledgeSources();
		knowledgeSources.removeIf(knowledgeSource -> !knowledgeSource.isActivated());
		return knowledgeSources;
	}

	public KnowledgeSource getKnowledgeSourceByName(String knowledgeSourceName) {
		Optional<KnowledgeSource> knowledgeSourceWithName = getAllKnowledgeSources().stream()
				.filter(source -> source.getName().equals(knowledgeSourceName)).findAny();
		return knowledgeSourceWithName.isPresent() ? knowledgeSourceWithName.get() : null;
	}

}
