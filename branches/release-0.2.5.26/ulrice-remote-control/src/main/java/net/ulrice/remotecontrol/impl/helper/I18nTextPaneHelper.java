package net.ulrice.remotecontrol.impl.helper;

import java.awt.Robot;
import java.util.Locale;
import java.util.Map;

import net.ulrice.remotecontrol.RemoteControlException;
import net.ulrice.remotecontrol.util.RemoteControlUtils;
import net.ulrice.ui.components.I18nTextPane;

public class I18nTextPaneHelper extends AbstractJComponentHelper<I18nTextPane> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<I18nTextPane> getType() {
        return I18nTextPane.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean enter(Robot robot, final I18nTextPane component, final String text)
        throws RemoteControlException {

        try {
            RemoteControlUtils.invokeInSwing(new Runnable() {
                @Override
                public void run() {
                    component.getTextPane().setText(text);
                    Map<Locale, String> map = component.getData();
                    map.put(component.getSelectedLocale(), text);
                }
            });
        }
        catch (RemoteControlException e) {
            throw new RemoteControlException(String.format("Failed to enter %s into the I18n text pane", text), e);
        }
        return true;
    }

    @Override
    public String getText(I18nTextPane component) throws RemoteControlException {
        return component.getData().get(component.getSelectedLocale());
    }
}
