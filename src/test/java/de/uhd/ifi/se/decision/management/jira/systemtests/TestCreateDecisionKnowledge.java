package de.uhd.ifi.se.decision.management.jira.systemtests;

import org.junit.*;

import static org.junit.Assert.assertEquals;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.ProfilesIni;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class TestCreateDecisionKnowledge {
	static String baseUrl;
	static FirefoxOptions options;
	static long DEFAULT_TIME_OUT = 10;
	/**
	 * Preconditions:
	 * To set up Selenium, you will need to have firefox installed and create a profile named SeleniumTester.
	 * To make this test work, you will need to use that profile to log into your locally running Jira instance.
	 * Make sure you select "Remember me". This will be saved in the profile, so that the tests can run without
	 * another login.
	 * <p>
	 * You will also need to verify that the ConDec plugin is enabled and the ConDec issue types are available
	 */

	@BeforeClass
	public static void setUpClass() {
		// Since these tests are designed to be run locally
		// we explicitly disable them so they aren't run on the CI
		// To enable these tests, set the environment variable RUN_SYS_TESTS to true
		Assume.assumeTrue("The variable RUN_SYS_TESTS is not set!", System.getenv("RUN_SYS_TESTS") != null);
		setUpGeckoDriverExecutable();

		// TODO: maybe the base URL should be read from a config file
		baseUrl = "http://localhost:2990/jira";

		/*
		 * @issue how to make selenium automatically log in to local Jira instance?
		 * @alternative use a firefox profile to save login data
		 * @con it takes a long time to load the firefox profile, and it must be done for every new WebDriver
		 *
		 * @alternative send an HTTP request and get an authentication cookie
		 * @con just using a cookie is not enough to log in to Jira, as selenium does not allow to set headers
		 *
		 * @decision use firefox profiles to save login data
		 *
		 * @issue how can we ensure that our Firefox profiles don't grow too large?
		 * @alternative set Firefox up to delete history every time it is closed
		 * @pro this is easy to set up
		 * @con this can not be automated
		 * @decision Set up the SeleniumTester Firefox profile to delete history every time it is closed!
		 *
		 */

		ProfilesIni profile = new ProfilesIni();
		FirefoxProfile testerProfile = profile.getProfile("SeleniumTester");
		options = new FirefoxOptions();
		options.setProfile(testerProfile);

	}


	@Test
	public void TestCreateDecisionKnowledgeAsJiraIssue() {
		WebDriver driver = new FirefoxDriver(options);

		try {

			driver.get(baseUrl + "/secure/CreateIssue!default.jspa");
			new WebDriverWait(driver, DEFAULT_TIME_OUT).until(ExpectedConditions.presenceOfElementLocated(By.id("jira")));
			// TODO: this will only work if Issue is already selected. It should be replaced with a less volatile method
			// like an HTTP request to create a decision knowledge issue.
			driver.findElement(By.id("issue-create-submit")).click(); // this clicks the next button

			// wait until the page loads and the summary box is present
			new WebDriverWait(driver, DEFAULT_TIME_OUT).until(ExpectedConditions.presenceOfElementLocated(By.id("summary")));

			// Write the name of the decision knowledge element
			driver.findElement(By.id("summary")).sendKeys("Decision knowledge element 1");

			// click the create button
			driver.findElement(By.id("issue-create-submit")).click();

			// Wait until the issue page loads
			new WebDriverWait(driver, DEFAULT_TIME_OUT).until(ExpectedConditions.presenceOfElementLocated(By.id("type-val")));
			assertEquals(driver.findElement(By.id("type-val")).getText(), "Issue");
			assertEquals(driver.findElement(By.id("summary-val")).getText(), "Decision knowledge element 1");
		} finally {
			// always clean up!
			driver.quit();
		}
	}


	/**
	 * Set up the path to the GeckoDriver executable
	 * To use a path other than /opt/WebDriver/bin/geckodriver,
	 * set the environment variable WEBDRIVER_GECKO_DRIVER to your custom path
	 */
	private static void setUpGeckoDriverExecutable() {
		String geckoDriverPath = System.getenv("WEBDRIVER_GECKO_DRIVER");
		if (geckoDriverPath == null) {
			// this is the recommended installation path according to selenium docs
			geckoDriverPath = "/opt/WebDriver/bin/geckodriver";
		}
		System.setProperty("webdriver.gecko.driver", geckoDriverPath);
	}
}

