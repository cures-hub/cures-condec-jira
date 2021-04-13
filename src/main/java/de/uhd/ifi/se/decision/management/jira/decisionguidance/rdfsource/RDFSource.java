package de.uhd.ifi.se.decision.management.jira.decisionguidance.rdfsource;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.google.common.base.Splitter;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.KnowledgeSource;

/**
 * Knowledge source identified using the Resource Description Framework (RDF)
 * format. Is used to query DBPedia.
 */
public class RDFSource extends KnowledgeSource {

	protected String service;
	protected String query;
	protected int timeout;
	protected String constraints;

	public static final String PREFIX = "PREFIX dbo: <http://dbpedia.org/ontology/>"
			+ "PREFIX dct: <http://purl.org/dc/terms/>" + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
			+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "PREFIX foaf: <http://xmlns.com/foaf/0.1/>";
	public static final String DEFAULT_QUERY = PREFIX + " select distinct ?subject ?url count(?link) where { "
			+ "%variable% dbo:genre ?genre. ?subject dbo:genre ?genre. ?subject foaf:isPrimaryTopicOf ?url. "
			+ "?subject dbo:wikiPageExternalLink ?link.} GROUP BY ?subject ?url ";
	public static final String DEFAULT_SERVICE = "http://dbpedia.org/sparql";
	public static final String DEFAULT_NAME = "DBPedia";

	/**
	 * Creates a default knowledge source for DBPedia.
	 */
	public RDFSource() {
		this.service = DEFAULT_SERVICE;
		this.query = DEFAULT_QUERY;
		this.name = DEFAULT_NAME;
		this.timeout = 30000;
		this.isActivated = true;
		this.icon = "aui-iconfont-download";
		this.constraints = "";
	}

	/**
	 * @param projectKey
	 *            of the Jira project.
	 * @param service,
	 *            Uniform Resource Identifier (URI) to RDF graph, e.g.,
	 *            http://dbpedia.org/sparql
	 * @param query
	 *            in SPARQL language (SPARQL Protocol And RDF Query Language).
	 * @param name
	 *            of the knowledge source.
	 * @param timeout
	 *            in milliseconds.
	 */
	public RDFSource(String name, String service, String query, int timeout, String constraint) {
		this.service = service;
		this.query = query;
		this.name = name;
		this.timeout = timeout;
		this.isActivated = true;
		this.icon = "aui-iconfont-download";
		this.constraints = constraint;
	}

	/**
	 * @return Uniform Resource Identifier (URI) to RDF graph, e.g.
	 *         http://dbpedia.org/sparql.
	 */
	public String getService() {
		return service;
	}

	/**
	 * @param service
	 *            Uniform Resource Identifier (URI) to RDF graph, e.g.
	 *            http://dbpedia.org/sparql.
	 */
	public void setService(String service) {
		this.service = service;
	}

	/**
	 * @return query in SPARQL language (SPARQL Protocol And RDF Query Language).
	 */
	public String getQuery() {
		return query;
	}

	/**
	 * @param query
	 *            in SPARQL language (SPARQL Protocol And RDF Query Language).
	 */
	public void setQuery(String query) {
		this.query = query;
	}

	/**
	 * @return timeout in milliseconds.
	 */
	public int getTimeout() {
		return timeout;
	}

	/**
	 * @param timeout
	 *            in milliseconds.
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
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