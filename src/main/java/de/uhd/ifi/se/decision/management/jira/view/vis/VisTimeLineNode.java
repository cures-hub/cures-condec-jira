package de.uhd.ifi.se.decision.management.jira.view.vis;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.xml.bind.annotation.XmlElement;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.quality.DefinitionOfDoneChecker;

public class VisTimeLineNode {

	@XmlElement
	private int id;

	@XmlElement
	private String content;

	@XmlElement
	private String start;

	@XmlElement
	private String end;

	@XmlElement
	private String className;

	@XmlElement
	private String title;

	@XmlElement
	private long group;

	@XmlElement
	private String documentationLocation;

	private static final DateFormat DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd");

	public VisTimeLineNode(KnowledgeElement element, boolean isPlacedAtCreationDate, boolean isPlacedAtUpdatingDate,
			FilterSettings filterSettings) {
		if (element == null) {
			return;
		}
		id = ((int) element.getId());
		content = createContentString(element);
		if (isPlacedAtCreationDate) {
			start = DATEFORMAT.format(element.getCreationDate());
		} else {
			start = DATEFORMAT.format(element.getLatestUpdatingDate());
		}
		if (isPlacedAtCreationDate && isPlacedAtUpdatingDate) {
			end = DATEFORMAT.format(element.getLatestUpdatingDate());
		}
		className = element.getTypeAsString().toLowerCase();
		className += " " + element.getStatusAsString();
		title = element.getDescription();
		documentationLocation = element.getDocumentationLocation().getIdentifier();

		if (filterSettings.areQualityProblemHighlighted()) {
			String problemExplanation = DefinitionOfDoneChecker.getQualityProblemExplanation(element, filterSettings);
			if (!problemExplanation.isEmpty()) {
				title = problemExplanation;
				className += " dodViolation";
			}
		}
	}

	public VisTimeLineNode(KnowledgeElement element, long group, boolean isPlacedAtCreationDate,
			boolean isPlacedAtUpdatingDate, FilterSettings filterSettings) {
		this(element, isPlacedAtCreationDate, isPlacedAtUpdatingDate, filterSettings);
		this.group = group;
	}

	private String createContentString(KnowledgeElement element) {
		String image = "<img src=\"" + KnowledgeType.getIconUrl(element) + "\"> ";
		return image + element.getSummary();
	}

	public int getId() {
		return id;
	}

	public String getContent() {
		return content;
	}

	public String getStart() {
		return start;
	}

	public String getEnd() {
		return end;
	}

	public String getClassName() {
		return className;
	}

	public long getGroup() {
		return group;
	}

	public String getTitle() {
		return title;
	}
}
