package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

/**
 * Represents the intra-rationale completeness between two
 * {@link KnowledgeType}s. For example, a question to be answered with the
 * metric is "How many decisions have pro-arguments documented and how many
 * decisions do not have pro-arguments documented?"
 */
public class RationaleCoverageMetric {

	private KnowledgeType targetElementType;
	private int minimumRequiredCoverage;
	private Map<Integer, List<KnowledgeElement>> coverageMap = new LinkedHashMap<>();

	public RationaleCoverageMetric(KnowledgeType targetElementType) {
		this.targetElementType = targetElementType;
		this.coverageMap = new LinkedHashMap<>();
	}

	@XmlElement
	public String getTargetElementType() {
		return targetElementType.toString();
	}

	@XmlElement
	public int getMinimumRequiredCoverage() {
		return minimumRequiredCoverage;
	}

	public void setMinimumRequiredCoverage(int minimumRequiredCoverage) {
		this.minimumRequiredCoverage = minimumRequiredCoverage;
	}

	@XmlElement
	public Map<Integer, List<KnowledgeElement>> getCoverageMap() {
		return coverageMap;
	}
}