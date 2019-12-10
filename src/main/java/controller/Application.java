package controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import nl.uu.cs.ape.sat.APE;
import nl.uu.cs.ape.sat.core.implSAT.SATsolutionsList;
import nl.uu.cs.ape.sat.utils.APEUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@SpringBootApplication
public class Application {
	/** APE instance */
	public static APE apeInstance;
	/** Http-Client */
	public final static OkHttpClient client = new OkHttpClient();
	/** List of two String pairs data type label and data type id */
	public static List<Map<String, String>> allDataTypes = new ArrayList<Map<String, String>>();
	/** List of two String pairs format type label and format type id */
	public static List<Map<String, String>> allFormatTypes = new ArrayList<Map<String, String>>();
<<<<<<< HEAD
=======
	/** List of two String pairs tool label and tool id */
	public static List<Map<String, String>> allTools = new ArrayList<Map<String, String>>();
	/** Result object */
	public static SATsolutionsList results;
>>>>>>> branch 'master' of git@git.science.uu.nl:v.kasalica/biotoolcomposedemo.git
	
	public static void main(String[] args) throws Exception {
    	/** Fetch tool annotations */
    	JSONObject toolAnnotations = fetchTools("apeInputs/toolList.json");
    	/** Fetch ontology */
    	fetchAndWriteOntology("http://edamontology.org/EDAM.owl", "apeInputs/ontology.owl");
    	/** Write tool annotations in the inputs folder */
		writeToolAnnotationsFile(toolAnnotations, "apeInputs/toolAnnotations.json");	
		
		try {
			/** Create APE instance */
			apeInstance = new APE("./apeInputs/apeConfigHardcoded.json");
			allDataTypes = apeInstance.getTaxonomyElements("Data");
	    	allFormatTypes = apeInstance.getTaxonomyElements("Format");
	    	allTools = apeInstance.getTaxonomyElements("Operation");
		} catch (JSONException e) {
			System.err.println("Error in parsing the configuration file.");
		} 
		
        SpringApplication.run(Application.class, args);
        
    }
    
    public static void fetchAndWriteOntology(String urlToOntology, String pathToOnto) throws IOException {
    	try {
    		/** URL to the ontology **/
			URL ONTOLOGY_URL = new URL(urlToOntology);
			/** Ontology gets locally saved in the apeInputs folder **/
			File ontologyFile = new File(pathToOnto); //temp file
			FileUtils.copyURLToFile(ONTOLOGY_URL, ontologyFile); // Get ontology and copy to the temp file
		} catch (MalformedURLException e) {
			System.err.println("Ontology not provided correctly");
		}
    	
    }
    
    /**
     * Writes file with toolAnnotations
     * @throws IOException
     */
    public static void writeToolAnnotationsFile(JSONObject toolAnnotations, String pathToTools) throws IOException {
    	File toolAnnotationsFile = new File(pathToTools);
    	BufferedWriter writer = new BufferedWriter(new FileWriter(toolAnnotationsFile));
        writer.write(toolAnnotations.toString());
        writer.close();
    }
    
    /**
     * Send Get request to get tool annotations
     * Saves JSONArray with all the tool annotations (in tool list)
     * @return 
     */
    public static JSONObject fetchTools(String pathToTools) throws Exception {
    	JSONArray bioToolAnnotations = new JSONArray();
    	File toolList = new File(pathToTools);
		JSONArray toolListJson = new JSONArray(FileUtils.readFileToString(toolList, "UTF-8"));
		for(int i = 0; i<toolListJson.length(); i++) {
			String currTool = toolListJson.getString(i);
	        Request request = new Request.Builder()
	                .url("https://bio.tools/api/" + currTool + "?format=json")
	                .build();
	        try (Response response = client.newCall(request).execute()) {
	            if (!response.isSuccessful()) throw new IOException("Unexpected code when trying to fetch" + response);
	            // Get response body
	            JSONObject responseJson = new JSONObject(response.body().string());
	            bioToolAnnotations.put(i, responseJson);
	        }
		}
		return  APEUtils.convertBioTools2Ape(bioToolAnnotations);
    }
    
}
  