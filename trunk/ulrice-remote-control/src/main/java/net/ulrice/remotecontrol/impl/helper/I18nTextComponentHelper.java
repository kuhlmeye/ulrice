package net.ulrice.remotecontrol.impl.helper;

import java.awt.Robot;
import java.util.Locale;
import java.util.Map;

import javax.swing.text.JTextComponent;

import net.ulrice.remotecontrol.RemoteControlException;
import net.ulrice.remotecontrol.util.RemoteControlUtils;
import net.ulrice.ui.components.I18nTextComponent;

public class I18nTextComponentHelper extends AbstractJComponentHelper<I18nTextComponent> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<I18nTextComponent> getType() {
        return I18nTextComponent.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean enter(Robot robot, final I18nTextComponent component, final String text)
        throws RemoteControlException {

        try {
            RemoteControlUtils.invokeInSwing(new Runnable() {

                @Override
                public void run() {
                    Map<Locale, String> map = component.getData();
                    map.put(component.getSelectedLocale(), text);
                }
            });
        }
        catch (RemoteControlException e) {
            throw new RemoteControlException(String.format("Failed to enter %s into the I18n text component", text), e);
        }

        return true;
    }

    @Override
    public String getText(I18nTextComponent component) throws RemoteControlException {
        return component.getData().get(component.getSelectedLocale());
    }

}
