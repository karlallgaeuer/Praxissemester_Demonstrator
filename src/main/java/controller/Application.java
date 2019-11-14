package controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import model.Ontology;
import nl.uu.cs.ape.sat.utils.APEUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@SpringBootApplication
public class Application {

	public static Ontology ontology = null;
	public final static OkHttpClient client = new OkHttpClient();
	public static JSONObject toolAnnotations;
	
    public static void main(String[] args) throws Exception {
		ontology = new Ontology();
		fetchTools(client);
		writeToolAnnotationsFile();	// Change to use parameter for path and for string to write
        SpringApplication.run(Application.class, args);
        
    }
    
    /**
     * Writes file with toolAnnotations
     * @throws IOException
     */
    public static void writeToolAnnotationsFile() throws IOException {
    	File toolAnnotationsFile = new File("apeInputs/toolAnnotations.txt");
    	BufferedWriter writer = new BufferedWriter(new FileWriter(toolAnnotationsFile));
        writer.write(toolAnnotations.toString());
        writer.close();
    }
    
    /**
     * Send Get request to get tool annotations
     * Saves JSONArray with all the tool annotations (in tool list)
     */
    private static void fetchTools(OkHttpClient client) throws Exception {
    	JSONArray bioToolAnnotations = new JSONArray();
    	File toolList = new File("apeInputs/toolList.txt");
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
		toolAnnotations =  APEUtils.convertBioTools2Ape(bioToolAnnotations);
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