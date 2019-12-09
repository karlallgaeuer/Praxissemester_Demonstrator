package biotoolcomposedemo;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class TestClassUtils {
	public static BufferedImage readImageFromFile(String path) throws IOException {
		BufferedImage image = null;
		File file = new File(path);
		image = ImageIO.read(file);
		return image;
	}
}
