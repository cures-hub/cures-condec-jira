package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProjectSource implements KnowledgeSource {

	private String projectKey;
	private String projectSourceName;
	private boolean isActivated;
	KnowledgePersistenceManager knowledgePersistenceManager;

	public ProjectSource(String projectKey) {
		this.projectKey = projectKey;
		try {
			this.knowledgePersistenceManager = KnowledgePersistenceManager.getOrCreate(this.projectKey);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		this.isActivated = false;
	}

	public ProjectSource(String projectKey, String projectSourceName, boolean isActivated) {
		this.projectKey = projectKey;
		this.projectSourceName = projectSourceName;
		this.isActivated = isActivated;
		try {
			this.knowledgePersistenceManager = KnowledgePersistenceManager.getOrCreate(projectSourceName);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

	protected List<KnowledgeElement> queryDatabase() {
		return this.knowledgePersistenceManager != null ? this.knowledgePersistenceManager.getKnowledgeElements() : null;
	}

	@Override
	public String getName() {
		return this.projectSourceName;
	}

	@Override
	public void setName(String name) {
		this.projectSourceName = name;
	}

	@Override
	public boolean isActivated() {
		return this.isActivated;
	}

	@Override
	public void setActivated(boolean activated) {
	}

	@Override
	public List<Recommendation> getResults(String inputs) {
		List<Recommendation> recommendations = new ArrayList<>();

		List<KnowledgeElement> knowledgeElements = this.queryDatabase();

		if (knowledgeElements != null) {

			//filter all knowledge elements by the type "issue"
			List<KnowledgeElement> issues = knowledgeElements
				.stream()
				.filter(knowledgeElement -> knowledgeElement.getType() == KnowledgeType.ISSUE)
				.collect(Collectors.toList());

			//get all alternatives, which parent contains the pattern"
			issues.forEach(issue -> {
				if (issue.getSummary().contains(inputs)) {
					issue.getLinks().stream()
						.filter(link -> link.getTarget().getType() == KnowledgeType.ALTERNATIVE)
						.forEach(child -> {
							Recommendation recommendation = new Recommendation(this.projectSourceName, child.getTarget());
							recommendations.add(recommendation);
						});
				}
			});
		}
		return recommendations;
	}
}
