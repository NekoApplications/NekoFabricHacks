package icu.takeneko.nfh.progress;

import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import org.spongepowered.asm.logging.ILogger;
import org.spongepowered.asm.mixin.transformer.MixinTargetContext;
import org.spongepowered.asm.service.MixinService;

import java.lang.reflect.Field;

public class Progress {
    private static final ILogger logger = MixinService.getService().getLogger("NekoFabricHacks");

    public static void onEntrypointInvoke(String key, EntrypointContainer<?> container) {
        logger.info(
                "Loading {} Entrypoint({}) of Mod {} {}",
                key.substring(0, 1).toUpperCase() + key.substring(1),
                container.getEntrypoint().getClass().getName(),
                container.getProvider().getMetadata().getId(),
                container.getProvider().getMetadata().getVersion()
        );
    }

    public static void onMixinConfigPluginLoad(Object handle) throws Throwable{
        Field f = handle.getClass().getDeclaredField("plugin");
        f.setAccessible(true);
        Object plugin = f.get(handle);
        if (plugin == null)return;
        logger.info("Loading Mixin Config Plugin " + plugin.getClass().getName());
    }

    public static void onSelectingMixinConfig(String name){
        logger.info("Selecting Mixin Config " + name);
    }

    public static void onMixinApply(MixinTargetContext target, String pass){
        logger.info(
                "Applying Mixin [{}(from {}) -> {}] at stage {}",
                target.getMixin().getClassName(),
                target.getMixin().getConfig().getName(),
                target.getClassInfo().getName(),
                pass
        );
    }
}
