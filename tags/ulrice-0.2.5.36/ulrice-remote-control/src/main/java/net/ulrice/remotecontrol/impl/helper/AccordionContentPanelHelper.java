package net.ulrice.remotecontrol.impl.helper;

import net.ulrice.remotecontrol.RemoteControlException;
import net.ulrice.ui.accordionpanel.AccordionContentPanel;

public class AccordionContentPanelHelper extends AbstractJComponentHelper<AccordionContentPanel> {

    @Override
    public Class<AccordionContentPanel> getType() {
        return AccordionContentPanel.class;
    }

    @Override
    public String getTitle(AccordionContentPanel contentPanel) throws RemoteControlException {
        return contentPanel.getSeparatorPanel().getTitle();
    }

}
