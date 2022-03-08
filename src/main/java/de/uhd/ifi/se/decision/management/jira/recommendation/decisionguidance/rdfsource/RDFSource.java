package de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.rdfsource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.common.base.Splitter;

import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.KnowledgeSource;

/**
 * Knowledge source identified using the Resource Description Framework (RDF)
 * format. Is used to query DBPedia.
 */
public class RDFSource extends KnowledgeSource {

	protected String service;
	protected String query;
	protected int timeout;
	protected String constraints;

	public static final String PREFIX = "PREFIX dbo: <http://dbpedia.org/ontology/>\n" //
			+ "PREFIX dct: <http://purl.org/dc/terms/>\n" //
			+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" //
			+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" //
			+ "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n\n";
	public static final String DEFAULT_QUERY = PREFIX + "select distinct ?subject ?url count(?link) where { \n" //
			+ "%variable% dbo:genre ?genre.\n" //
			+ "?subject dbo:genre ?genre.\n"//
			+ "?subject foaf:isPrimaryTopicOf ?url.\n" //
			+ "?subject dbo:wikiPageExternalLink ?link.} GROUP BY ?subject ?url";
	public static final String DEFAULT_NAME = "Frameworks";
	public static final String DEFAULT_SERVICE = "http://dbpedia.org/sparql";

	/**
	 * Creates a default knowledge source for DBPedia.
	 */
	public RDFSource() {
		super(DEFAULT_NAME, true);
		this.service = DEFAULT_SERVICE;
		this.query = DEFAULT_QUERY;
		this.timeout = 30000;
		this.constraints = "";
	}

	/**
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
		super(name, true);
		this.service = service;
		this.query = query;
		this.timeout = timeout;
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

	public static List<RDFSource> getDefaultDBPediaQueries() {
		List<RDFSource> defaultSources = new ArrayList<>();
		defaultSources.add(dbPediaFrameworks());
		return defaultSources;
	}

	public static RDFSource dbPediaFrameworks() {
		return new RDFSource();
	}
}