package net.ulrice.databinding.bufferedbinding;

import net.ulrice.message.EmptyTranslationProvider;
import net.ulrice.message.ModuleTranslationProvider;

public class DefaultTableAMBuilderCallback implements IFTableAMBuilderCallback {

    @Override
    public ModuleTranslationProvider getTranslationProvider() {
        return new EmptyTranslationProvider();
    }

}
