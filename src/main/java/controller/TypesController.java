package controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
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

import model.Utils;
import nl.uu.cs.ape.sat.constraints.ConstraintTemplate;
import nl.uu.cs.ape.sat.core.solutionStructure.ModuleNode;

@RestController
public class TypesController {
	/** Hardcoded APE-config as file */
	private File apeConfigFileHardcoded;
	/** APE-config, that is to be completed with user inputs */
	private JSONObject apeConfig;

	public TypesController() throws IOException {
		apeConfigFileHardcoded = new File("apeInputs/apeConfigHardcoded.json");
		/** Read the hardcoded config file and use it to make a Json-objetct **/
		apeConfig = new JSONObject(FileUtils.readFileToString(apeConfigFileHardcoded, "UTF-8")); 
	}

	/**
	 * @return map with format/data types (spring transforms it to Json)
	 */
	@RequestMapping("/getTypes")
	public Map<String, List<Map<String, String>>> getTypes() {
		if (Application.apeInstance != null) {
			/** Create map, which saves both data types and formats **/
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
	 * @return the APE results in a very basic text-style
	 */
	@RequestMapping("/getSimpleResults")
	public static String getSimpleResults() {
		System.out.println(Application.results.toString());
		/**
		 * List that contains lists of the tool sequences (1 list per workflow solution)
		 **/
		List<List<String>> allWorkflowSolutions = new ArrayList<List<String>>();
		Application.results.getStream().forEach(solution -> {
			List<String> solutionTools = new ArrayList<String>();
			for (ModuleNode currTool : solution.getModuleNodes()) {
				solutionTools.add(currTool.getUsedModule().getPredicateLabel());
			}
			allWorkflowSolutions.add(solutionTools);
		});
		
		JSONArray resultsJSON = new JSONArray();
		for (int i = 0; i < allWorkflowSolutions.size(); i++) {
			resultsJSON.put(i, allWorkflowSolutions.get(i));
		}
		return resultsJSON.toString();
	}

	/**
	 * @return the control flow graphs in a JSONArray as Base64-Strings
	 * @throws IOException
	 */
	@RequestMapping("/getControlFlowImg") // GET-Rest request
	public static String getControlFlowImg() throws IOException {
		@SuppressWarnings("unused")
		BufferedImage image = null;
		JSONArray jsonImage = new JSONArray();
		
		Application.results.getStream().forEach(solution -> {
			BufferedImage controlFlow = solution.getControlflowGraph().getPNGImage();
			
			String encodedImage = null;
			try {
				encodedImage = Utils.getByteArrayFromImg(controlFlow);
			} catch (IOException e) {
				e.printStackTrace();
			}
					
			jsonImage.put(solution.getIndex(), encodedImage);
			
		});
		
		return jsonImage.toString();
	}
	
	/**
	 * @return the data flow graphs in a JSONArray as Base64-String
	 * @throws IOException
	 */
	@RequestMapping("/getDataFlowImg")
	public static String getDataFlowImg() throws IOException {
		@SuppressWarnings("unused")
		BufferedImage image = null;
		JSONArray jsonImage = new JSONArray();
		
		Application.results.getStream().forEach(solution -> {
			BufferedImage controlFlow = solution.getControlflowGraph().getPNGImage();
			
			String encodedImage = null;
			try {
				encodedImage = Utils.getByteArrayFromImg(controlFlow);
			} catch (IOException e) {
				e.printStackTrace();
			}
					
			jsonImage.put(solution.getIndex(), encodedImage);
			
		});
		
		return jsonImage.toString();
	}

	@RequestMapping("/getConstraintDescriptions") // GET-Rest request
	public static List<Map<String, String>> getConstraintDescriptions() {
		@SuppressWarnings("unused")
		JSONObject constraints = null;
		
		//get the constraints in the same structure as this: (this is a dummy map):
		if (Application.apeInstance != null) {
			// Create map, which saves both data types and formats
			List<Map<String, String>> list = new ArrayList<Map<String, String>>();
			Collection<ConstraintTemplate> constrTemplates = Application.apeInstance.getConstraintTemplates();
			for(ConstraintTemplate currTempl : constrTemplates) {
				Map<String, String> map = new HashMap<>();
				map.put("value", currTempl.getConstraintID());
				map.put("label", currTempl.getDescription());
				list.add(map);
			}
			
			return list;
		} else {
			System.err.println("Ontology doesn't exist.");
			return null;
		}
	}


	/**
	 * @param APE config
	 * @return APE result
	 * @throws Exception
	 */
	@RequestMapping(value = "/run")
	public boolean run(@RequestBody String userInput) {
		try {
			JSONObject userInputJson = new JSONObject(userInput); // Config settings selected by the user as a JSON-file
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
		boolean bool = Application.runApe(apeConfig);
		return bool;
	}
}