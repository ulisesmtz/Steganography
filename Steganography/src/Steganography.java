import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import javax.imageio.ImageIO;

/**
 * @author UlisesM
 *
 */
public class Steganography {
	
	/**Converts an image to 3 bytes of blue, green, red each, respectively
	 * @param bimg the bufferedimage to be converted to
	 * @return new bufferedimage with color scheme desired
	 */
	private static BufferedImage convertToBGR(BufferedImage bimg) {
		BufferedImage newImage = new BufferedImage(bimg.getWidth(), bimg.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
		Graphics g = newImage.getGraphics();
		g.drawImage(bimg, 0, 0, null);
		g.dispose();
		return newImage;
	}
	
	/**
	 * Hides the bytes in an image by grabbing the LSB in image byte and 
	 * giving it the value of the corresponding bit.
	 * @param bimg the image where the text will be hidden in
	 * @param bytes the message/image to hide
	 * @param offset how much offset there is in bimg array
	 */
	private static void hideBytes(byte[] bimg, byte[] bytes, int offset) {
		for (int i = 0; i < bytes.length; i++) {
			int character = bytes[i];
			for (int j = 7; j >= 0; j--) {  // 8 bits per character
				// get corresponding bit
				int bit = (character >> j) & 1;
				
				// get last image byte, set last bit to 0 ( AND 0xFE) then OR with bit
				bimg[offset] = (byte) ((bimg[offset] & 0xFE) | bit);
				offset++;
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
	 * Encodes bytes into an image using the least significant bit (LSB) algorithm
	 * @param bimg the image used to hide text in
	 * @param message the text to be hidden
	 * @return new image with the text embedded in it
	 */
	private static BufferedImage encodeText(BufferedImage bimg, String message) {
		
		byte[] bytes = message.getBytes();
		
		// convert length to byte array (4 bytes)
		byte[] messageLengthArray = new byte[4]; // will hold length of bytes in bytes arrray
		messageLengthArray[0] = (byte)((bytes.length & 0xFF000000) >> 24);
		messageLengthArray[1] = (byte)((bytes.length & 0x00FF0000) >> 16);
		messageLengthArray[2] = (byte)((bytes.length & 0x0000FF00) >> 8);
		messageLengthArray[3] = (byte) (bytes.length & 0x000000FF);

		
		byte[] imageArray = getImageBytes(bimg);
		
		// hide bytes in image
		hideBytes(imageArray, messageLengthArray, 0);
		hideBytes(imageArray, bytes, 32);
				
		return bimg;
		
	}

	/**
	 * Hides an image within another image using LSB algorithm
	 * @param cover the bufferedimage used to disguise image
	 * @param secret the bufferedimage to be hidden
	 * @return new bufferedimage with contents of secret image embedded in cover image
	 */
	private static BufferedImage encodeImage(BufferedImage cover, BufferedImage secret) {
		int height = secret.getHeight();
		int width = secret.getWidth();
		
		// store width as byte (2) array
		byte[] widthArray = new byte[2];
		widthArray[0] = (byte)((width & 0x0000FF00) >> 8);
		widthArray[1] = (byte) (width & 0x000000FF);
		
		// same thing for height...
		byte[] heightArray = new byte[2];
		heightArray[0] = (byte)((height & 0x0000FF00) >> 8);
		heightArray[1] = (byte) (height & 0x000000FF);
		
		byte[] bytes = getImageBytes(secret);
		byte[] messageLengthArray = new byte[4]; // will hold length of bytes in bytes array
		// same procedure
		messageLengthArray[0] = (byte)((bytes.length & 0xFF000000) >> 24);
		messageLengthArray[1] = (byte)((bytes.length & 0x00FF0000) >> 16);
		messageLengthArray[2] = (byte)((bytes.length & 0x0000FF00) >> 8);
		messageLengthArray[3] = (byte) (bytes.length & 0x000000FF);
		
		byte[] imageArray = getImageBytes(cover);
		
		hideBytes(imageArray, messageLengthArray, 0); // 0 - 31 bits 
		hideBytes(imageArray, widthArray, 32);        // 32 - 47 bits
		hideBytes(imageArray, heightArray, 48);       // 48 to 63 bits
		hideBytes(imageArray, bytes, 64);             // 64 to bytes.length bits
		
		return cover;

	}
	
	/**
	 * Retrieves hidden image inside another image using LSB algorithm
	 * @param bimg the bufferedimage with hidden image inside
	 * @return bufferedimage that was encoded in bimg
	 */
	private static BufferedImage decodeImage(BufferedImage bimg) {
		byte[] imageArray = getImageBytes(bimg);
		int length = 0, height = 0, width = 0;
		
		for (int i = 0; i < 64; i++) {
			if (i < 32) // length is stored in first 32 bits (4bytes)
				length = (length << 1) | (imageArray[i] & 1);
			else if (i < 48) // width is stored 16 bits (2 bytes)
				width = (width << 1) | (imageArray[i] & 1);
			else             // height is stored in 16 bits (2 bytes) 
				height = (height << 1) | (imageArray[i] & 1);
		}
		
		byte[] result = new byte[length];
		int index = 64; // skip first 8 bytes (64 bits) for length, height, & width

		// loop til length of message and grab each LSB from image
		for (int i = 0; i < result.length; i++) {
			for (int j = 0; j < 8; j++) { // each bit
				// shift by 1 and grab last bit of next image byte
				result[i] = (byte) ((result[i] << 1) | (imageArray[index] & 1));
				index++;
			}
		}
		
		// swap blue with red to follow RGB sequence from BGR
		for (int i = 0; i < result.length; i+=3) {
			byte temp = result[i];
			result[i] = result[i+2];
			result[i+2] = temp;
		}
		
		// create image
		BufferedImage b = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		WritableRaster raster = b.getRaster();
		raster.setDataElements(0, 0, width, height, result);
		
		return b;
	}
	
	/**
	 * Gets the hidden text from an image using LSB algorithm
	 * @param bimg the image with the hidden text inside
	 * @return the hidden text
	 */
	private static String decodeText(BufferedImage bimg) {
		byte[] imageArray = getImageBytes(bimg);
		
		int length = 0;
		
		// get length stored in first 4 bytes of image
		for (int i = 0; i < 32; i++) {
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
		
		String message = "";
		// convert to string
		for (byte b : result)
			message += (char)b;
		
		return message;
		
	}
	
	
	public static void main(String[] args) {
		// test encoding and decoding text and image
		try {
			BufferedImage original = convertToBGR(ImageIO.read(new File("background.png")));
			
			// try encoding text in image
			File txtFile = new File("text.txt");
			Scanner sc = new Scanner(txtFile);
			String message = "";
			while (sc.hasNextLine())
				message += sc.nextLine() + "\n";
			sc.close();
			BufferedImage encodedText = encodeText(original, message);
			ImageIO.write(encodedText,"png", new File("encodedText.png"));
			System.out.println("-> Finished encoding text into image!");
			
			// try decoding text in image
			String msg = decodeText(encodedText);
			System.out.println(msg);
			System.out.println("-> Finished decoding text from image!");
			
			// try encoding image in image
			BufferedImage secret = convertToBGR(ImageIO.read(new File("secret.png")));
			BufferedImage encodedImage = encodeImage(original, secret);
			ImageIO.write(encodedImage, "png", new File("encodedImage.png"));
			System.out.println("-> Finished encoding image in image!");
			
			// try decoding image from image
			BufferedImage decoded = decodeImage(encodedImage);
			ImageIO.write(decoded, "png", new File("decodedImage.png"));
			System.out.println("-> Finished decoding image in image!");
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
		
	}

}
