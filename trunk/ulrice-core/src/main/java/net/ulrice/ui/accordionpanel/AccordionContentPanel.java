package net.ulrice.ui.accordionpanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;

import net.ulrice.ui.components.HorizontalScrollPane;

public class AccordionContentPanel extends JPanel implements ActionListener {

    private static final long ANIMATION_DURATION = 150;

    private static class Animation extends Thread {
        
        private static final int FPS = 30;

        private final long startMillis;
        private final AccordionContentPanel panel;
        private final long duration;
        private final int startHeight;
        private final int endHeight;

        public Animation(AccordionContentPanel panel, long duration, int startHeight, int endHeight) {
            super("Animation");

            setDaemon(true);
            startMillis = System.currentTimeMillis();

            this.panel = panel;
            this.duration = duration;
            this.startHeight = startHeight;
            this.endHeight = endHeight;
        }

        @Override
        public void run() {
            while (true) {
                final long millis = System.currentTimeMillis() - startMillis;
                final double time = Math.sin(Math.min((double) millis / duration, 1) * (Math.PI / 2));
                final Dimension size = panel.getMaximumSize();

                size.height = startHeight + (int) ((endHeight - startHeight) * time);

                try {
                    SwingUtilities.invokeAndWait(new Runnable() {
                        @Override
                        public void run() {
                            if (millis > duration) {
                                if (endHeight < startHeight) {
                                    panel.content.setVisible(false);
                                }
                                else {
                                    size.height = Integer.MAX_VALUE;
                                }
                            }
                            else if (!panel.content.isVisible()) {
                                panel.content.setVisible(true);
                            }

                            panel.setMaximumSize(size);
                            panel.revalidate();

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

    private String actionCommand;
    private boolean folded = false;

    public AccordionContentPanel(String title, Component content, Color seperatorBackgroundColor) {
        super(new BorderLayout());

        separatorPanel = new AccordionSeparatorPanel(title, seperatorBackgroundColor);
        separatorPanel.addActionListener(this);

        this.content = content;

        scrollPane = new HorizontalScrollPane(content);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        add(separatorPanel, BorderLayout.NORTH);
        // add(new AccordionHolderPanel(seperatorBackgroundColor), BorderLayout.WEST);
        add(scrollPane, BorderLayout.CENTER);
        add(new JSeparator(), BorderLayout.SOUTH);

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
        content.setVisible(!folded);

        if (folded) {
            Dimension maximumSize = getMaximumSize();

            maximumSize.height = getPreferredSize().height - content.getPreferredSize().height;

            setMaximumSize(maximumSize);
        }

        this.folded = folded;
    }

    public void setFolded(boolean folded) {
        if (this.folded == folded) {
            return;
        }

        separatorPanel.setOpened(!folded);

        if (folded) {
            new Animation(this, ANIMATION_DURATION, getPreferredSize().height, getPreferredSize().height - content.getPreferredSize().height).start();
        }
        else {
            new Animation(this, ANIMATION_DURATION, getPreferredSize().height - content.getPreferredSize().height, getPreferredSize().height).start();
        }

        this.folded = folded;
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
