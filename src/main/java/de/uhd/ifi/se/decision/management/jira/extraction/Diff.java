package de.uhd.ifi.se.decision.management.jira.extraction;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import com.atlassian.gzipfilter.org.apache.commons.lang.StringEscapeUtils;
import com.atlassian.jira.util.json.JSONObject;

/**
 * Interface for a list of changed files. The scope for the diff might be a
 * single git commit, a whole feature branch (with many commits), or all commits
 * belonging to a JIRA issue.
 */
public interface Diff {

	ArrayList<ChangedFile> getChangedFiles();

	void addChangedFile(ChangedFile changedFile);

	/**
	 * Sends the evaluation data to an external server in order to evaluate the
	 * algorithm to detect tangled changes.
	 */
	static void sendPost(String projectName, String issueKey, String data) throws Exception {
		String USER_AGENT = "Mozilla/5.0";
		String url = "https://ijezxzhgjf.execute-api.eu-west-3.amazonaws.com/prod/diff/";
		URL obj = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

		// add reuqest header
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		con.setRequestProperty("Content-Type", "application/json");
		String results = "";
		try {
			results = StringEscapeUtils.escapeJava(data);
		} catch (Exception e) {
			e.printStackTrace();
		}

		JSONObject json = new JSONObject();
		json.put("projectKey", projectName);
		json.put("issueKey", issueKey);
		json.put("data", results);
		String urlParameters = json.toString();

		con.setDoOutput(true);
		con.setDoInput(true);
		con.setUseCaches(false);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + urlParameters);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		System.out.println(response.toString());
	}
}
