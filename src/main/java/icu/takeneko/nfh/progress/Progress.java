package icu.takeneko.nfh.progress;

import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.transformer.MixinTargetContext;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;

public class Progress {
    private static VarHandle H_PluginHandle_plugin;

    public static void init() {
        if (H_PluginHandle_plugin != null) return;
        try {
            Class<?> pluginHandleClass = Class.forName("org.spongepowered.asm.mixin.transformer.PluginHandle");
            Field f = pluginHandleClass.getDeclaredField("plugin");
            f.setAccessible(true);
            H_PluginHandle_plugin = MethodHandles.lookup().unreflectVarHandle(f);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public static void onEntrypointInvoke(String key, EntrypointContainer<?> container) {
        Log.info(
            LogCategory.ENTRYPOINT,
            "Loading %s Entrypoint(%s) of Mod %s %s",
            key.substring(0, 1).toUpperCase() + key.substring(1),
            container.getEntrypoint().getClass().getName(),
            container.getProvider().getMetadata().getId(),
            container.getProvider().getMetadata().getVersion()
        );
    }

    public static void onMixinConfigPluginLoad(Object handle) {
        init();
        IMixinConfigPlugin plugin = (IMixinConfigPlugin) H_PluginHandle_plugin.get(handle);
        if (plugin == null) return;
        Log.info(LogCategory.MIXIN, "Loading Mixin Config Plugin " + plugin.getClass().getName());
    }

    public static void onSelectingMixinConfig(String name) {
        Log.info(LogCategory.MIXIN, "Selecting Mixin Config " + name);
    }

    public static void onMixinApply(MixinTargetContext target, String pass, int injectorOrder) {
        if (injectorOrder != -1) {
            Log.info(
                LogCategory.MIXIN,
                "Applying Mixin [%s(from %s) -> %s] at stage %s (InjectorOrder: %d)",
                target.getMixin().getClassName(),
                target.getMixin().getConfig().getName(),
                target.getTargetClassInfo().getName(),
                pass,
                injectorOrder
            );
        } else {
            Log.info(
                LogCategory.MIXIN,
                "Applying Mixin [%s(from %s) -> %s] at stage %s",
                target.getMixin().getClassName(),
                target.getMixin().getConfig().getName(),
                target.getTargetClassInfo().getName(),
                pass
            );
        }
    }
}
