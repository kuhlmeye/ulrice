package net.ulrice.ui.accordionpanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import net.ulrice.ui.components.HorizontalScrollPane;

public class AccordionContentPanel extends JPanel implements ActionListener {

    // private static final int ANIMATION_STEPS_DIVISOR = 20;
    // private static final int ANIMATION_STEP_PAUSE = 5;

    private static final long serialVersionUID = 5734171488424370652L;

    private final AccordionSeparatorPanel separatorPanel;
    private final HorizontalScrollPane scrollPane;
    private final Component content;

    private String actionCommand;

    public AccordionContentPanel(String title, Component content, Color seperatorBackgroundColor) {
        super(new BorderLayout());

        separatorPanel = new AccordionSeparatorPanel(title, seperatorBackgroundColor);
        separatorPanel.addActionListener(this);

        this.content = content;

        scrollPane = new HorizontalScrollPane(content);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        add(separatorPanel, BorderLayout.NORTH);
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
        return !scrollPane.isVisible();
    }

    public void setInitialFolded(boolean folded) {
        separatorPanel.setOpened(!folded);
        scrollPane.setVisible(!folded);
    }

    public void setFolded(boolean folded) {
        separatorPanel.setOpened(!folded);
        scrollPane.setVisible(!folded);
        // doFoldingAnimation(folded);
    }

    // private void doFoldingAnimation(final boolean folded) {
    // Dimension preferredSize = scrollPane.getExpectedPreferredSize();
    // final int maxh = preferredSize.height;
    // final int maxw = preferredSize.width;
    //
    // final int stepSize = maxh / ANIMATION_STEPS_DIVISOR;
    //
    // if (!folded) {
    // content.setPreferredSize(new Dimension(maxw, 0));
    // content.setVisible(true);
    // final int offset = maxh % stepSize;
    // Thread t = new Thread(new Runnable() {
    // @Override
    // public void run() {
    // for (int xx = 0; xx < maxh; xx += stepSize) {
    // final int height = xx;
    //
    // SwingUtilities.invokeLater(new Runnable() {
    // @Override
    // public void run() {
    // System.out.println(maxw + ", " + (height + offset));
    // content.setPreferredSize(new Dimension(maxw, height + offset));
    // revalidate();
    // }
    // });
    // try {
    // Thread.sleep(ANIMATION_STEP_PAUSE);
    // }
    // catch (InterruptedException e) {
    // e.printStackTrace();
    // }
    // }
    // }
    // });
    // t.start();
    // }
    // else {
    // Thread t = new Thread(new Runnable() {
    // @Override
    // public void run() {
    // for (int xx = maxh; xx > 0; xx -= stepSize) {
    // final int height = xx;
    //
    // SwingUtilities.invokeLater(new Runnable() {
    // @Override
    // public void run() {
    // content.setPreferredSize(new Dimension(maxw, height));
    // revalidate();
    // if (height <= stepSize) {
    // content.setVisible(false);
    // content.setPreferredSize(null);
    // }
    // }
    // });
    // try {
    // Thread.sleep(ANIMATION_STEP_PAUSE);
    // }
    // catch (InterruptedException e) {
    // e.printStackTrace();
    // }
    // }
    // }
    // });
    // t.start();
    // }
    // }

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
