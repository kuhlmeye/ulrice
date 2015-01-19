package net.ulrice.remotecontrol.impl.helper;

import net.ulrice.remotecontrol.RemoteControlException;
import net.ulrice.remotecontrol.util.RemoteControlUtils;
import net.ulrice.remotecontrol.util.Result;
import net.ulrice.ui.components.I18nTextPane;
import net.ulrice.ui.components.LocaleSelector;

import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.Locale;
import java.util.Map;

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
    public boolean enter(final Robot robot, final I18nTextPane component, final String text) throws RemoteControlException {

        final Result<Boolean> result = new Result<Boolean>(1);
        final JTextComponent editor = component.getTextPane();
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
            throw new RemoteControlException(String.format("Failed to enter %s into the I18n text pane", text), e);
        }
    }

    @Override
    public String getText(I18nTextPane component) throws RemoteControlException {
        return component.getData().get(component.getSelectedLocale());
    }
}
