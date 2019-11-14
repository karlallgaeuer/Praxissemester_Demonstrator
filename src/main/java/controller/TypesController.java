package controller;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import nl.uu.cs.ape.sat.APE;

@RestController
public class TypesController {
	
	private File apeConfigFileHardcoded;
	private JSONObject apeConfig;
	
	public TypesController() throws IOException{
		apeConfigFileHardcoded = new File("apeInputs/apeConfigHardcoded.txt");
		apeConfig = new JSONObject(FileUtils.readFileToString(apeConfigFileHardcoded, "UTF-8"));	// Read the hardcoded config file and use it to make a json objetct
	}
    /**
     * @return map with format/data types (spring transforms it to json)
     */
    @RequestMapping("/getTypes")	// GET-Rest request
    public Map<String, List<Map<String, String>>> getTypes() {
    	if (Application.ontology != null) {
    		// Create map, which saves both data types and formats
    		Map<String, List<Map<String, String>>> map = new HashMap<>();
    	    map.put("dataTypes", Application.ontology.getDataTypes());
    	    map.put("formatTypes", Application.ontology.getFormatTypes());
    	    return map;
    	}else {
    		System.err.println("Ontology doesn't exist.");
    		return null;
    	}
    }
    
    /**
     * @param userInput
     * @return APE result
     * @throws Exception 
     */
    @RequestMapping(value="/run")
    public boolean run(@RequestBody String userInput) {
    	try {
    	JSONObject userInputJson = new JSONObject(userInput);	// Config settings selected by the user as a json
    	String minLength = String.valueOf(userInputJson.get("solution_min_length"));
    	String maxLength = String.valueOf(userInputJson.get("solution_max_length"));
    	String maxSolutions = String.valueOf(userInputJson.get("max_solutions"));
    	JSONArray inputs = userInputJson.getJSONArray("inputs");
    	JSONArray outputs = userInputJson.getJSONArray("outputs");
    	apeConfig.put("solution_min_length", minLength);
    	apeConfig.put("solution_max_length", maxLength);
    	apeConfig.put("max_solutions", maxSolutions);
    	apeConfig.put("number_of_generated_graphs", maxSolutions);
    	apeConfig.put("inputs", inputs);
    	apeConfig.put("outputs", outputs);
    	}catch (JSONException e){
    		System.err.println("Json configuration set up wrong");
    	}
    	// Send tool annotation array (Application.toolAnnotations) to APE, get results (test method here)
    	//Application.toolAnnotations;
    	//boolean runSucc = runApe(apeConfig);
    	
    	
		return false;
    }
    
	private boolean runApe(JSONObject apeConfig) {
		
		JSONObject confiObject = null;
		APE apeFramework = null;
		try {
			apeFramework = new APE(confiObject);
		} catch (JSONException e) {
			System.err.println("Error in parsing the configuration file.");
			return false;
		} 
		
		try {
			apeFramework.runSynthesis();
		} catch (IOException e) {
			System.err.println("Error in synthesis execution. Writing to the file system failed.");
			return false;
		}
		
		return false;
		
	}
}