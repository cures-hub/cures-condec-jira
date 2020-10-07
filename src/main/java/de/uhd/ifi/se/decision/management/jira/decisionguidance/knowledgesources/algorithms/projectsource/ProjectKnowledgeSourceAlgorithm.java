package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.algorithms.projectsource;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.algorithms.KnowledgeSourceAlgorithm;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;
import de.uhd.ifi.se.decision.management.jira.view.decisiontable.Argument;

import java.util.ArrayList;
import java.util.List;

public abstract class ProjectKnowledgeSourceAlgorithm implements KnowledgeSourceAlgorithm {


	protected String projectKey;
	protected String projectSourceName;
	protected KnowledgePersistenceManager knowledgePersistenceManager;

	public void setData(String projectKey, String knowledgeSourceName) {
		this.projectKey = projectKey;
		this.projectSourceName = knowledgeSourceName;
		try {
			this.knowledgePersistenceManager = KnowledgePersistenceManager.getOrCreate(this.projectKey);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

	protected Recommendation createRecommendation(KnowledgeElement source, KnowledgeElement target, KnowledgeType... knowledgeTypes) {
		for (KnowledgeType knowledgeType : knowledgeTypes) {
			if (source.getType() == knowledgeType)
				return new Recommendation(this.projectSourceName, source.getSummary(), source.getUrl());
			if (target.getType() == knowledgeType)
				return new Recommendation(this.projectSourceName, target.getSummary(), target.getUrl());
		}

		return null;
	}

	protected boolean matchingIssueTypes(KnowledgeElement knowledgeElement, KnowledgeType... knowledgeTypes) {
		int matchedType = 0;
		for (KnowledgeType knowledgeType : knowledgeTypes) {
			if (knowledgeElement.getType() == knowledgeType) matchedType += 1;
		}
		return matchedType > 0;
	}

	protected List<Argument> getArguments(KnowledgeElement knowledgeElement) {
		List<Argument> arguments = new ArrayList<>();

		for (Link link : knowledgeElement.getLinks()) {
			KnowledgeElement source = link.getSource();
			KnowledgeElement target = link.getTarget();
			if (source.getType().equals(KnowledgeType.PRO) || source.getType().equals(KnowledgeType.CON)) {
				arguments.add(new Argument(source));
			}
			if (target.getType().equals(KnowledgeType.PRO) || target.getType().equals(KnowledgeType.CON)) {
				arguments.add(new Argument(target));
			}
		}

		return arguments;
	}

}
