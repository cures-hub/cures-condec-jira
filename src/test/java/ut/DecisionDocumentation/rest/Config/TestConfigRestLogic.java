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
}
