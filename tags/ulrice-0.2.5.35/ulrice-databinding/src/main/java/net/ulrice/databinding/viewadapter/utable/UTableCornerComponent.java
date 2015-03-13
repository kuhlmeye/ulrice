package net.ulrice.databinding.viewadapter.utable;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;
import javax.swing.Painter;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

public class UTableCornerComponent extends JComponent {

    private static final long serialVersionUID = 669253678109419823L;

    private final String key;

    public UTableCornerComponent(String key) {
        super();
        
        this.key = key;
    }

    @Override
    public void paint(Graphics graphics) {
        super.paint(graphics);

        UIDefaults uiDefaults = UIManager.getLookAndFeelDefaults();

        if (uiDefaults != null) {
            @SuppressWarnings("unchecked")
            Painter<Object> painter = (Painter<Object>) uiDefaults.get("UTableCorderComponent." + key + ".backgroundPainter");

            if (painter != null) {
                Graphics2D g = (Graphics2D) graphics.create();
                int width = getWidth();
                int height = getHeight();

                painter.paint(g, this, width, height);
                
                return;
            }
        }
    }
}
