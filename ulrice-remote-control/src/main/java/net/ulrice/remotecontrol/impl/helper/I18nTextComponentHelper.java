package net.ulrice.remotecontrol.impl.helper;

import net.ulrice.remotecontrol.RemoteControlException;
import net.ulrice.remotecontrol.util.RemoteControlUtils;
import net.ulrice.remotecontrol.util.Result;
import net.ulrice.ui.components.I18nTextComponent;
import net.ulrice.ui.components.LocaleSelector;

import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.Locale;

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
    public boolean enter(final Robot robot, final I18nTextComponent component, final String text)
        throws RemoteControlException {

        final Result<Boolean> result = new Result<Boolean>(2);
        final JTextComponent editor = component.getTextComponent();
        final ComponentHelper<Component> helper = ComponentHelperRegistry.get(editor.getClass());

        try {
            RemoteControlUtils.invokeInSwing(new Runnable() {
                @Override
                public void run() {
                    LocaleSelector selector = component.getLocaleSelector();

                    for (Locale locale : component.getAvailableLocales()) {
                        selector.setSelectedLocale(locale);
                        try {
                           result.fireResult(helper.enter(robot, editor, text));
                        }
                        catch (RemoteControlException e) {
                            result.fireException(e);
                        }
                    }
                }
            });

            return result.aquireResult();
        }
        catch (RemoteControlException e) {
            throw new RemoteControlException(String.format("Failed to enter %s into the I18n text component", text), e);
        }
    }

    @Override
    public String getText(I18nTextComponent component) throws RemoteControlException {
        return component.getData().get(component.getSelectedLocale());
    }

}
