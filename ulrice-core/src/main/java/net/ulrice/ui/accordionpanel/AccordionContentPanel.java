package net.ulrice.ui.accordionpanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class AccordionContentPanel extends JPanel implements ActionListener {

    private static final int ANIMATION_STEP_SIZE = 8;

    private static final long serialVersionUID = 5734171488424370652L;

    private final AccordionSeparatorPanel separatorPanel;
    private final JComponent content;

    private String actionCommand;

    public AccordionContentPanel(String title, JComponent content, Color seperatorBackgroundColor) {
        super(new BorderLayout());

        separatorPanel = new AccordionSeparatorPanel(title, seperatorBackgroundColor);
        separatorPanel.addActionListener(this);

        this.content = content;

        add(content, BorderLayout.CENTER);
        add(separatorPanel, BorderLayout.NORTH);
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
        return !content.isVisible();
    }

    public void setFolded(boolean folded) {
        separatorPanel.setOpened(!folded);
        doFoldingAnimation(folded);
    }

    private void doFoldingAnimation(final boolean folded) {
        final int maxh = (int) content.getPreferredSize().getHeight();
        final int maxw = (int) content.getPreferredSize().getWidth();

        if (!folded) {
            content.setPreferredSize(new Dimension(maxw, 0));
            content.setVisible(true);
            final int offset = maxh % ANIMATION_STEP_SIZE;
            Thread t = new Thread(new Runnable() {
                public void run() {
                    for (int xx = 0; xx < maxh; xx += ANIMATION_STEP_SIZE) {
                        final int height = xx;

                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                content.setPreferredSize(new Dimension(maxw, height + offset));
                                revalidate();
                            }
                        });
                        try {
                            Thread.sleep(ANIMATION_STEP_SIZE);
                        }
                        catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            t.start();
        }
        else {
            Thread t = new Thread(new Runnable() {
                public void run() {
                    for (int xx = maxh; xx > 0; xx -= ANIMATION_STEP_SIZE) {
                        final int height = xx;

                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                content.setPreferredSize(new Dimension(maxw, height));
                                revalidate();
                                if (height <= ANIMATION_STEP_SIZE) {
                                    content.setVisible(false);
                                    content.setPreferredSize(null);
                                }
                            }
                        });
                        try {
                            Thread.sleep(ANIMATION_STEP_SIZE);
                        }
                        catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            t.start();
        }
    }

    public void setTitle(String title) {
        separatorPanel.setTitle(title);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        fireActionPerformed(e);
    }

    public JComponent getContent() {
        return content;
    }

    public AccordionSeparatorPanel getSeparatorPanel() {
        return separatorPanel;
    }
}
