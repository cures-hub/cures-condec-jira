package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.LinkType;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProjectSource implements KnowledgeSource {

	private String projectKey;
	KnowledgePersistenceManager knowledgePersistenceManager;

	public ProjectSource(String projectKey) {
		this.projectKey = projectKey;
		try {
			this.knowledgePersistenceManager = KnowledgePersistenceManager.getOrCreate(this.projectKey);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

	protected List<KnowledgeElement> queryDatabase() {
		return this.knowledgePersistenceManager != null ? this.knowledgePersistenceManager.getKnowledgeElements() : null;
	}

	@Override
	public List<KnowledgeElement> getResults(String inputs) {
		List<KnowledgeElement> recommendations = new ArrayList<>();

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
						.forEach(child -> recommendations.add(child.getTarget()));
				}
			});
		}

		return recommendations;
	}
}
