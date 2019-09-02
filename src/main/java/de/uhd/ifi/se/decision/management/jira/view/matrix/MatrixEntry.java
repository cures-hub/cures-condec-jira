package de.uhd.ifi.se.decision.management.jira.view.matrix;

import de.uhd.ifi.se.decision.management.jira.model.LinkType;

import javax.xml.bind.annotation.XmlElement;

public class MatrixEntry {
    @XmlElement
    private Long idOfSourceElement;

    @XmlElement
    private Long idOfDestinationElement;

    @XmlElement
    private String linkType;

    @XmlElement
    private String color;

    public MatrixEntry(Long idOfSourceElement, Long idOfDestinationElement, String linkType) {
        this.idOfSourceElement = idOfSourceElement;
        this.idOfDestinationElement = idOfDestinationElement;
        this.linkType = linkType;
        this.color = getColor(linkType);
    }

    public Long getIdOfSourceElement() {
        return idOfSourceElement;
    }

    public Long getIdOfDestinationElement() {
        return idOfDestinationElement;
    }

    public String getLinkType() {
        return linkType;
    }

    public String getColor() {
        return color;
    }

    private String getColor(String linkType) {
        switch (linkType) {
            case "Constraints": return LinkType.CONSTRAINT.getColor();
            case "Enables": return LinkType.ENABLE.getColor();
            case "Forbids": return LinkType.FORBID.getColor();
            case "Comprises": return LinkType.COMPRISE.getColor();
            case "Subsumes": return LinkType.SUBSUME.getColor();
            case "Overrides": return LinkType.OVERRIDE.getColor();
            case "Replaces": return LinkType.REPLACE.getColor();
            case "Relates": return LinkType.RELATE.getColor();
            default: return "";
        }

    }
}
