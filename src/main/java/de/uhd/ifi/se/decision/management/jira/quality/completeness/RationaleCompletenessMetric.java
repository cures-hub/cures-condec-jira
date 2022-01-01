package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

/**
 * Represents the intra-rationale completeness between two
 * {@link KnowledgeType}s. For example, a question to be answered with the
 * metric is "How many decisions have pro-arguments documented and how many
 * decisions do not have pro-arguments documented?"
 */
public class RationaleCompletenessMetric {

	private KnowledgeType sourceElementType;
	private KnowledgeType targetElementType;
	private Set<KnowledgeElement> completeElements;
	private Set<KnowledgeElement> incompleteElements;

	public RationaleCompletenessMetric(KnowledgeType sourceElementType, KnowledgeType targetElementType) {
		this.sourceElementType = sourceElementType;
		this.targetElementType = targetElementType;
		completeElements = new LinkedHashSet<>();
		incompleteElements = new LinkedHashSet<>();
	}

	public boolean addCompleteElement(KnowledgeElement element) {
		return completeElements.add(element);
	}

	public boolean addIncompleteElement(KnowledgeElement element) {
		return incompleteElements.add(element);
	}

	@XmlElement
	public String getSourceElementType() {
		return sourceElementType.toString();
	}

	@XmlElement
	public String getTargetElementType() {
		return targetElementType.toString();
	}

	@XmlElement
	public Set<KnowledgeElement> getCompleteElements() {
		return completeElements;
	}

	@XmlElement
	public Set<KnowledgeElement> getIncompleteElements() {
		return incompleteElements;
	}
}