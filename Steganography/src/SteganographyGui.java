import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.util.Enumeration;
import java.util.logging.Logger;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author UlisesM
 */
public class SteganographyGui extends JFrame implements ActionListener {
	
    private ButtonGroup buttonGroup; // to hold radio buttons
    private JButton coverButton;     // to choose cover image
    private JLabel coverLabel;  
    private JTextField coverTextField;   // display path of cover image
    private JRadioButton decodeImageRadio;
    private JRadioButton encodeImageRadio;
    private JRadioButton encodeTextRadio;
    private JRadioButton decodeTextRadio;
    private JButton mainButton;  // button where magic happens (either to encode or decode)
    private JButton secretButton;// to choose secret image / text file
    private JLabel secretLabel;
    private JTextField secretTextField; // display path of secret image or text file
    private Steganography stega;

    // action commands
    private final String ON = "ON";
    private final String OFF = "OFF";
    private final String FILE1 = "FILE1";
    private final String FILE2 = "FILE2";
    private final String SUBMIT = "SUBMIT";
    
    private final String ENCODE_IMAGE = "Encode image";
    private final String ENCODE_TEXT = "Encode text";
    private final String DECODE_IMAGE = "Decode image";
    private final String DECODE_TEXT = "Decode text";

    /**
     * Creates new form Frame
     */
    public SteganographyGui() {
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Steganography");
    	        
    	// initialize all variables
        buttonGroup = new ButtonGroup();
        encodeImageRadio = new JRadioButton(ENCODE_IMAGE);
        decodeImageRadio = new JRadioButton(DECODE_IMAGE);
        encodeTextRadio = new JRadioButton(ENCODE_TEXT);
        decodeTextRadio = new JRadioButton(DECODE_TEXT);
        coverTextField = new JTextField();
        secretTextField = new JTextField();
        coverButton = new JButton("Choose");
        secretButton = new JButton("Choose");
        mainButton = new JButton("Do it!");
        coverLabel = new JLabel("Cover image");
        secretLabel = new JLabel("Secret image / text");
        stega = new Steganography();

        // user can't edit path
        coverTextField.setEditable(false);
        secretTextField.setEditable(false);

        encodeImageRadio.setSelected(true);
        buttonGroup.add(encodeImageRadio);
        buttonGroup.add(decodeImageRadio);
        buttonGroup.add(encodeTextRadio);
        buttonGroup.add(decodeTextRadio);
        
        // add action commands to components
        decodeImageRadio.setActionCommand(OFF);
        decodeTextRadio.setActionCommand(OFF);
        encodeImageRadio.setActionCommand(ON);
        encodeTextRadio.setActionCommand(ON);
        coverButton.setActionCommand(FILE1);
        secretButton.setActionCommand(FILE2);
        mainButton.setActionCommand(SUBMIT);
        
        // add action listener to components
        decodeImageRadio.addActionListener(this);
        decodeTextRadio.addActionListener(this);
        encodeImageRadio.addActionListener(this);
        encodeTextRadio.addActionListener(this);
        coverButton.addActionListener(this);
        secretButton.addActionListener(this);
        mainButton.addActionListener(this);
        
        setCustomLayout();
    }

    
    /**
     * Gets the file from the user and displays the path in text field
     * @param isText if TXT file is allowed 
     */
    private void getFile(boolean isText) {
		JFileChooser jfc = new JFileChooser();
		FileNameExtensionFilter filter;
		
		//  set filters (only png and txt allowed)
		if (!isText)
			filter = new FileNameExtensionFilter("PNG", "png");
		else
			filter = new FileNameExtensionFilter("PNG and TXT", "png", "txt");
		
		jfc.setFileFilter(filter);
		jfc.setAcceptAllFileFilterUsed(false);
		
		int result = jfc.showOpenDialog(this);
		if (result == JFileChooser.APPROVE_OPTION) {
			File file = jfc.getSelectedFile();
			// display path in corresponding text field
			if (!isText)
				coverTextField.setText(file.getAbsolutePath());
			else
				secretTextField.setText(file.getAbsolutePath());
		}
	}
    
    /**
     * Loops through a given ButtonGroup and returns the text of the 
     * JRadioButtom that is selected
     * @param buttonGroup the ButtonGroup to check which JRadioButton is selected
     * @return the text of the JRadioButton that is selected
     */
    private String getSelectedRadioButton(ButtonGroup buttonGroup) {
    	// create enumeration
    	Enumeration<AbstractButton> radioButtons = buttonGroup.getElements();
    	
    	// loop through and find which JRadioButton is selected
    	while (radioButtons.hasMoreElements()) {
    		JRadioButton jrb = (JRadioButton) radioButtons.nextElement();
    		if (jrb.isSelected()) 
    			return jrb.getText();
    	}
    	
    	return null;
    }
    
    /**
     * Displays an error message to user if something goes awry
     * @param message the message to be display to user 
     */
    private void showErrorMessage(String message) {
    	JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE, null);
    }
    
    private void saveImage(BufferedImage bimg) throws IOException {
    	JFileChooser jfc = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG", "png");
		jfc.setFileFilter(filter);
		jfc.setAcceptAllFileFilterUsed(false);
		int result = jfc.showSaveDialog(this);
		if (result == JFileChooser.APPROVE_OPTION) {
			File f = jfc.getSelectedFile();
			f = new File(f.toString() + ".png");
			ImageIO.write(bimg, "png", f);
		}
    }
    
    private void performAction(String action) throws IOException{
    	File file1 = new File(coverTextField.getText());
    	
    	if (!file1.exists()) {
    		showErrorMessage("Cover image does not exist");
    		return; 
    	}
    	
    	if (action.equals(ENCODE_IMAGE)) {
    		
			if (!secretTextField.getText().endsWith(".png")) {
				showErrorMessage("Second file must be png image");
				return;
			}
			
			File file2 = new File(secretTextField.getText());
			
			if (!file2.exists()) {
				showErrorMessage("Secret image does not exist");
	    		return; 
			}
			
			BufferedImage bimg = stega.encodeImage(ImageIO.read(file1), ImageIO.read(file2));
			saveImage(bimg);
			
		} else if (action.equals(ENCODE_TEXT)) {
			if (!secretTextField.getText().endsWith(".txt")) {
				showErrorMessage("Second file must be txt file");
				return;
			}
			
			File file2 = new File(secretTextField.getText());
			
			if (!file2.exists()) {
				showErrorMessage("Txt file does not exist");
	    		return; 
			}
			
			String text = stega.getText(file2);
			BufferedImage bimg = stega.encodeText(ImageIO.read(file1), text);
			saveImage(bimg);
			
		} else if (action.equals(DECODE_IMAGE)) {
			BufferedImage bimg = stega.decodeImage(ImageIO.read(file1));
			saveImage(bimg);
		} else if (action.equals(DECODE_TEXT)) {
			String message = stega.decodeText(ImageIO.read(file1));
			JFileChooser jfc = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("TXT", "txt");
			jfc.setFileFilter(filter);
			jfc.setAcceptAllFileFilterUsed(false);
			int result = jfc.showSaveDialog(this);
			if (result == JFileChooser.APPROVE_OPTION) {
				File f = jfc.getSelectedFile();
				f = new File(f.toString() + ".txt");
				BufferedWriter writer = new BufferedWriter(new FileWriter(f));
				writer.write(message);
				writer.close();
			}
		} else {
			System.err.println("Unknown option selected");
		}
    }
    
    /**
     * Sets the custom layout for the project
     */
    private void setCustomLayout() {
    	 GroupLayout layout = new GroupLayout(getContentPane());
         getContentPane().setLayout(layout);
         layout.setHorizontalGroup(
             layout.createParallelGroup(GroupLayout.Alignment.LEADING)
             .addGroup(layout.createSequentialGroup()
                 .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                     .addGroup(GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                         .addContainerGap()
                         .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                             .addComponent(coverLabel)
                             .addComponent(secretLabel))
                         .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                         .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                             .addComponent(coverTextField, GroupLayout.Alignment.LEADING)
                             .addComponent(secretTextField, GroupLayout.Alignment.LEADING)))
                     .addGroup(layout.createSequentialGroup()
                         .addContainerGap(140, Short.MAX_VALUE)
                         .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                             .addComponent(encodeImageRadio)
                             .addComponent(encodeTextRadio))
                         .addGap(25, 25, 25)
                         .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                             .addGroup(layout.createSequentialGroup()
                                 .addComponent(decodeTextRadio)
                                 .addGap(0, 0, Short.MAX_VALUE))
                             .addGroup(layout.createSequentialGroup()
                                 .addComponent(decodeImageRadio)
                                 .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 90, Short.MAX_VALUE)))))
                 .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                 .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                     .addComponent(coverButton)
                     .addComponent(secretButton))
                 .addContainerGap())
             .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                 .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                 .addComponent(mainButton)
                 .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
         );
         layout.setVerticalGroup(
             layout.createParallelGroup(GroupLayout.Alignment.LEADING)
             .addGroup(layout.createSequentialGroup()
                 .addContainerGap()
                 .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                     .addComponent(encodeImageRadio)
                     .addComponent(decodeImageRadio))
                 .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                 .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                     .addComponent(encodeTextRadio)
                     .addComponent(decodeTextRadio))
                 .addGap(8, 8, 8)
                 .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                     .addComponent(coverTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                     .addComponent(coverButton)
                     .addComponent(coverLabel))
                 .addGap(18, 18, 18)
                 .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                     .addComponent(secretTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                     .addComponent(secretButton)
                     .addComponent(secretLabel))
                 .addGap(18, 18, 18)
                 .addComponent(mainButton)
                 .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
         );

         pack();
    }
               
    @Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		
		if (command.equals(OFF)) {
			secretButton.setEnabled(false);
			secretTextField.setEnabled(false);
		} else if (command.equals(ON)) {
			secretButton.setEnabled(true);
			secretTextField.setEnabled(true);
		} else if (command.equals(FILE1)) {
			getFile(false);
		} else if (command.equals(FILE2)) {
			getFile(true);
		} else if (command.equals(SUBMIT)) {
			try {
				performAction(getSelectedRadioButton(buttonGroup));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} else {
			System.err.println("Unknown command.");
		}
	}

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SteganographyGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(SteganographyGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(SteganographyGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(SteganographyGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        /* Create and display the form */
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SteganographyGui().setVisible(true);
            }
        });
    }
                 
}
