package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.rdfsource;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.InputMethod;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.KnowledgeSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.recommender.RecommenderType;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;

import java.util.Objects;

public class RDFSource extends KnowledgeSource {

	protected String service;
	protected String queryString;
	protected String timeout;
	protected int limit;

	public RDFSource() {

	}

	/**
	 * @param projectKey
	 */
	public RDFSource(String projectKey) {
		this.projectKey = projectKey;
		this.service = "http://dbpedia.org/sparql";
		this.queryString = "PREFIX dbo: <http://dbpedia.org/ontology/> " +
			"PREFIX dbr: <http://dbpedia.org/resource/> SELECT ?alternative ?url WHERE { ?alternative a dbo:Country." +
			"?url dbo:capital ?alternative }";
		this.name = "DBPedia";
		this.timeout = "30000";
		this.limit = 10;
		this.isActivated = true;
	}

	/**
	 * @param projectKey
	 * @param service
	 * @param queryString
	 * @param name
	 * @param timeout
	 */
	public RDFSource(String projectKey, String service, String queryString, String name, String timeout) {
		this.projectKey = projectKey;
		this.service = service;
		this.queryString = queryString;
		this.name = name;
		this.timeout = timeout;
		this.isActivated = true;
		this.limit = 10;
	}

	@Override
	public void setData() {
		if (ConfigPersistenceManager.getRecommendationInput(projectKey).equals(RecommenderType.KEYWORD))
			((RDFSourceInputString) this.inputMethod).setData(projectKey, name, service, queryString, timeout, limit);
		else {
			((RDFSourceInputKnowledgeElement) this.inputMethod).setData(projectKey, name, service, queryString, timeout, limit);
		}
	}


	@Override
	public InputMethod getInputMethod() {
		if (ConfigPersistenceManager.getRecommendationInput(projectKey).equals(RecommenderType.KEYWORD)) {
			this.inputMethod = new RDFSourceInputString();
		} else {
			this.inputMethod = new RDFSourceInputKnowledgeElement();
		}
		return this.inputMethod;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getQueryString() {
		return queryString;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	public String getTimeout() {
		return timeout;
	}

	public void setTimeout(String timeout) {
		this.timeout = timeout;
	}

	public int getLimit() {
		return ConfigPersistenceManager.getMaxNumberRecommendations(this.projectKey);
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	@Override
	public String toString() {
		return this.name.replace(' ', '-');
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		RDFSource rdfSource = (RDFSource) o;
		return name.equals(rdfSource.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}
}
