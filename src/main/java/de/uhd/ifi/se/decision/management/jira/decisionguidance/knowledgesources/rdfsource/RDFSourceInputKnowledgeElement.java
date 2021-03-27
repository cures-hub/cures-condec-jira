package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.rdfsource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.InputMethod;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;

public class RDFSourceInputKnowledgeElement implements InputMethod<KnowledgeElement, RDFSource> {

	protected RDFSource knowledgeSource;
	protected String projectKey;
	protected String name;
	protected String service;
	protected String queryString;
	protected String timeout;
	protected int limit;

	@Override
	public void setData(RDFSource knowledgeSource) {
		this.knowledgeSource = knowledgeSource;
		this.projectKey = this.knowledgeSource.getProjectKey();
		this.name = this.knowledgeSource.getName();
		this.service = this.knowledgeSource.getService();
		this.queryString = this.knowledgeSource.getQueryString();
		this.timeout = this.knowledgeSource.getTimeout();
		this.limit = this.knowledgeSource.getLimit();
	}

	@Override
	public List<Recommendation> getResults(KnowledgeElement knowledgeElement) {

		List<Recommendation> recommendations = new ArrayList<>();
		if (knowledgeElement == null)
			return recommendations;

		RDFSourceInputString rdfSourceInputString = new RDFSourceInputString();
		rdfSourceInputString.setData(this.knowledgeSource);

		for (Link link : knowledgeElement.getLinks()) {
			for (KnowledgeElement linkedElement : link.getBothElements()) {
				if (linkedElement.getType() == KnowledgeType.ALTERNATIVE
						|| linkedElement.getType() == KnowledgeType.DECISION) {
					List<Recommendation> recommendationFromAlternative = rdfSourceInputString
							.getResults(linkedElement.getSummary());
					recommendations.addAll(recommendationFromAlternative);
				}
			}
		}

		return recommendations.stream().distinct().collect(Collectors.toList());
	}
}
