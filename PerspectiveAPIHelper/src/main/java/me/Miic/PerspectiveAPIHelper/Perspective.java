package me.Miic.PerspectiveAPIHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**

 An assisting class for those using PerspectiveAPI in Java.
 
 Static class that returns the result from Google / Jigsaw's PerspectiveAPI.
 
 Simply set the key recieved from the research team and make call requests from the Static class
 
 */

public class Perspective 
{
	
	private static String apiKey;
	
	public static void setKey(String apiKey) {
		Perspective.apiKey = apiKey;
	}
	
    public static float getToxicity(String query) {
    	try {
	       	HttpURLConnection httpcon = (HttpURLConnection) ((new URL("https://commentanalyzer.googleapis.com/v1alpha1/comments:analyze?key=" + apiKey).openConnection()));
	    	httpcon.setDoOutput(true);
	    	httpcon.setRequestProperty("Content-Type", "application/json");
	    	httpcon.setRequestProperty("Accept", "application/json");
	    	httpcon.setRequestMethod("POST");
	    	httpcon.connect();
	    	
	    	JsonParser parser = new JsonParser();
	    	JsonObject jObj = parser.parse(
	    			"{comment: {text: \"" + query + "\"},"
	    			+ "languages: [\"en\"], "
	    			+ "requestedAttributes: {TOXICITY:{}} }").getAsJsonObject();
	
	    	//Send Request
	    	OutputStream os = httpcon.getOutputStream();
	    	PrintWriter pw = new PrintWriter(new OutputStreamWriter(os));
	    	pw.write(jObj.toString());
	    	pw.close();
	    	os.close();
	    	
	    	//Read response
	    	InputStream is = httpcon.getInputStream();
	    	BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	    	String line = null;
	    	StringBuffer sb = new StringBuffer();
	    	while ((line = reader.readLine()) != null) {
	    	    sb.append(line);
	    	}
	    	is.close();
	    	
	    	//Get specified data needed.
	    	JsonObject jResponse = parser.parse(sb.toString()).getAsJsonObject();
	    	return jResponse.get("attributeScores").getAsJsonObject().get("TOXICITY").getAsJsonObject().get("summaryScore").getAsJsonObject().get("value").getAsFloat();
    	} catch (IOException e) {
    		e.printStackTrace();
    		return 0;
    	}
    }
}
