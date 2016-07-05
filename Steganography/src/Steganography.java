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
	
	
	public static void main(String[] args) {
		Scanner scanner = null;     // retrieve text using scanner
		BufferedImage bimg = null;  // original image to hide text in
		BufferedImage newImage = null;  // output of image with text embedded
		int index = 0; // index at where we will be writing to
		
		// **ENCRYPTION**
		try {
			scanner = new Scanner(new File("C://Users//UlisesM//Desktop//text.txt"));
			bimg = ImageIO.read(new File("C://Users//UlisesM//Desktop//flyer+gator6.png"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
		// store contents of text in variable message
		String message = "";
		while (scanner.hasNextLine()) {
			message += scanner.nextLine() + "\n";
		}
		
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
		
		
		// create byte array from bufferedimage
		WritableRaster raster = newImage.getRaster();
		DataBufferByte dbb = (DataBufferByte) raster.getDataBuffer();
		byte[] imageArray = dbb.getData();
		
		// store length of message in first 4 bytes of image
		for (int i = 0; i < messageLengthArray.length; i++) {
			int character = messageLengthArray[i];
			for (int j = 7; j >= 0; j--) {  // 8 bits per character
				// set last bit in image to 0
				int bit0 = imageArray[index] & 0xFE;
				
				// get corresponding bit
				int bit = (character >> j) & 1;
				imageArray[index] = (byte) (bit0 | bit);
				index++;
			}
		}
		
		// loop through length of message and copy each bit into LSB of each byte in image
		for (int i = 0; i < messageArray.length; i++) {
			int character = messageArray[i];
			for (int j = 7; j >= 0; j--) {  // 8 bits per character
				// set last bit in image to 0
				int bit0 = imageArray[index] & 0xFE;
				
				// get corresponding bit
				int bit = (character >> j) & 1;
				imageArray[index] = (byte) (bit0 | bit);
				index++;
			}
		}
		
		// **DECRYPTION**
		
		// loop through first 4 bytes of image to get length of message
		int length = 0; 
		for (int i = 0; i < 32; ++i) {
			length = (length << 1) | (imageArray[i] & 1);
		}
		
		byte[] result = new byte[length];
		int index2 = 32; // skip first 4 bytes for length of message

		// loop til length of message and grab each LSB from image
		for (int i = 0; i < result.length; i++) {
			for (int j = 0; j < 8; j++) { // each bit
				// shift by 1 and grab last bit of next image byte
				result[i] = (byte) ((result[i] << 1) | (imageArray[index2] & 1));
				index2++;
			}
		}
		
		// test
		for (byte a : result) {
			System.out.print((char)a);
		}
		
		
	}

}
