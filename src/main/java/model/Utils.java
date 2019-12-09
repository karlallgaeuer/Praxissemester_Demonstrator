package model;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;

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

}
