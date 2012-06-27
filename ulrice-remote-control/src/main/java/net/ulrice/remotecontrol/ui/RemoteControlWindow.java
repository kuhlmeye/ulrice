package net.ulrice.remotecontrol.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JWindow;
import javax.swing.SwingConstants;

import net.ulrice.remotecontrol.RemoteControlCenter;
import net.ulrice.remotecontrol.RemoteControlException;
import net.ulrice.remotecontrol.util.RemoteControlUtils;

public class RemoteControlWindow extends JWindow implements ActionListener {

    private static class DragListener extends MouseAdapter {

        private final JWindow window;

        private Point dragPoint;

        public DragListener(JWindow window) {
            super();

            this.window = window;
        }

        /**
         * {@inheritDoc}
         * 
         * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
         */
        @Override
        public void mousePressed(MouseEvent e) {
            dragPoint = (Point) e.getPoint().clone();
        }

        /**
         * {@inheritDoc}
         * 
         * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
         */
        @Override
        public void mouseReleased(MouseEvent e) {
            dragPoint = null;
        }

        /**
         * {@inheritDoc}
         * 
         * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
         */
        @Override
        public void mouseDragged(MouseEvent e) {
            if (dragPoint != null) {
                Point point = (Point) e.getPoint().clone();
                Point location = window.getLocation();

                location.x += point.x - dragPoint.x;
                location.y += point.y - dragPoint.y;

                window.setLocation(location);
            }
        }
    }

    private static final long serialVersionUID = -3532363641302143886L;

    private static final String PLAY_ACTION = "play";
    private static final String STEP_ACTION = "step";
    private static final String INFO_ACTION = "info";
    private static final String PAUSE_ON_ERROR_ACTION = "pauseOnError";
    private static final String MINUS_ACTION = "minus";
    private static final String PLUS_ACTION = "plus";
    private static final String STOP_ACTION = "stop";

    private static final Icon GRIP_ICON;
    private static final Icon PLAY_ICON;
    private static final Icon PAUSE_ICON;
    private static final Icon PAUSE_ON_ERROR_ICON;
    private static final Icon STEP_ICON;
    private static final Icon DOWN_ICON;
    private static final Icon UP_ICON;
    // private static final Icon INFO_ICON;
    private static final Icon MINUS_ICON;
    private static final Icon PLUS_ICON;
    private static final Icon STOP_ICON;
    private static final Icon OK_ICON;
    private static final Icon WARNING_ICON;
    private static final Icon ERROR_ICON;

    private static final double[] SPEED_VALUES = { 0, 0.25, 0.5, 1, 2, 4, 8, 16, 32, 64 };

    static {
        try {
            GRIP_ICON = new ImageIcon(ImageIO.read(RemoteControlWindow.class.getResource("grip.png")));
            PLAY_ICON = new ImageIcon(ImageIO.read(RemoteControlWindow.class.getResource("play.png")));
            PAUSE_ICON = new ImageIcon(ImageIO.read(RemoteControlWindow.class.getResource("pause.png")));
            PAUSE_ON_ERROR_ICON =
                    new ImageIcon(ImageIO.read(RemoteControlWindow.class.getResource("pauseOnError.png")));
            STEP_ICON = new ImageIcon(ImageIO.read(RemoteControlWindow.class.getResource("step.png")));
            DOWN_ICON = new ImageIcon(ImageIO.read(RemoteControlWindow.class.getResource("down.png")));
            UP_ICON = new ImageIcon(ImageIO.read(RemoteControlWindow.class.getResource("up.png")));
            // INFO_ICON = new ImageIcon(ImageIO.read(RemoteControlWindow.class.getResource("info.png")));
            MINUS_ICON = new ImageIcon(ImageIO.read(RemoteControlWindow.class.getResource("minus.png")));
            PLUS_ICON = new ImageIcon(ImageIO.read(RemoteControlWindow.class.getResource("plus.png")));
            STOP_ICON = new ImageIcon(ImageIO.read(RemoteControlWindow.class.getResource("stop.png")));
            OK_ICON = new ImageIcon(ImageIO.read(RemoteControlWindow.class.getResource("ok.png")));
            WARNING_ICON = new ImageIcon(ImageIO.read(RemoteControlWindow.class.getResource("warning.png")));
            ERROR_ICON = new ImageIcon(ImageIO.read(RemoteControlWindow.class.getResource("error.png")));
        }
        catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private final DragListener dragListener = new DragListener(this);

    private final JLabel gripLabel;
    private final JButton playButton;
    private final JButton stepButton;
    private final JToggleButton pauseOnErrorButton;
    private final JLabel infoLabel;
    private final JTextField infoField;
    private final JButton infoButton;
    private final JTextArea infoArea;
    private final JScrollPane infoPane;
    private final JButton minusButton;
    private final JTextField speedField;
    private final JButton plusButton;
    private final JButton stopButton;

    public RemoteControlWindow() {
        super();

        JToolBar toolbar = new JToolBar() {

            private static final long serialVersionUID = 5363374860898779797L;

            @Override
            protected void paintComponent(Graphics graphics) {
                Graphics2D g = (Graphics2D) graphics;

                Color COLOR_A = new Color(0xffffff);
                Color COLOR_B = new Color(0xe7e3f1);
                Color COLOR_C = new Color(0xcbc2e1);
                Color COLOR_D = new Color(0xffffff);

                g.setPaint(new LinearGradientPaint(0, 0, 0, getHeight(), new float[] { 0.0f, 0.45f, 0.46f, 1.0f },
                    new Color[] { COLOR_A, COLOR_B, COLOR_C, COLOR_D }));
                g.fillRect(0, 0, getWidth(), getHeight());

                super.paintComponent(g);
            }

        };

        toolbar.setFloatable(false);
        toolbar.setBorder(BorderFactory.createEmptyBorder());
        toolbar.setOpaque(false);

        gripLabel = createLabel(GRIP_ICON);
        gripLabel.setPreferredSize(new Dimension(GRIP_ICON.getIconWidth() + 4, GRIP_ICON.getIconHeight()));
        gripLabel.setHorizontalAlignment(SwingConstants.LEFT);
        playButton = createButton(PLAY_ICON, "Play", PLAY_ACTION);
        stepButton = createButton(STEP_ICON, "Step", STEP_ACTION);
        pauseOnErrorButton = createToggleButton(PAUSE_ON_ERROR_ICON, "Pause On Error", PAUSE_ON_ERROR_ACTION);
        infoLabel = createLabel(OK_ICON);
        infoField = createTextField("Ulrice Remote Control", 48);
        infoField.addMouseListener(dragListener);
        infoField.addMouseMotionListener(dragListener);
        infoButton = createButton(DOWN_ICON, "Info", INFO_ACTION);
        infoArea = createTextArea();
        infoPane = new JScrollPane(infoArea);
        infoPane.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLACK));
        infoPane.setVisible(false);
        minusButton = createButton(MINUS_ICON, "Slow Down", MINUS_ACTION);
        speedField = createTextField("100 %", 5);
        speedField.setHorizontalAlignment(SwingConstants.CENTER);
        plusButton = createButton(PLUS_ICON, "Speed Up", PLUS_ACTION);
        stopButton = createButton(STOP_ICON, "Stop", STOP_ACTION);

        toolbar.add(gripLabel);
        toolbar.add(playButton);
        toolbar.add(stepButton);
        toolbar.addSeparator();
        toolbar.add(infoLabel);
        toolbar.add(infoField);
        toolbar.add(infoButton);
        toolbar.addSeparator();
        toolbar.add(pauseOnErrorButton);
        toolbar.addSeparator();
        toolbar.add(minusButton);
        toolbar.add(speedField);
        toolbar.add(plusButton);
        toolbar.addSeparator();
        toolbar.add(stopButton);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(toolbar, BorderLayout.NORTH);
        panel.add(infoPane, BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK));

        setContentPane(panel);

        updateState();
    }

    public void updateState() {
        double speedFactor = RemoteControlUtils.speedFactor();
        int speedIndex = getSpeedIndex();

        if (speedFactor <= 0.1) {
            speedField.setText("Turbo!");
        }
        else {
            speedField.setText(String.format("%,.1f%%", 100 / speedFactor));
        }

        if (RemoteControlCenter.isPausing()) {
            playButton.setIcon(PLAY_ICON);
        }
        else {
            playButton.setIcon(PAUSE_ICON);
        }

        stepButton.setEnabled(RemoteControlCenter.isWaiting());
        pauseOnErrorButton.setSelected(RemoteControlCenter.isPauseOnError());
        minusButton.setEnabled(speedIndex < (SPEED_VALUES.length - 1));
        plusButton.setEnabled(speedIndex > 0);

    }

    public void activate() {
        pack();

        Dimension screenSize = getToolkit().getScreenSize();
        Dimension windowSize = getSize();

        setLocation((screenSize.width - windowSize.width) / 2, 0);
        setAlwaysOnTop(true);

        updateState();

        setVisible(true);
    }

    public void info(String info) {
        infoLabel.setIcon(OK_ICON);
        infoField.setText(trim(info));
        infoArea.setText(info);
    }

    public void warning(String warning) {
        infoLabel.setIcon(WARNING_ICON);
        infoField.setText(trim(warning));
        infoArea.setText(warning);
    }

    public void error(String error) {
        infoLabel.setIcon(ERROR_ICON);
        infoField.setText(trim(error));
        infoArea.setText(error);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();

        if (PLAY_ACTION.equals(action)) {
            onPlay();
            return;
        }

        if (STEP_ACTION.equals(action)) {
            onStep();
            return;
        }

        if (INFO_ACTION.equals(action)) {
            onInfo();
            return;
        }

        if (PAUSE_ON_ERROR_ACTION.equals(action)) {
            onPauseOnError();
            return;
        }

        if (MINUS_ACTION.equals(action)) {
            int index = getSpeedIndex();

            if (index < (SPEED_VALUES.length - 1)) {
                onSpeed(SPEED_VALUES[index + 1]);
            }
            return;
        }

        if (PLUS_ACTION.equals(action)) {
            int index = getSpeedIndex();

            if (index > 0) {
                onSpeed(SPEED_VALUES[index - 1]);
            }
            return;
        }

        if (STOP_ACTION.equals(action)) {
            onStop();
            return;
        }
    }

    private void onPlay() {
        if (RemoteControlCenter.isPausing()) {
            if (RemoteControlCenter.isWaiting()) {
                RemoteControlCenter.setPausing(false);
                RemoteControlCenter.nextStep();
            }
        }
        else {
            RemoteControlCenter.setPausing(true);
        }
        updateState();
    }

    private void onStep() {
        if (RemoteControlCenter.isPausing()) {
            if (RemoteControlCenter.isWaiting()) {
                RemoteControlCenter.nextStep();
            }
        }

        updateState();
    }

    private void onInfo() {
        if (infoPane.isVisible()) {
            infoPane.setVisible(false);
            infoButton.setIcon(DOWN_ICON);
        }
        else {
            infoPane.setVisible(true);
            infoButton.setIcon(UP_ICON);
        }

        pack();
        updateState();
    }

    private void onPauseOnError() {
        RemoteControlCenter.setPauseOnError(pauseOnErrorButton.isSelected());
    }

    private void onStop() {
        setVisible(false);

        // if (RemoteControlCenter.isClientConnected()) {
        // try {
        // RemoteControlCenter.applicationRC().shutdown();
        // }
        // catch (RemoteControlException e) {
        // e.printStackTrace(System.err);
        // }
        // }

        System.exit(0);
    }

    private void onSpeed(double speedFactor) {
        RemoteControlUtils.overrideSpeedFactor(speedFactor);

        if (RemoteControlCenter.isClientConnected()) {
            try {
                RemoteControlCenter.applicationRC().overrideSpeedFactor(speedFactor);
            }
            catch (RemoteControlException e) {
                e.printStackTrace(System.err);
            }
        }

        updateState();
    }

    private int getSpeedIndex() {
        int index = -1;
        double minimum = Double.MAX_VALUE;

        for (int i = 0; i < SPEED_VALUES.length; i += 1) {
            double distance = Math.abs(SPEED_VALUES[i] - RemoteControlUtils.speedFactor());

            if (distance < minimum) {
                minimum = distance;
                index = i;
            }
        }

        return index;
    }

    private JLabel createLabel(Icon icon) {
        JLabel label = new JLabel(icon);

        label.addMouseListener(dragListener);
        label.addMouseMotionListener(dragListener);

        return label;
    }

    private JButton createButton(Icon icon, String text, String command) {
        JButton button = new JButton(icon);

        button.setActionCommand(command);
        button.setToolTipText(text);
        button.addActionListener(this);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setOpaque(false);

        return button;
    }

    private JToggleButton createToggleButton(Icon icon, String text, String command) {
        JToggleButton button = new JToggleButton(icon);

        button.setActionCommand(command);
        button.setToolTipText(text);
        button.addActionListener(this);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setOpaque(false);

        return button;
    }

    private JTextField createTextField(String text, int columns) {
        JTextField field = new JTextField(text, columns);

        field.setEditable(false);
        field.setFont(new Font(Font.DIALOG, Font.PLAIN, 10));
        field.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));
        field.setOpaque(false);

        return field;
    }

    private JTextArea createTextArea() {
        JTextArea area = new JTextArea(5, 48);

        area.setEditable(false);
        area.setLineWrap(false);
        area.setBackground(new Color(0xfeffcc));
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 10));

        return area;
    }

    private static String trim(String s) {
        int index = s.indexOf('\n');

        if (index >= 0) {
            return s.substring(0, index) + " ...";
        }

        return s;
    }

    public static void main(String[] args) {
        RemoteControlCenter.activateControlWindow();
    }

}
