package biotoolcomposedemo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.imageio.ImageIO;

import org.junit.Test;

import controller.ApeController;
import model.Utils;
import controller.Application;

public class TestClass {

	@Test
	public void dummyMethod() {
		TestClass tester = new TestClass();
		
		/** Test of the "Utils" class */
		BufferedImage testImg = null;
		try {
			testImg = TestClassUtils.readImageFromFile("src/test/java/biotoolcomposedemo/resources/testImage.png");
		} catch (IOException e) {
			System.err.println("Reading of the test image didn't work.");
		}
		try {
			assertEquals("Hash Codes of the two Strings should match", -1009892453, Utils.getByteArrayFromImg(testImg).hashCode());
		} catch (IOException e) {
			System.err.println("Reading of the test image didn't work.");
			assertTrue(false);
		}	
		
		/**  */
	}

}
