package icu.takeneko.nfh.reload

import net.fabricmc.loader.impl.FabricLoaderImpl
import net.fabricmc.loader.impl.discovery.ModCandidate
import net.fabricmc.loader.impl.game.GameProvider.BuiltinMod
import net.fabricmc.loader.impl.metadata.BuiltinModMetadata
import net.fabricmc.loader.impl.metadata.DependencyOverrides
import net.fabricmc.loader.impl.metadata.VersionOverrides

object Reload {

    val modCandidate = mutableListOf<ModCandidate>()
    val modCandidateHashes = mutableMapOf<String, ByteArray>()

    fun reload() {
        FabricLoaderAccess.unfreeze()
        FabricLoaderImpl.INSTANCE.load()
        FabricLoaderImpl.INSTANCE.freeze()
    }

    @JvmStatic
    fun checkFabricLoaderExisis(candidates: java.util.List<ModCandidate>): Boolean {
        return !candidates.any { it.id == "fabricloader" || it.id == "fabric-loader" }
    }

    @JvmStatic
    fun fixFabricLoaderMod(candidates: java.util.List<ModCandidate>, versionOverrides: VersionOverrides, depOverrides: DependencyOverrides) {
        if(!candidates.any { it.id == "fabricloader" || it.id == "fabric-loader" }){
            val metadata = BuiltinModMetadata.Builder("fabricloader", FabricLoaderImpl.VERSION).setName("Fabric Loader").build()
            val builtinMod = BuiltinMod(emptyList(), metadata)
            val candidate = ModCandidate::class.java
                .getDeclaredMethod("createBuiltin",BuiltinMod::class.java, VersionOverrides::class.java, DependencyOverrides::class.java)
                .apply { isAccessible = true }
                .invoke(null, builtinMod, versionOverrides, depOverrides) as ModCandidate
            candidates.add(candidate)
        }
    }

    @JvmStatic
    fun copyModCandidates(candidates: java.util.List<ModCandidate>?){
        this.modCandidate.clear()
        for (candidate in candidates ?: return) {
            modCandidate.add(candidate)
        }
    }

    @JvmStatic
    fun fixRemap(candidates: java.util.List<ModCandidate>){
        for (candidate in candidates) {
            if (candidate in modCandidate){
                ModCandidate::class.java.getDeclaredField("requiresRemap").apply { isAccessible = true }.set(candidate, false)
            }
        }
    }


}