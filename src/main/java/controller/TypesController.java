package controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import nl.uu.cs.ape.sat.APE;
import nl.uu.cs.ape.sat.core.solutionStructure.ModuleNode;
import nl.uu.cs.ape.sat.core.solutionStructure.SolutionWorkflow;

@RestController
public class TypesController {

	private File apeConfigFileHardcoded;
	private JSONObject apeConfig;

	public TypesController() throws IOException {
		apeConfigFileHardcoded = new File("apeInputs/apeConfigHardcoded.json");
		apeConfig = new JSONObject(FileUtils.readFileToString(apeConfigFileHardcoded, "UTF-8")); // Read the hardcoded
																									// config file and
																									// use it to make a
																									// json objetct
	}

	/**
	 * @return map with format/data types (spring transforms it to json)
	 */
	@RequestMapping("/getTypes") // GET-Rest request
	public Map<String, List<Map<String, String>>> getTypes() {
		if (Application.apeInstance != null) {
			// Create map, which saves both data types and formats
			Map<String, List<Map<String, String>>> map = new HashMap<>();
			map.put("dataTypes", Application.allDataTypes);
			map.put("formatTypes", Application.allFormatTypes);
			return map;
		} else {
			System.err.println("Ontology doesn't exist.");
			return null;
		}
	}

	/**
	 * @return APE results
	 */
	@RequestMapping("/getResults") // GET-Rest request
	public static String getResults() {
		System.out.println("dxgxdgdx");
		System.out.println(Application.results.toString());
		/**
		 * List that contains lists of the tool sequences (1 list per workflow solution)
		 **/
		List<List<String>> allWorkflowSolutions = new ArrayList<List<String>>();
		for (SolutionWorkflow currWorkflowSol : Application.results) {
			List<String> solutionTools = new ArrayList<String>();
			for (ModuleNode currTool : currWorkflowSol.getModuleNodes()) {
				solutionTools.add(currTool.getDotLabel());
			}
			allWorkflowSolutions.add(solutionTools);
		}
		System.out.println(allWorkflowSolutions.get(0).toString());
		JSONArray resultsJSON = new JSONArray();
		for (int i = 0; i < allWorkflowSolutions.size(); i++) {
			resultsJSON.put(i, allWorkflowSolutions.get(i));
		}
		System.out.println(resultsJSON.toString());
		return resultsJSON.toString();
	}

	/** TODO **/
	@RequestMapping("/getFullResults") // GET-Rest request
	public static String getSequences() throws JSONException, IOException {

		JSONArray resultSequencesJSON = new JSONArray(); // Read the result file and save the text in a JSONArray
		int i = 0;
		for (SolutionWorkflow currWorkflowSol : Application.results) {
			String rawSolution = currWorkflowSol.getnativeSATsolution().getRelevantSolution();
			resultSequencesJSON.put(i, rawSolution);
			i++;
		}
		return resultSequencesJSON.toString();
	}

	/** TODO **/
	@RequestMapping("/getPictures") // GET-Rest request
	public static String getPictures() {
		return "to implement";
	}

	/**
	 * @param userInput
	 * @return APE result
	 * @throws Exception
	 */
	@RequestMapping(value = "/run")
	public boolean run(@RequestBody String userInput) {
		try {
			JSONObject userInputJson = new JSONObject(userInput); // Config settings selected by the user as a json
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
		} catch (JSONException e) {
			System.err.println("Json configuration set up wrong");
		}
		/** TODO: delete this **/
		System.out.println(apeConfig.toString());
		boolean bool = Application.runApe(apeConfig);
		return bool;
	}
}