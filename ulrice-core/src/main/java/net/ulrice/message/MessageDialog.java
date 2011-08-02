package net.ulrice.message;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import net.ulrice.ui.UI;

/**
 * Dialog for messages in the ulrice framework. 
 * 
 * @author dritonshoshi
 */
public class MessageDialog extends JDialog {

    /** Generated serialVersionUID */
    private static final long serialVersionUID = -707670329224887436L;

    JTextArea textArea = new JTextArea();
    
    public MessageDialog() {
        setUndecorated(true);
        add(textArea);
        textArea.setEditable(false);
        textArea.setOpaque(UIManager.getBoolean(UI.MESSAGEDIALOG_OPAQUE));
        textArea.setForeground(UIManager.getColor(UI.MESSAGEDIALOG_FOREGROUND));
        
//        URL resource = getClass().getResource("warning32.png");
//        ImageIcon imageIcon = new ImageIcon(resource);

//        label.setIcon(imageIcon);
        textArea.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.RED, 1), BorderFactory
            .createEmptyBorder(5, 5, 5, 5)));

        textArea.setOpaque(true);
    }

    public void setMessage(String message) {
        textArea.setText(message);
        
    }
}
