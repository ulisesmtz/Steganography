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
		
		
		// loop through length of message and copy each bit into LSB of each byte in image
		
		
		// **DECRYPTION**
		
		// loop through first 4 bytes of image to get length of message
		
		// loop til length of message and grab each LSB from image
		
	}

}
