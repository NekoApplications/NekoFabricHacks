package icu.takeneko.nfh;

import net.fabricmc.loader.api.LanguageAdapter;
import net.fabricmc.loader.api.LanguageAdapterException;
import net.fabricmc.loader.api.ModContainer;

public class EarlyLaunchLanguageAdapter implements LanguageAdapter {

    static {
        try {
            Early.launch();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T create(ModContainer mod, String value, Class<T> type) throws LanguageAdapterException {
        throw new RuntimeException("this should not happen");
    }
}
