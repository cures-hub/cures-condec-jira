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

    public MatrixEntry(Long idOfSourceElement, Long idOfDestinationElement, String linkTypeName) {
        this.idOfSourceElement = idOfSourceElement;
        this.idOfDestinationElement = idOfDestinationElement;
        this.linkType = linkTypeName;
        this.color = LinkType.getLinkTypeColor(linkTypeName);
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
}
