package de.uhd.ifi.se.decision.management.jira.decisionguidance.rdfsource;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.google.common.base.Splitter;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.KnowledgeSource;

/**
 * Knowledge source identified using the Resource Description Framework (RDF)
 * format. Is used to model DBPedia.
 */
public class RDFSource extends KnowledgeSource {

	protected String service;
	protected String queryString;
	protected int timeout;
	protected int limit;
	protected String constraints;

	private static final String PREFIX = "PREFIX dbo: <http://dbpedia.org/ontology/>"
			+ "PREFIX dct: <http://purl.org/dc/terms/>" + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
			+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "PREFIX foaf: <http://xmlns.com/foaf/0.1/>";

	/**
	 * Creates a default knowledge source for DBPedia.
	 */
	public RDFSource() {
		this.service = "http://dbpedia.org/sparql";
		this.queryString = PREFIX + " select distinct ?subject ?url count(?link)   where { "
				+ "%variable% dbo:genre ?genre. " + "?subject dbo:genre ?genre. "
				+ "?subject foaf:isPrimaryTopicOf ?url. "
				+ "?subject dbo:wikiPageExternalLink ?link.} GROUP BY ?subject ?url ";
		this.name = "DBPedia";
		this.timeout = 30000;
		this.limit = 10;
		this.isActivated = true;
		this.icon = "aui-iconfont-download";
		this.constraints = "";
	}

	/**
	 * @param projectKey
	 *            of the Jira project.
	 * @param service,
	 *            e.g., http://dbpedia.org/sparql
	 * @param queryString
	 *            in SPARQL language (SPARQL Protocol And RDF Query Language).
	 * @param name
	 *            of the knowledge source.
	 * @param timeout
	 *            in milliseconds.
	 */
	public RDFSource(String name, String service, String queryString, int timeout, int limit, String constraint) {
		this.service = service;
		this.queryString = queryString;
		this.name = name;
		this.timeout = timeout;
		this.isActivated = true;
		this.limit = limit;
		this.icon = "aui-iconfont-download";
		this.constraints = constraint;
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

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public int getLimit() {
		return this.limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	/**
	 * @return e.g. License=dbo:license. Multiple values are separated with "&".
	 */
	public String getConstraints() {
		return constraints;
	}

	public Map<String, String> getConstraintMap() {
		try {
			return Splitter.on("&").withKeyValueSeparator("=").split(getConstraints());
		} catch (IllegalArgumentException e) {
			return new HashMap<>();
		}
	}

	/**
	 * @param constraints
	 *            e.g. License=dbo:license. Multiple values are separated with "&".
	 */
	public void setConstraints(String constraints) {
		this.constraints = constraints;
	}

	@Override
	public String toString() {
		return name.replace(" ", "-");
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (object == null || getClass() != object.getClass()) {
			return false;
		}
		RDFSource rdfSource = (RDFSource) object;
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