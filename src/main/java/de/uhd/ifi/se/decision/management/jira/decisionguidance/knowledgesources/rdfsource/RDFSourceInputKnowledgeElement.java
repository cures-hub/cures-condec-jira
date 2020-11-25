package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.rdfsource;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.InputMethod;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RDFSourceInputKnowledgeElement implements InputMethod<KnowledgeElement> {

	protected String projectKey;
	protected String name;
	protected String service;
	protected String queryString;
	protected String timeout;
	protected int limit;

	public InputMethod setData(String projectKey, String name, String service, String queryName, String timeout, int limit) {
		this.projectKey = projectKey;
		this.name = name;
		this.service = service;
		this.queryString = queryName;
		this.timeout = timeout;
		this.limit = limit;
		return this;
	}

	@Override
	public List<Recommendation> getResults(KnowledgeElement knowledgeElement) {

		List<Recommendation> recommendations = new ArrayList<>();
		if (knowledgeElement == null) return recommendations;

		RDFSourceInputString rdfSourceInputString = new RDFSourceInputString();
		rdfSourceInputString.setData(projectKey, name, service, queryString, timeout, limit);

		for (Link link : knowledgeElement.getLinks()) {
			for (KnowledgeElement linkedElement : link.getBothElements()) {
				if (linkedElement.getType().equals(KnowledgeType.ALTERNATIVE) || linkedElement.getType().equals(KnowledgeType.DECISION)) {
					List<Recommendation> recommendationFromAlternative = rdfSourceInputString.getResults(linkedElement.getSummary());
					recommendations.addAll(recommendationFromAlternative);
				}
			}
		}



		return recommendations.stream().distinct().collect(Collectors.toList());
	}
}
