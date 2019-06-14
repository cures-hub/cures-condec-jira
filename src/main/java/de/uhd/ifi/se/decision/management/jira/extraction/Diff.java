package de.uhd.ifi.se.decision.management.jira.extraction;

import com.atlassian.gzipfilter.org.apache.commons.lang.StringEscapeUtils;
import com.atlassian.jira.util.json.JSONObject;
import de.uhd.ifi.se.decision.management.jira.extraction.impl.ChangedFileImpl;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public interface Diff {
    ArrayList<ChangedFileImpl> getChangedFileImpls();
    void addChangedFileImpl(ChangedFileImpl changedFileImpl);

    static void sendPost(String projectName, String issueKey, String data) throws Exception {
        String USER_AGENT = "Mozilla/5.0";
        String url = "https://ijezxzhgjf.execute-api.eu-west-3.amazonaws.com/prod/diff/";
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        //add reuqest header
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        con.setRequestProperty("Content-Type", "application/json");
        String results ="";
        try {
            results = StringEscapeUtils.escapeJava(data);
        }catch (Exception e){
            e.printStackTrace();
        }

        JSONObject json = new JSONObject();
        json.put("projectKey", projectName );
        json.put("issueKey", issueKey );
        json.put("data", results);
        String urlParameters = json.toString();

        con.setDoOutput(true);
        con.setDoInput( true );
        con.setUseCaches( false );
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
