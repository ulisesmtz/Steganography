import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
	 * @param bytes the message/image to hide
	 * @param isStoreLength true if hiding length of text, false otherwise
	 */
	private static void hideText(byte[] bimg, byte[] bytes, boolean isStoreLength) {
		int index = 0; // where to start writing in image
		
		if (!isStoreLength) // start at bit 32
			index = 32;
		
		for (int i = 0; i < bytes.length; i++) {
			int character = bytes[i];
			for (int j = 7; j >= 0; j--) {  // 8 bits per character
				// get corresponding bit
				int bit = (character >> j) & 1;
				
				// get last image byte, set last bit to 0 ( AND 0xFE) then OR with bit
				bimg[index] = (byte) ((bimg[index] & 0xFE) | bit);
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
	 * @param bytes the bytes to be hidden
	 * @return new image with the bytes embedded in it
	 */
	private static BufferedImage encode(BufferedImage bimg, byte[] bytes) {
		BufferedImage newImage = null;
	//	byte[] messageArray = message.getBytes();
		
		// convert length to byte array (4 bytes)
		byte[] messageLengthArray = new byte[4]; // will hold length of bytes in bytes arrray
		messageLengthArray[0] = (byte)((bytes.length & 0xFF000000) >> 24);
		messageLengthArray[1] = (byte)((bytes.length & 0x00FF0000) >> 16);
		messageLengthArray[2] = (byte)((bytes.length & 0x0000FF00) >> 8);
		messageLengthArray[3] = (byte) (bytes.length & 0x000000FF);

		
		// copy original image to new image using graphics
		newImage = new BufferedImage(bimg.getWidth(), bimg.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
		Graphics g = newImage.createGraphics();
		g.drawImage(bimg, 0, 0, null);
		g.dispose();		
	
		byte[] imageArray = getImageBytes(newImage);
			
		// hide bytes in image
		hideText(imageArray, messageLengthArray, true);
		hideText(imageArray, bytes, false);
				
		return newImage;
		
	}
	
	/**
	 * Gets the hidden text from an image using LSB algorithm
	 * @param bimg the image with the hidden text inside
	 * @return the hidden bytes
	 */
	private static byte[] decode(BufferedImage bimg) {
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
		
		return result;
		
	}
	
	
	public static void main(String[] args) {
//		
//		BufferedImage bimg = null;  // original image to hide text in
//		BufferedImage newImage = null;  // output of image with text embedded
//		Scanner scanner = null;     // retrieve text using scanner
//		
//		try {
//			scanner = new Scanner(new File("C://Users//UlisesM//Desktop//text.txt"));
//			bimg = ImageIO.read(new File("C://Users//UlisesM//Desktop//flyer+gator6.png"));
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//				
//		String message = "";
//		
//		// store contents of text in variable message
//		while (scanner.hasNextLine()) {
//			message += scanner.nextLine() + "\n";
//		}
//		
//		newImage = encode(bimg, message);
//	
//		String msg = decode(newImage);
//		
//		//System.out.println(msg); // test
//		
		BufferedImage b = null;
		try {
			  b = ImageIO.read(new File("C://Users//UlisesM//Desktop//small.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		BufferedImage coverImage = null, secretImage = null;
		try {
			BufferedImage coverImage2 = ImageIO.read(new File("C://Users//UlisesM//Desktop//flyer+gator6.png"));
			BufferedImage secretImage2 = ImageIO.read(new File("C://Users//UlisesM//Desktop//small2.png"));
			coverImage = new BufferedImage(coverImage2.getWidth(), coverImage2.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
			Graphics g = coverImage.createGraphics();
			g.drawImage(coverImage2, 0, 0, null);
			g.dispose();
			
			secretImage = new BufferedImage(secretImage2.getWidth(), secretImage2.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
			Graphics g2 = secretImage.createGraphics();
			g2.drawImage(secretImage2, 0, 0, null);
			g2.dispose();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
		// test normal message
		BufferedImage b = encode(coverImage, "Hello\nThis is a \nMesdfhdfhdhfgdshjfgshjfgdshg fsdjgfhdsgfdsfdssdfkjrthrbrhfbgehgvefhdjfbdssdsdsad\ngfgdfgsdasafe form ".getBytes());
		byte[] c = decode(b);
		for (byte x : c) System.out.print((char)x);
		
		//**********************************
		// test images
		BufferedImage result = encode(coverImage, getImageBytes(secretImage));
		try {
			ImageIO.write(result, "png", new File("C://Users//UlisesM//Desktop//new.png"));
			ImageIO.write(b, "png", new File("C://Users//UlisesM//Desktop//b.png"));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("\n----");
		
		
		byte[] pp = decode(result);
		
		// swap blue with red
		for (int i=0;i<pp.length;i+=3) {
			byte temp = pp[i];
			pp[i] = pp[i+2];
			pp[i+2] = temp;
		}
		
		
		BufferedImage k = new BufferedImage(secretImage.getWidth(), secretImage.getHeight(), secretImage.getType());
		WritableRaster raster = k.getRaster();
		raster.setDataElements(0, 0, secretImage.getWidth(), secretImage.getHeight(), pp);
		System.out.println("+-+-+-+-+");
		try {
			ImageIO.write(k, "png", new File("C://Users//UlisesM//Desktop//newimage.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	

		
	}

}
