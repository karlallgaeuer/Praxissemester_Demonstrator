package model;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONObject;

public class Utils {

	public static String getByteArrayFromImg(BufferedImage controlFlow) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(controlFlow, "png", baos);
		baos.flush();
		byte[] controlFlowBit = baos.toByteArray();
		baos.close();
		String encodedImg = new String(Base64.encodeBase64(controlFlowBit), "UTF-8");
		return encodedImg;
	}
	
	public static JSONObject writeApeConfig(String userInput, JSONObject apeConfig) {
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
		return apeConfig;
	}

}
