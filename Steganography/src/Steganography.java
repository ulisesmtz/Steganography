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
	
	private final int MESSAGE_LENGTH_BITS = 32;  // max bits used to encode length of message 
	private final int WIDTH_BITS = 16;			 // max bits used to encode length of image width 
	private final int HEIGHT_BITS = 16; 		 // max bits used to encode length of image height
	
	/**Converts an image to 3 bytes of blue, green, red each, respectively
	 * @param bimg the bufferedimage to be converted to
	 * @return new bufferedimage with color scheme desired
	 */
	private BufferedImage convertToBGR(BufferedImage bimg) {
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
	private void hideBytes(byte[] bimg, byte[] bytes, int offset) {
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
	private byte[] getImageBytes(BufferedImage bimg) {
		WritableRaster raster = bimg.getRaster();
		DataBufferByte dbb = (DataBufferByte) raster.getDataBuffer();
		return dbb.getData();
	}
	
	/**
	 * Gets the bytes of an integer and stores them in a byte array
	 * @param size the size of the byte array
	 * @param number which integer to convert to bytes
	 * @return byte array with number in bytes
	 */
	private byte[] getBytesFromInt(int size, int number) {
		byte[] b = new byte[size];
		
		for (int i = size-1, shift = 0; i >= 0; i--, shift += 8) 
			b[i] = (byte) ((number & (0x000000FF << shift)) >> shift);
		
		return b;
	}
	
	/**
	 * Reads the content of a file and returns it in a String
	 * @param file the .txt file to be read
	 * @return contents of .txt file in String
	 */
	public String getText(File file) {
		Scanner scanner = null;
		String text = "";
		try {
			scanner = new Scanner(file);
			
			while (scanner.hasNextLine()) 
				text += scanner.nextLine() + "\n"; // add new line char to at end of every line
			
		} catch (FileNotFoundException e) {
			System.err.println("Could not find file " + file);
		} finally {
			scanner.close();
		}
		
		return text;
	}
	
	/**
	 * Encodes bytes into an image using the least significant bit (LSB) algorithm
	 * @param bimg the image used to hide text in
	 * @param message the text to be hidden
	 * @return new image with the text embedded in it
	 */
	public BufferedImage encodeText(BufferedImage bimg, String message) {
		byte[] bytes = message.getBytes();
		byte[] messageLengthArray = getBytesFromInt(MESSAGE_LENGTH_BITS/8, bytes.length); // 8 bits per byte
		byte[] imageArray = getImageBytes(bimg);
		
		// hide bytes in image
		hideBytes(imageArray, messageLengthArray, 0);
		hideBytes(imageArray, bytes, MESSAGE_LENGTH_BITS);
				
		return bimg;
	}

	/**
	 * Hides an image within another image using LSB algorithm
	 * @param cover the bufferedimage used to disguise image
	 * @param secret the bufferedimage to be hidden
	 * @return new bufferedimage with contents of secret image embedded in cover image
	 */
	public BufferedImage encodeImage(BufferedImage cover, BufferedImage secret) {
		int height = secret.getHeight();
		int width = secret.getWidth();
		byte[] bytes = getImageBytes(secret);

		// get byte array for width, height, and message (8 bytes = 1 bit)
		byte[] widthArray = getBytesFromInt(WIDTH_BITS/8, width);
		byte[] heightArray = getBytesFromInt(HEIGHT_BITS/8, height);
		byte[] messageLengthArray = getBytesFromInt(MESSAGE_LENGTH_BITS/8, bytes.length);
		
		byte[] imageArray = getImageBytes(cover);
		
		// hide bytes with offset of previous bit lengths added up
		hideBytes(imageArray, messageLengthArray, 0); 
		hideBytes(imageArray, widthArray, MESSAGE_LENGTH_BITS);   
		hideBytes(imageArray, heightArray, MESSAGE_LENGTH_BITS + WIDTH_BITS);      
		hideBytes(imageArray, bytes, MESSAGE_LENGTH_BITS + WIDTH_BITS + HEIGHT_BITS);
		
		return cover;
	}
	
	/**
	 * Retrieves hidden image inside another image using LSB algorithm
	 * @param bimg the bufferedimage with hidden image inside
	 * @return bufferedimage that was encoded in bimg
	 */
	public BufferedImage decodeImage(BufferedImage bimg) {
		byte[] imageArray = getImageBytes(bimg);
		int length = 0, height = 0, width = 0;
		
		// loop through all encoded length bits and store value in corresponding variable
		for (int i = 0; i < (MESSAGE_LENGTH_BITS + WIDTH_BITS + HEIGHT_BITS); i++) {
			if (i < MESSAGE_LENGTH_BITS) 
				length = (length << 1) | (imageArray[i] & 1);
			else if (i < MESSAGE_LENGTH_BITS + WIDTH_BITS) 
				width = (width << 1) | (imageArray[i] & 1);
			else             
				height = (height << 1) | (imageArray[i] & 1);
		}
		
		byte[] result = new byte[length];
		
		// skip all encoded lengths (message_length, width and height bits)
		int index = MESSAGE_LENGTH_BITS + WIDTH_BITS + HEIGHT_BITS;
		
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
	public String decodeText(BufferedImage bimg) {
		byte[] imageArray = getImageBytes(bimg);
		
		int length = 0;
		
		// get length stored in messgae_length_bits
		for (int i = 0; i < MESSAGE_LENGTH_BITS; i++) {
			length = (length << 1) | (imageArray[i] & 1);
		}
		
		byte[] result = new byte[length];
		int index = MESSAGE_LENGTH_BITS; // skip bits for length of message

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
		Steganography stega = new Steganography();
		// test encoding and decoding text and image
		try {
			BufferedImage original = stega.convertToBGR(ImageIO.read(new File("background.png")));
			
			// try encoding text in image
			File txtFile = new File("text.txt");
			String message = stega.getText(txtFile);
			BufferedImage encodedText = stega.encodeText(original, message);
			ImageIO.write(encodedText,"png", new File("encodedText.png"));
			System.out.println("-> Finished encoding text into image!");
			
			// try decoding text in image
			String msg = stega.decodeText(encodedText);
			System.out.println(msg);
			System.out.println("-> Finished decoding text from image!");
			
			// try encoding image in image
			BufferedImage secret = stega.convertToBGR(ImageIO.read(new File("secret.png")));
			BufferedImage encodedImage = stega.encodeImage(original, secret);
			ImageIO.write(encodedImage, "png", new File("encodedImage.png"));
			System.out.println("-> Finished encoding image in image!");
			
			// try decoding image from image
			BufferedImage decoded = stega.decodeImage(encodedImage);
			ImageIO.write(decoded, "png", new File("decodedImage.png"));
			System.out.println("-> Finished decoding image in image!");
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
		
	}

}
