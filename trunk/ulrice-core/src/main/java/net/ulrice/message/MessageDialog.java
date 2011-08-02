package net.ulrice.message;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
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

    JLabel label = new JLabel();

    public MessageDialog() {
        setUndecorated(true);
        add(label);
        label.setOpaque(UIManager.getBoolean(UI.MESSAGEDIALOG_OPAQUE));
        label.setForeground(UIManager.getColor(UI.MESSAGEDIALOG_FOREGROUND));

        // URL resource = getClass().getResource("warning32.png");
        // ImageIcon imageIcon = new ImageIcon(resource);

        // label.setIcon(imageIcon);
        label.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.RED, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        label.setOpaque(true);
    }

    public void setMessage(String message) {
        setPreferredSize(new Dimension(600, 600));
        String m = "<html><body><table border='0'>" + message + "</table></body></html>";
        label.setText(m);
    }
}
