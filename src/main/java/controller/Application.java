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

import model.Ontology;
import nl.uu.cs.ape.sat.APE;
import nl.uu.cs.ape.sat.utils.APEUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@SpringBootApplication
public class Application {
	public static APE apeInstance;
	public final static OkHttpClient client = new OkHttpClient();
	/** List of two String pairs data type label and data type id */
	public static List<Map<String, String>> allDataTypes = new ArrayList<Map<String, String>>();
	/** List of two String pairs format type label and format type id */
	public static List<Map<String, String>> allFormatTypes = new ArrayList<Map<String, String>>();
	
	
    public static void main(String[] args) throws Exception {
    	JSONObject toolAnnotations = fetchTools("apeInputs/toolList.json");
		/** TODO**/
    	fetchAndWriteOntology("http://edamontology.org/EDAM.owl", "apeInputs/ontology.owl");
		writeToolAnnotationsFile(toolAnnotations, "apeInputs/toolAnnotations.json");	// Change to use parameter for path and for string to write
		
		try {
			apeInstance = new APE("./apeInputs/apeConfigHardcoded.json");
			allDataTypes = apeInstance.getTypeElements("Data");
	    	allFormatTypes = apeInstance.getTypeElements("Format");
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
    private static JSONObject fetchTools(String pathToTools) throws Exception {
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
    
    public static boolean runApe(JSONObject apeConfig) {
    	boolean corrExe = true;
    	
    	try {
    		corrExe = apeInstance.runSynthesis(apeConfig);
		} catch (IOException e) {
			System.err.println("Error in synthesis execution. Writing to the file system failed.");
			return false;
		}
    	
    	return corrExe;
    }
    
    
    
    
    //------------------------------------------------------------------------------
    /**
     * TODO: 
     * @param bioToolsAnotation
     * @return
     */
	public static JSONObject convertBioTools2Ape(JSONArray bioToolsAnotation) {
			
			JSONArray apeToolsAnnotations = new JSONArray();
			
			for (int i = 0; i < bioToolsAnotation.length(); i++) {
				JSONObject apeJsonTool = new JSONObject();
				JSONObject bioJsonTool = bioToolsAnotation.getJSONObject(i);
				
				apeJsonTool.put("name", bioJsonTool.getString("name"));
				apeJsonTool.put("operation", bioJsonTool.getString("biotoolsID"));
				
				JSONArray apeTaxonomyTerms = new JSONArray();
				JSONObject function = bioJsonTool.getJSONArray("function").getJSONObject(0);
				
				
				JSONArray operations = function.getJSONArray("operation");
				for (int j = 0; j < operations.length(); j++) {
					JSONObject bioOperation = operations.getJSONObject(j);
					apeTaxonomyTerms.put(bioOperation.get("term"));
				}
				apeJsonTool.put("taxonomyTerms", apeTaxonomyTerms);
	//			reading inputs
				JSONArray apeInputs = new JSONArray();
				JSONArray bioInputs = function.getJSONArray("input");
	//			for each input
				for (int j = 0; j < bioInputs.length(); j++) {
					JSONObject bioInput = bioInputs.getJSONObject(j);
					JSONObject apeInput = new JSONObject();
					JSONArray apeInputTypes = new JSONArray();
					JSONArray apeInputFormats = new JSONArray();
	//				add all data types
					for(JSONObject bioType : getListFromJson(bioInput, "data", JSONObject.class)) {
						apeInputTypes.put(bioType.getString("term"));
					}
					apeInput.put("Data", apeInputTypes);
	//				add all data formats
					for(JSONObject bioType : getListFromJson(bioInput, "format", JSONObject.class)) {
						apeInputFormats.put(bioType.getString("term"));
					}
					apeInput.put("Format", apeInputFormats);
					
					apeInputs.put(apeInput);
				}
				apeJsonTool.put("inputs", apeInputs);
				
	//			reading inputs
				JSONArray apeOutputs = new JSONArray();
				JSONArray bioOutputs = function.getJSONArray("output");
	//			for each output
				for (int j = 0; j < bioOutputs.length(); j++) {
					JSONObject bioOutput = bioOutputs.getJSONObject(j);
					JSONObject apeOutput = new JSONObject();
					JSONArray apeOutputTypes = new JSONArray();
					JSONArray apeOutputFormats = new JSONArray();
	//				add all data types
					for(JSONObject bioType : getListFromJson(bioOutput, "data", JSONObject.class)) {
						apeOutputTypes.put(bioType.getString("term"));
					}
					apeOutput.put("Data", apeOutputTypes);
	//				add all data formats
					for(JSONObject bioType : getListFromJson(bioOutput, "format", JSONObject.class)) {
						apeOutputFormats.put(bioType.getString("term"));
					}
					apeOutput.put("Format", apeOutputFormats);
					
					apeOutputs.put(apeOutput);
				}
				apeJsonTool.put("outputs", apeOutputs);
				
				
				
				apeToolsAnnotations.put(apeJsonTool);
			}
			
			
			return new JSONObject().put("functions", apeToolsAnnotations);
		}

	/**
	 * The method return a list of {@code <T>} elements that correspond to a
	 * given key in the given json object. If the key corresponds to a
	 * {@link JSONArray} all the elements are put in a {@link List}, otherwise if
	 * the key corresponds to a {@code <T>} list will contain only that
	 * object.
	 * 
	 * @param jsonObject - Json object that is being explored
	 * @param key        - key label that corresponds to the elements
	 * @param clazz      - class to which the elements should belong to
	 * @return List of elements that corresponds to the key. If the key does not
	 *         exists returns empty list.
	 */
	public static <T> List<T> getListFromJson(JSONObject jsonObject, String key, Class<T> clazz) {
		List<T> jsonList = new ArrayList<T>();
		try {
			Object tmp = jsonObject.get(key);
			try {
				if (tmp instanceof JSONArray) {
					JSONArray elements = (JSONArray) tmp;
					for (int i = 0; i < elements.length(); i++) {
						T element = (T) elements.get(i);
						jsonList.add(element);
					}
				} else {
					T element = (T) tmp;
					jsonList.add(element);
				}
			} catch (JSONException e) {
				System.err.println("Json parsing error. Expected object '" + clazz.getSimpleName() + "' under the tag '"
						+ key + "'. The followig object does not match the provided format:\n" + jsonObject.toString());
				return jsonList;
			}
			return jsonList;
		} catch (JSONException e) {
			return jsonList;
		}

	}
}