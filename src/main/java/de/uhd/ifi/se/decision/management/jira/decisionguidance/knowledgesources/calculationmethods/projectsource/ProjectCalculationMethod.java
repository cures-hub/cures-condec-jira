package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.calculationmethods.projectsource;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.calculationmethods.CalculationMethod;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;
import de.uhd.ifi.se.decision.management.jira.view.decisiontable.Argument;

import java.util.ArrayList;
import java.util.List;

public abstract class ProjectCalculationMethod implements CalculationMethod {


	protected String projectKey;
	protected String projectSourceName;
	protected KnowledgePersistenceManager knowledgePersistenceManager;


	@Override
	public List<Recommendation> getResults(String inputs) {
		return new ArrayList<>();
	}

	@Override
	public List<Recommendation> getResults(KnowledgeElement knowledgeElement) {
		return new ArrayList<>();
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
