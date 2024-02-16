package icu.takeneko.nfh.progress

import net.fabricmc.loader.api.entrypoint.EntrypointContainer
import org.spongepowered.asm.service.MixinService
import java.lang.reflect.Proxy

object Progress {

    private val logger = MixinService.getService().getLogger("NekoFabricHacks")

    @JvmStatic
    fun onEntrypointInvoke(key: String, container: EntrypointContainer<*>) {
        logger.info(
            "Loading ${describeKey(key)} Entrypoint(${container.entrypoint.javaClass.name}) of Mod ${container.provider.metadata.id} ${container.provider.metadata.version}"
        )
    }

    @JvmStatic
    fun onMixinConfigPluginLoad(handle:Object){
        val plugin = handle.javaClass.getDeclaredField("plugin").apply { isAccessible = true }.get(handle)
        if (plugin == null)return
        logger.info("Loading Mixin Config Plugin ${plugin.javaClass.name}")
    }

    @JvmStatic
    fun onSelectingMixinConfig(name: String){
        logger.info("Selecting Mixin Config $name")
    }

    private fun describeKey(key: String): String {
        return key.replaceFirstChar { it.uppercaseChar() }
    }
}

