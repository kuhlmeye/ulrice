package net.ulrice.ui.accordionpanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.ulrice.ui.components.HorizontalScrollPane;

public class AccordionContentPanel extends JPanel implements ActionListener {

    private static final long ANIMATION_DURATION = 200;

    private static class Animation extends Thread {

        private static final int FPS = 60;

        private final long startMillis;
        private final AccordionContentPanel panel;
        private final long duration;
        private final boolean folding;

        public Animation(AccordionContentPanel panel, long duration, boolean folding) {
            super("Animation");

            setDaemon(true);
            startMillis = System.currentTimeMillis();

            this.panel = panel;
            this.duration = duration;
            this.folding = folding;
        }

        @Override
        public void run() {
            while (true) {
                final long millis = System.currentTimeMillis() - startMillis;
                final double factor = Math.sin(Math.min((double) millis / duration, 1) * (Math.PI / 2));

                try {
                    SwingUtilities.invokeAndWait(new Runnable() {
                        @Override
                        public void run() {
                            if (millis > duration) {
                                // complete
                                if (folding) {
                                    panel.setUnfoldedFactor(0);
                                }
                                else {
                                    panel.setUnfoldedFactor(1);
                                }
                            }

                            panel.setUnfoldedFactor((folding) ? 1 - factor : factor);
                            panel.revalidate();

                            if (!folding) {
                                panel.scrollRectToVisible(new Rectangle(panel.getSize()));
                            }
                        }
                    });
                }
                catch (InterruptedException e) {
                    break;
                }
                catch (InvocationTargetException e) {
                    e.printStackTrace(System.err);
                    break;
                }

                if (millis > duration) {
                    break;
                }

                try {
                    Thread.sleep(Math.min(Math.max((millis + (1000 / FPS)) - (System.currentTimeMillis() - startMillis), 1), 1000));
                }
                catch (InterruptedException e) {
                    break;
                }
            }
        }
    }

    private static final long serialVersionUID = 5734171488424370652L;

    private final AccordionSeparatorPanel separatorPanel;
    private final HorizontalScrollPane scrollPane;
    private final Component content;
    private final Component footer;

    private String actionCommand;
    private boolean folded = false;
    private double unfoldedFactor = 1;
    private boolean standalone = true;

    public AccordionContentPanel(String title, Component content, Color seperatorBackgroundColor) {
        super(new AccordionContentPanelLayout());

        separatorPanel = new AccordionSeparatorPanel(title, seperatorBackgroundColor);
        separatorPanel.addActionListener(this);

        this.content = content;

        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xa8a8a8)));

        scrollPane = new HorizontalScrollPane(content);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        footer = new AccordionContentFooter();

        add(separatorPanel);
        add(scrollPane, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);
    }

    public String getActionCommand() {
        return actionCommand;
    }

    public void setActionCommand(String actionCommand) {
        this.actionCommand = actionCommand;
    }

    public void addActionListener(ActionListener l) {
        listenerList.add(ActionListener.class, l);
    }

    public void removeActionListener(ActionListener l) {
        listenerList.remove(ActionListener.class, l);
    }

    protected void fireActionPerformed(ActionEvent event) {
        Object[] listeners = listenerList.getListenerList();
        ActionEvent e = null;

        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ActionListener.class) {
                e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, getActionCommand(), event.getWhen(), event.getModifiers());
            }
            ((ActionListener) listeners[i + 1]).actionPerformed(e);
        }
    }

    public boolean isFolded() {
        return folded;
    }

    public void setInitialFolded(boolean folded) {
        separatorPanel.setOpened(!folded);

        this.folded = folded;
        setUnfoldedFactor((folded) ? 0 : 1);
    }

    public void setFolded(boolean folded) {
        if (this.folded == folded) {
            return;
        }

        separatorPanel.setOpened(!folded);

        if (folded) {
            new Animation(this, ANIMATION_DURATION, true).start();
        }
        else {
            new Animation(this, ANIMATION_DURATION, false).start();
        }

        this.folded = folded;
        firePropertyChange("folded", !folded, folded);
    }

    public double getUnfoldedFactor() {
        return unfoldedFactor;
    }

    public void setUnfoldedFactor(double unfoldedFactor) {
        this.unfoldedFactor = unfoldedFactor;

        if (content != null) {
            content.setVisible(unfoldedFactor > 0);
        }
    }

    public boolean isStandalone() {
        return standalone;
    }

    public void setStandalone(boolean standalone) {
        this.standalone = standalone;
    }

    public boolean isContent(Component component) {
        return (component == scrollPane) || (component == content);
    }

    public void setTitle(String title) {
        separatorPanel.setTitle(title);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        fireActionPerformed(e);
    }

    public Component getContent() {
        return content;
    }

    public AccordionSeparatorPanel getSeparatorPanel() {
        return separatorPanel;
    }
}
