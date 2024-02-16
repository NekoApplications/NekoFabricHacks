package icu.takeneko.nfh.mixin

import icu.takeneko.nfh.patch.ClassPatcher
import icu.takeneko.nfh.patch.FabricLoaderImplPatch
import icu.takeneko.nfh.patch.MixinConfigPatch
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.impl.FabricLoaderImpl
import org.objectweb.asm.tree.ClassNode
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin
import org.spongepowered.asm.mixin.extensibility.IMixinInfo
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

class MixinConfigPlugin:IMixinConfigPlugin {
    override fun onLoad(mixinPackage: String?) {
        defineClass("icu.takeneko.nfh.ModKt")
        defineClass("icu.takeneko.nfh.progress.Progress")
        ClassPatcher.applyPatch(FabricLoaderImpl::class.java, FabricLoaderImplPatch())
        ClassPatcher.applyPatch<Object>(Class.forName("org.spongepowered.asm.mixin.transformer.MixinConfig") as Class<Object>, MixinConfigPatch())
    }

    override fun getRefMapperConfig(): String? {
        return null
    }

    override fun shouldApplyMixin(targetClassName: String, mixinClassName: String): Boolean {
        return true
    }

    override fun acceptTargets(myTargets: MutableSet<String>, otherTargets: MutableSet<String>) {
        
    }

    override fun getMixins(): List<String> {
        return emptyList()
    }

    override fun preApply(
        targetClassName: String,
        targetClass: ClassNode,
        mixinClassName: String,
        mixinInfo: IMixinInfo
    ) {
        
    }

    override fun postApply(
        targetClassName: String,
        targetClass: ClassNode,
        mixinClassName: String,
        mixinInfo: IMixinInfo
    ) {
        
    }


    private fun defineClass(name: String) {
        if (isAlreadyLoaded(name)) {
            return
        }
        val defineClass = ClassLoader::class.java.getDeclaredMethod(
            "defineClass",
            String::class.java,
            ByteArray::class.java,
            Int::class.javaPrimitiveType,
            Int::class.javaPrimitiveType
        )
        defineClass.isAccessible = true
        defineClass.invoke(
            FabricLoader::class.java.classLoader,
            name,
            getClassFile(name),
            0,
            getClassFile(name).size
        )
    }

    private fun isAlreadyLoaded(name: String): Boolean {
        try {
            Class.forName(name, false, FabricLoader::class.java.classLoader)
            return true
        } catch (e: ClassNotFoundException) {
            return false
        }
    }

    private fun getClassFile(name: String): ByteArray {
        this::class.java.getClassLoader()
            .getResourceAsStream(name.replace('.', '/') + ".class").use {
                return it.readAllBytes()
            }
    }
}