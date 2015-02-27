package net.ulrice.remotecontrol.impl.helper;

import javax.swing.JPanel;

import net.ulrice.remotecontrol.RemoteControlException;
import net.ulrice.remotecontrol.util.ComponentUtils;

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
        return ComponentUtils.getTitle(panel);
    }

}
