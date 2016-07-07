import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.util.logging.Logger;
import java.awt.EventQueue;

/**
 *
 * @author UlisesM
 */
public class SteganographyGui extends JFrame {
	
    private ButtonGroup buttonGroup; // to hold radio buttons
    private JButton coverButton;
    private JLabel coverLabel;  // display path of image
    private JTextField coverTextField;
    private JRadioButton decodeRadio;
    private JRadioButton encodeRadio;
    private JButton mainButton;  // button where magic happens (either to encode or decode)
    private JButton secretButton;
    private JLabel secretLabel;
    private JTextField secretTextField; // display path of secret image or text file

    /**
     * Creates new form Frame
     */
    public SteganographyGui() {
        initComponents();
    }

    private void initComponents() {
    	// initialize all variables
        buttonGroup = new ButtonGroup();
        encodeRadio = new JRadioButton("Encode");
        decodeRadio = new JRadioButton("Decode");
        coverTextField = new JTextField();
        secretTextField = new JTextField();
        coverButton = new JButton("Choose");
        secretButton = new JButton("Choose");
        mainButton = new JButton("Do it!");
        coverLabel = new JLabel("Cover image");
        secretLabel = new JLabel("Secret image / text");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Steganography");

        encodeRadio.setSelected(true);
        buttonGroup.add(encodeRadio);
        buttonGroup.add(decodeRadio);
        
        setCustomLayout();
        
        // user can't edit path
        coverTextField.setEditable(false);
        secretTextField.setEditable(false);

       
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
                     .addGroup(layout.createSequentialGroup()
                         .addContainerGap(171, Short.MAX_VALUE)
                         .addComponent(encodeRadio)
                         .addGap(55, 55, 55)
                         .addComponent(decodeRadio)
                         .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 90, Short.MAX_VALUE))
                     .addGroup(GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                         .addContainerGap()
                         .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                             .addComponent(coverLabel)
                             .addComponent(secretLabel))
                         .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                         .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                             .addComponent(coverTextField, GroupLayout.Alignment.LEADING)
                             .addComponent(secretTextField, GroupLayout.Alignment.LEADING))))
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
                     .addComponent(encodeRadio)
                     .addComponent(decodeRadio))
                 .addGap(31, 31, 31)
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
