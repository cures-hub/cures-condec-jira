package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.rdfsource;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.InputMethod;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.KnowledgeSource;

import java.util.Objects;

public class RDFSource extends KnowledgeSource {

	protected String service;
	protected String queryString;
	protected String timeout;
	protected int limit;
	protected String constraint;

	private static final String PREFIX = "PREFIX dbo: <http://dbpedia.org/ontology/>" +
		"PREFIX dct: <http://purl.org/dc/terms/>" +
		"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
		"PREFIX foaf: <http://xmlns.com/foaf/0.1/>";

	public RDFSource() {

	}

	/**
	 * @param projectKey
	 */
	public RDFSource(String projectKey) {
		this.projectKey = projectKey;
		this.service = "http://dbpedia.org/sparql";
		this.queryString = PREFIX + " select distinct ?subject ?url count(?link)   where { " +
			"%variable% dbo:genre ?genre. " +
			"?subject dbo:genre ?genre. " +
			"?subject foaf:isPrimaryTopicOf ?url. " +
			"?subject dbo:wikiPageExternalLink ?link.} GROUP BY ?subject ?url ";
		this.name = "DBPedia";
		this.timeout = "30000";
		this.limit = 10;
		this.isActivated = true;
		this.icon = "aui-iconfont-download";
		this.constraint = "";
	}


	/**
	 * @param projectKey
	 * @param service
	 * @param queryString
	 * @param name
	 * @param timeout
	 */
	public RDFSource(String projectKey, String service, String queryString, String name, String timeout, int limit, String constraint) {
		this.projectKey = projectKey;
		this.service = service;
		this.queryString = queryString;
		this.name = name;
		this.timeout = timeout;
		this.isActivated = true;
		this.limit = limit;
		this.icon = "aui-iconfont-download";
		this.constraint = constraint;
	}

    @Override
	public InputMethod getInputMethod() {
		this.inputMethod = SourceInputFactoryUtils.getInputMethod(recommenderType);
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
		return this.limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public String getConstraint() {
		return constraint;
	}

	public void setConstraints(String constraint) {
		this.constraint = constraint;
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

	@Override
	public String getIcon() {
		return "aui-iconfont-download";
	}
}
