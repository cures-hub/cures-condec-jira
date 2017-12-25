package ut.DecisionDocumentation.rest.Config;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.DecisionDocumentation.rest.Config.ConfigRestLogic;
import javax.ws.rs.core.Response.Status;
/**
 * 
 * @author Tim Kuchenbuch
 * @description Testing the functionality of the ConfigRestLogic Class 
 *
 */
public class TestConfigRestLogic {
	
	private ConfigRestLogic restLogic; 
	
	@Before
	public void setUp() {
		restLogic= new ConfigRestLogic();
	}
	
	//Testing setResponseForGet
	
	@Test
	public void testSetResponseForGetProjectKeyNull() {
		String projectKey;
		projectKey=null;
		restLogic.setResponseForGet(projectKey);
		assertEquals(Status.CONFLICT.getStatusCode(),restLogic.getResponse().getStatus());
	}
	
	@Test
	public void testSetResponseForGetProjectKeyEmpty() {
		String projectKey;
		projectKey="";
		restLogic.setResponseForGet(projectKey);
		assertEquals(Status.CONFLICT.getStatusCode(),restLogic.getResponse().getStatus());
	}
	
	@Test
	public void testSetResponseForGetProjectKeyFilled() {
		String projectKey;
		projectKey="TEST";
		restLogic.setResponseForGet(projectKey);
		assertEquals(Status.OK.getStatusCode(),restLogic.getResponse().getStatus());
	}
	
	// Testing isActivated
	@Test
	public void testSetIsActivatedProjectKeyNullIsActivatedNull() {
		String projectKey=null;
		String isActivated=null;
		restLogic.setIsActivated(projectKey, isActivated);
		assertEquals(Status.CONFLICT.getStatusCode(),restLogic.getResponse().getStatus());
	}
	
	@Test
	public void testSetIsActivatedProjectKeyEmptylIsActivatedNull() {
		String projectKey="";
		String isActivated=null;
		restLogic.setIsActivated(projectKey, isActivated);
		assertEquals(Status.CONFLICT.getStatusCode(),restLogic.getResponse().getStatus());		
	}
	
	@Test
	public void testSetIsActivatedProjectKeyFilledIsActivatedNull() {
		String projectKey="TEST";
		String isActivated=null;
		restLogic.setIsActivated(projectKey, isActivated);
		assertEquals(Status.CONFLICT.getStatusCode(),restLogic.getResponse().getStatus());
	}
	
	@Test
	public void testSetIsActivatedProjectKeyNullIsActivatedEmpty() {
		String projectKey=null;
		String isActivated="";
		restLogic.setIsActivated(projectKey, isActivated);
		assertEquals(Status.CONFLICT.getStatusCode(),restLogic.getResponse().getStatus());
	}
	
	@Test
	public void testSetIsActivatedProjectKeyEmptylIsActivatedEmpty() {
		String projectKey="";
		String isActivated="";
		restLogic.setIsActivated(projectKey, isActivated);
		assertEquals(Status.CONFLICT.getStatusCode(),restLogic.getResponse().getStatus());
	}
	
	@Test
	public void testSetIsActivatedProjectKeyFilledIsActivatedEmpty() {
		String projectKey="TEST";
		String isActivated="";
		restLogic.setIsActivated(projectKey, isActivated);
		assertEquals(Status.CONFLICT.getStatusCode(),restLogic.getResponse().getStatus());
	}
	
	@Test
	public void testSetIsActivatedProjectKeyNullIsActivatedFilled() {
		String projectKey=null;
		String isActivated="true";
		restLogic.setIsActivated(projectKey, isActivated);
		assertEquals(Status.CONFLICT.getStatusCode(),restLogic.getResponse().getStatus());
	}
	
	@Test
	public void testSetIsActivatedProjectKeyEmptylIsActivatedFilled() {
		String projectKey="";
		String isActivated="true";
		restLogic.setIsActivated(projectKey, isActivated);
		assertEquals(Status.CONFLICT.getStatusCode(),restLogic.getResponse().getStatus());
	}
	
	@Test
	public void testSetIsActivatedProjectKeyFilledIsActivatedFilled() {
		String projectKey="TEST";
		String isActivated="true";
		restLogic.setIsActivated(projectKey, isActivated);
		assertEquals(Status.CONFLICT.getStatusCode(),restLogic.getResponse().getStatus());
	}
	
	// Testing setIssueStrategy
		@Test
		public void testSetIsActivatedProjectKeyNullisIssueStrategyNull() {
			String projectKey=null;
			String isIssueStrategy=null;
			restLogic.setIsIssueStrategy(projectKey, isIssueStrategy);
			assertEquals(Status.CONFLICT.getStatusCode(),restLogic.getResponse().getStatus());
		}
		
		@Test
		public void testSetIsActivatedProjectKeyEmptylisIssueStrategyNull() {
			String projectKey="";
			String isIssueStrategy=null;
			restLogic.setIsIssueStrategy(projectKey, isIssueStrategy);
			assertEquals(Status.CONFLICT.getStatusCode(),restLogic.getResponse().getStatus());		
		}
		
		@Test
		public void testSetIsActivatedProjectKeyFilledisIssueStrategyNull() {
			String projectKey="TEST";
			String isIssueStrategy=null;
			restLogic.setIsIssueStrategy(projectKey, isIssueStrategy);
			assertEquals(Status.CONFLICT.getStatusCode(),restLogic.getResponse().getStatus());
		}
		
		@Test
		public void testSetIsActivatedProjectKeyNullisIssueStrategyEmpty() {
			String projectKey=null;
			String isIssueStrategy="";
			restLogic.setIsIssueStrategy(projectKey, isIssueStrategy);
			assertEquals(Status.CONFLICT.getStatusCode(),restLogic.getResponse().getStatus());
		}
		
		@Test
		public void testSetIsActivatedProjectKeyEmptylisIssueStrategyEmpty() {
			String projectKey="";
			String isIssueStrategy="";
			restLogic.setIsIssueStrategy(projectKey,isIssueStrategy);
			assertEquals(Status.CONFLICT.getStatusCode(),restLogic.getResponse().getStatus());
		}
		
		@Test
		public void testSetIsActivatedProjectKeyFilledisIssueStrategyEmpty() {
			String projectKey="TEST";
			String isIssueStrategy="";
			restLogic.setIsIssueStrategy(projectKey,isIssueStrategy);
			assertEquals(Status.CONFLICT.getStatusCode(),restLogic.getResponse().getStatus());
		}
		
		@Test
		public void testSetIsActivatedProjectKeyNullisIssueStrategyFilled() {
			String projectKey=null;
			String isIssueStrategy="true";
			restLogic.setIsIssueStrategy(projectKey, isIssueStrategy);
			assertEquals(Status.CONFLICT.getStatusCode(),restLogic.getResponse().getStatus());
		}
		
		@Test
		public void testSetIsActivatedProjectKeyEmptylisIssueStrategyFilled() {
			String projectKey="";
			String isIssueStrategy="true";
			restLogic.setIsIssueStrategy(projectKey, isIssueStrategy);
			assertEquals(Status.CONFLICT.getStatusCode(),restLogic.getResponse().getStatus());
		}
		
		@Test
		public void testSetIsActivatedProjectKeyFilledisIssueStrategyFilled() {
			String projectKey="TEST";
			String isIssueStrategy="true";
			restLogic.setIsIssueStrategy(projectKey, isIssueStrategy);
			assertEquals(Status.CONFLICT.getStatusCode(),restLogic.getResponse().getStatus());
		}
	
	
}
