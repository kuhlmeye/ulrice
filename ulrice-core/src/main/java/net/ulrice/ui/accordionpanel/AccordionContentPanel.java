package net.ulrice.ui.accordionpanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JPanel;

public class AccordionContentPanel extends JPanel implements ActionListener {

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
                e =
                        new ActionEvent(this, ActionEvent.ACTION_PERFORMED, getActionCommand(), event.getWhen(),
                            event.getModifiers());
            }
            ((ActionListener) listeners[i + 1]).actionPerformed(e);
        }
    }

    public boolean isFolded() {
        return !content.isVisible();
    }

    public void setFolded(boolean folded) {
        separatorPanel.setOpened(!folded);
        content.setVisible(!folded);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        fireActionPerformed(e);
    }
    
    public JComponent getContent() {
		return content;
	}
}
