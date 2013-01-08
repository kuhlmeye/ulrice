package net.ulrice.remotecontrol.impl.helper;

import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import net.ulrice.remotecontrol.RemoteControlException;

public class JPanelHelper extends AbstractJComponentHelper<JPanel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<JPanel> getType() {
        return JPanel.class;
    }

    @Override
    public String getTitle(JPanel panel) throws RemoteControlException {
        Border border = panel.getBorder();
        if (border instanceof TitledBorder) {
            return ((TitledBorder) border).getTitle();
        }
        return null;
    }

}
