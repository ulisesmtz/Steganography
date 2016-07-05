import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import javax.imageio.ImageIO;

/**
 * @author UlisesM
 *
 */
public class Steganography {
	
	/**
	 * Hides the text in an image by grabbing the LSB in image byte and 
	 * giving it the value of the corresponding bit in text.
	 * @param bimg the image where the text will be hidden in
	 * @param text the message to hide
	 * @param isStoreLength true if hiding length of text, false otherwise
	 */
	private static void hideText(byte[] bimg, byte[] text, boolean isStoreLength) {
		int index = 0; // where to start writing in image
		
		if (!isStoreLength) // start at bit 32
			index = 32;
		
		for (int i = 0; i < text.length; i++) {
			int character = text[i];
			for (int j = 7; j >= 0; j--) {  // 8 bits per character
				// get corresponding bit
				int bit = (character >> j) & 0x1;
				
				// new bit is (0 OR bit) since (O OR anything) = anything
				bimg[index] = (byte) (0x0 | bit);
				index++;
			}
		}
	}
	
	/**
	 * Gets the bytes of an image in form of array
	 * @param bimg the image to get byte array from
	 * @return byte array of bufferedimage
	 */
	private static byte[] getImageBytes(BufferedImage bimg) {
		WritableRaster raster = bimg.getRaster();
		DataBufferByte dbb = (DataBufferByte) raster.getDataBuffer();
		return dbb.getData();
	}
	
	/**
	 * Encodes a message into an image using the least significant bit (LSB) algorithm
	 * @param bimg the image used to hide text in
	 * @param message the text to be hidden
	 * @return new image with the text embedded in it
	 */
	private static BufferedImage encode(BufferedImage bimg, String message) {
		BufferedImage newImage = null;
		byte[] messageArray = message.getBytes();
		
		// convert message length to byte array (4 bytes)
		byte[] messageLengthArray = new byte[4]; // will hold length of message in bytes
		messageLengthArray[0] = (byte)((message.length() & 0xFF000000) >> 24);
		messageLengthArray[1] = (byte)((message.length() & 0x00FF0000) >> 16);
		messageLengthArray[2] = (byte)((message.length() & 0x0000FF00) >> 8);
		messageLengthArray[3] = (byte) (message.length() & 0x000000FF);

		
		// copy original image to new image using graphics
		newImage = new BufferedImage(bimg.getWidth(), bimg.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
		Graphics g = bimg.createGraphics();
		g.drawImage(newImage, 0, 0, null);
		g.dispose();		
		
		byte[] imageArray = getImageBytes(newImage);
			
		// hide text in image
		hideText(imageArray, messageLengthArray, true);
		hideText(imageArray, messageArray, false);
				
		return newImage;
		
	}
	
	/**
	 * Gets the hidden text from an image using LSB algorithm
	 * @param bimg the image with the hidden text inside
	 * @return the hidden text
	 */
	private static String decode(BufferedImage bimg) {
		byte[] imageArray = getImageBytes(bimg);
		
		int length = 0;
		
		// get length stored in first 4 bytes of image
		for (int i = 0; i < 32; ++i) {
			length = (length << 1) | (imageArray[i] & 1);
		}
		
		byte[] result = new byte[length];
		int index = 32; // skip first 4 bytes for length of message

		// loop til length of message and grab each LSB from image
		for (int i = 0; i < result.length; i++) {
			for (int j = 0; j < 8; j++) { // each bit
				// shift by 1 and grab last bit of next image byte
				result[i] = (byte) ((result[i] << 1) | (imageArray[index] & 1));
				index++;
			}
		}
		
		// convert to string
		String message = "";
		for (byte b : result) {
			message += (char) b;
		}
		
		return message;
		
	}
	
	
	public static void main(String[] args) {
		
		BufferedImage bimg = null;  // original image to hide text in
		BufferedImage newImage = null;  // output of image with text embedded
		Scanner scanner = null;     // retrieve text using scanner
		
		try {
			scanner = new Scanner(new File("C://Users//UlisesM//Desktop//text.txt"));
			bimg = ImageIO.read(new File("C://Users//UlisesM//Desktop//flyer+gator6.png"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
				
		String message = "";
		
		// store contents of text in variable message
		while (scanner.hasNextLine()) {
			message += scanner.nextLine() + "\n";
		}
		
		newImage = encode(bimg, message);
	
		String msg = decode(newImage);
		
		System.out.println(msg); // test
		
	}

}
