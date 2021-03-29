package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.rdfsource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.InputMethod;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;

/**
 * Queries an RDF knowledge source (e.g. DBPedia) for a given
 * {@link KnowledgeElement} and its linked elements.
 * 
 * For example, a decision problem with alternatives and arguments already
 * documented can be used to query DBPedia.
 */
public class RDFSourceInputKnowledgeElement implements InputMethod<KnowledgeElement, RDFSource> {

	protected RDFSource knowledgeSource;
	protected String projectKey;
	protected String name;
	protected String service;
	protected String queryString;
	protected String timeout;
	protected int limit;

	@Override
	public void setKnowledgeSource(RDFSource knowledgeSource) {
		this.knowledgeSource = knowledgeSource;
		this.projectKey = knowledgeSource.getProjectKey();
		this.name = knowledgeSource.getName();
		this.service = knowledgeSource.getService();
		this.queryString = knowledgeSource.getQueryString();
		this.timeout = knowledgeSource.getTimeout();
		this.limit = knowledgeSource.getLimit();
	}

	@Override
	public List<Recommendation> getRecommendations(KnowledgeElement knowledgeElement) {
		if (knowledgeElement == null) {
			return new ArrayList<>();
		}
		List<Recommendation> recommendations = new ArrayList<>();
		RDFSourceInputString rdfSourceInputString = new RDFSourceInputString();
		rdfSourceInputString.setKnowledgeSource(knowledgeSource);

		for (Link link : knowledgeElement.getLinks()) {
			for (KnowledgeElement linkedElement : link.getBothElements()) {
				if (linkedElement.getType() == KnowledgeType.ALTERNATIVE
						|| linkedElement.getType() == KnowledgeType.DECISION) {
					List<Recommendation> recommendationFromAlternative = rdfSourceInputString
							.getRecommendations(linkedElement.getSummary());
					recommendations.addAll(recommendationFromAlternative);
				}
			}
		}

		return recommendations.stream().distinct().collect(Collectors.toList());
	}
}
