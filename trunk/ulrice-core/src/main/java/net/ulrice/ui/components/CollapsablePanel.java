package net.ulrice.ui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.SwingUtilities;

public class CollapsablePanel extends JPanel {

    private static final long serialVersionUID = 1L;
    
    private JPanel titlePanel = new JPanel() {

        private static final long serialVersionUID = 1L;
        private GradientPaint GP;

        protected void paintComponent(Graphics g) {            
            super.paintComponent(g);
            int h = getHeight();
            int w = getWidth();
            
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setPaint(new GradientPaint(0, 0, new Color(0xb7c6e4), 0, h, new Color(0x52748e), true));
            g2d.fillRect(0, 0, w, h);            
        };
    };
    private JLabel title = new JLabel();
    private Popup popup;
    private JToggleButton pinButton = new JToggleButton();
    private JToggleButton expandButton = new JToggleButton();
    private JPanel content = new JPanel();    
    
    public CollapsablePanel() {
        super(new BorderLayout());
        
        pinButton.setIcon(new ImageIcon(CollapsablePanel.class.getResource("pin_gray.png")));
        pinButton.setSelectedIcon(new ImageIcon(CollapsablePanel.class.getResource("pin_red.png")));
        pinButton.setContentAreaFilled(false);
        pinButton.setBorderPainted(false);
        pinButton.setFocusPainted(false);
        pinButton.setBorder(BorderFactory.createEmptyBorder());
        pinButton.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                if(pinButton.isSelected()) {
                    int x = titlePanel.getX();
                    int y = titlePanel.getY() + titlePanel.getHeight();
                    Point p = new Point(x, y);
                    content.setSize(titlePanel.getWidth(), content.getPreferredSize().height);
                    if(popup != null) {
                        popup.hide();
                    }
                    CollapsablePanel.this.add(content, BorderLayout.CENTER);  
                } else {                    
                    CollapsablePanel.this.remove(content);
                }
                CollapsablePanel.this.revalidate();
                CollapsablePanel.this.repaint();
            }
        });
        
        expandButton.setIcon(new ImageIcon(CollapsablePanel.class.getResource("collapse.png")));
        expandButton.setSelectedIcon(new ImageIcon(CollapsablePanel.class.getResource("expand.png")));
        expandButton.setContentAreaFilled(false);
        expandButton.setBorderPainted(false);
        expandButton.setFocusPainted(false);
        expandButton.setBorder(BorderFactory.createEmptyBorder());
        expandButton.addActionListener(new ActionListener() {
            

            @Override
            public void actionPerformed(ActionEvent e) {
                if(expandButton.isSelected()) {
                    int x = titlePanel.getX();
                    int y = titlePanel.getY() + titlePanel.getHeight();
                    Point p = new Point(x, y);
                    content.setSize(titlePanel.getWidth(), content.getPreferredSize().height);
                    
                    SwingUtilities.convertPointToScreen(p, titlePanel);
                    PopupFactory instance = PopupFactory.getSharedInstance();
                    popup = instance.getPopup(titlePanel, content, p.x, p.y);
                    popup.show();
                    
                } else {
                    popup.hide();
                }
            }
        });
        
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.X_AXIS));
        titlePanel.add(pinButton);
        titlePanel.add(expandButton);
        titlePanel.add(title);
        
        title.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
        
        add(titlePanel, BorderLayout.NORTH);
    }
    
    public void setText(String text) {
        title.setText(text);
    }
    
    public JPanel getContent() {
        return content;
    }
    
    public static void main(String[] args) {
        CollapsablePanel panel = new CollapsablePanel();
        panel.setText("Test Panel");
        panel.getContent().setBackground(Color.yellow);
        panel.getContent().add(new JLabel("Hallo, Welt!"));
        
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(panel);
        frame.pack();
        frame.setVisible(true);
    }
}
