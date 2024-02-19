package icu.takeneko.nfh.reload;

import net.fabricmc.loader.impl.FabricLoaderImpl;

import java.lang.reflect.Field;

public class FabricLoaderAccess {

    private final static Class<FabricLoaderImpl> fabricLoaderImpl = FabricLoaderImpl.class;

    private final static Field frozen;

    static {
        try {
            frozen = fabricLoaderImpl.getDeclaredField("frozen");
            frozen.setAccessible(true);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void unfreeze() throws Throwable {
        frozen.set(FabricLoaderImpl.INSTANCE, false);
    }
}
