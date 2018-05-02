package de.uhd.ifi.se.decision.documentation.jira.model;

import com.atlassian.jira.issue.link.IssueLink;
import de.uhd.ifi.se.decision.documentation.jira.mocks.MockIssueLink;
import de.uhd.ifi.se.decision.documentation.jira.persistence.LinkEntity;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class TestLinkImpl {

    private LinkImpl linkImpl;

    @Before
    public void setUp(){
        linkImpl = new LinkImpl();
        linkImpl.setLinkType("Test");
        linkImpl.setIngoingId((long) 14);
        linkImpl.setOutgoingId((long) 15);
    }

    @Test
    public void testConstructureEntity(){
        LinkEntity link;
        link = mock(LinkEntity.class);
        LinkImpl linkImp = new LinkImpl(link);
        assertNotNull(linkImp);
    }

    @Test
    public void testConstructorIssueLink(){
        IssueLink link = new MockIssueLink((long)54);
        LinkImpl linkImp = new LinkImpl(link);
        assertNotNull(linkImp);
    }

    @Test
    public void testGetLinkType(){
        assertEquals("Test", linkImpl.getLinkType());
    }

    @Test
    public void testGetOutGoingId(){
        assertEquals((long) 15, linkImpl.getOutgoingId());
    }

    @Test
    public void testGetIngoingId(){
        assertEquals((long)14, linkImpl.getIngoingId());
    }
}
