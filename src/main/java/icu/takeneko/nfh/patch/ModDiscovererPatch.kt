package icu.takeneko.nfh.patch

import net.fabricmc.loader.impl.discovery.ModDiscoverer
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*

class ModDiscovererPatch : ClassPatch<ModDiscoverer>() {
    override val clazz: Class<ModDiscoverer>
        get() = ModDiscoverer::class.java

    override fun apply(
        loader: ClassLoader,
        classBeingRedefined: Class<*>,
        targetClass: Class<ModDiscoverer>,
        classFileBuffer: ByteArray,
        classNode: ClassNode
    ): Boolean {
        for (methodNode in classNode.methods) {
            if (methodNode.name == "discoverMods" && methodNode.desc == "(Lnet/fabricmc/loader/impl/FabricLoaderImpl;Ljava/util/Map;)Ljava/util/List;") {
                val iter = methodNode.instructions.iterator()
                while (iter.hasNext()) {
                    //INVOKESPECIAL net/fabricmc/loader/impl/discovery/ModDiscoverer.createJavaMod ()Lnet/fabricmc/loader/impl/discovery/ModCandidate;
                    val insn = iter.next()
                    if (insn is MethodInsnNode
                        && insn.name == "createJavaMod"
                        && insn.owner == "net/fabricmc/loader/impl/discovery/ModDiscoverer"
                        && insn.desc == "()Lnet/fabricmc/loader/impl/discovery/ModCandidate;"
                    ) {
                        iter.previous()
                        iter.add(VarInsnNode(Opcodes.ALOAD, 9))
                        iter.add(VarInsnNode(Opcodes.ALOAD, 0))
                        iter.add(FieldInsnNode(Opcodes.GETFIELD, "net/fabricmc/loader/impl/discovery/ModDiscoverer", "versionOverrides", "Lnet/fabricmc/loader/impl/metadata/VersionOverrides;"))
                        iter.add(VarInsnNode(Opcodes.ALOAD, 0))
                        iter.add(FieldInsnNode(Opcodes.GETFIELD, "net/fabricmc/loader/impl/discovery/ModDiscoverer", "depOverrides", "Lnet/fabricmc/loader/impl/metadata/DependencyOverrides;"))
                        iter.add(MethodInsnNode(Opcodes.INVOKESTATIC, "icu/takeneko/nfh/reload/Reload","fixFabricLoaderMod","(Ljava/util/List;Lnet/fabricmc/loader/impl/metadata/VersionOverrides;Lnet/fabricmc/loader/impl/metadata/DependencyOverrides;)V"))
//                        iter.add(MethodInsnNode(Opcodes.INVOKESTATIC, "icu/takeneko/nfh/reload/Reload","checkFabricLoaderExisis","(Ljava/util/List;)Z"))
//                        val label1 = LabelNode()
//                        iter.add(JumpInsnNode(Opcodes.IFEQ, label1))
//
//                        iter.add(VarInsnNode(Opcodes.ALOAD, 9))
//
//                        iter.add(MethodInsnNode(Opcodes.INVOKESTATIC, "java/util/Collections", "emptyList", "()Ljava/util/List;"))
//
//                        iter.add(TypeInsnNode(Opcodes.NEW, "net/fabricmc/loader/impl/metadata/BuiltinModMetadata\$Builder"))
//                        iter.add(InsnNode(Opcodes.DUP))
//                        iter.add(LdcInsnNode("fabricloader"))
//                        iter.add(FieldInsnNode(Opcodes.GETFIELD, "net/fabricmc/loader/impl/FabricLoaderImpl", "VERSION", "Ljava/lang/String"))
//                        iter.add(MethodInsnNode(Opcodes.INVOKESPECIAL, "net/fabricmc/loader/impl/metadata/BuiltinModMetadata\$Builder", "<init>", "(Ljava/lang/String;Ljava/lang/String;)V"))
//                        iter.add(LdcInsnNode("Fabric Loader"))
//                        iter.add(MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/fabricmc/loader/impl/metadata/BuiltinModMetadata\$Builder","setName", "(Ljava/lang/String;)V"))
//                        iter.add(MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/fabricmc/loader/impl/metadata/BuiltinModMetadata\$Builder","build", "()Lnet/fabricmc/loader/api/metadata/ModMetadata;"))
//
//                        iter.add(TypeInsnNode(Opcodes.NEW, "net/fabricmc/loader/impl/game/GameProvider\$BuiltinMod"))
//                        iter.add(InsnNode(Opcodes.DUP))
//                        iter.add(MethodInsnNode(Opcodes.INVOKESPECIAL, "net/fabricmc/loader/impl/game/GameProvider\$BuiltinMod", "<init>", "(Ljava/util/List;Lnet/fabricmc/loader/api/metadata/ModMetadata;)V"))
//
//                        iter.add(VarInsnNode(Opcodes.ALOAD, 0))
//                        iter.add(FieldInsnNode(Opcodes.GETFIELD, "net/fabricmc/loader/impl/discovery/ModDiscoverer", "versionOverrides", "Lnet/fabricmc/loader/impl/metadata/VersionOverrides;"))
//                        iter.add(VarInsnNode(Opcodes.ALOAD, 0))
//                        iter.add(FieldInsnNode(Opcodes.GETFIELD, "net/fabricmc/loader/impl/discovery/ModDiscoverer", "depOverrides", "Lnet/fabricmc/loader/impl/metadata/DependencyOverrides;"))
//
//                        iter.add(MethodInsnNode(Opcodes.INVOKESTATIC, "net/fabricmc/loader/impl/discovery/ModCandidate","createBuiltin","Lnet/fabricmc/loader/impl/game/GameProvider\$BuiltinMod;Lnet/fabricmc/loader/impl/metadata/VersionOverrides;Lnet/fabricmc/loader/impl/metadata/DependencyOverrides;)Lnet/fabricmc/loader/impl/discovery/ModCandidate;"))
//
//                        iter.add(MethodInsnNode(Opcodes.INVOKEINTERFACE, "java/util/Collection", "add", "(Ljava/lang/Object;)Z"))
//                        iter.add(label1)
                        return true
                    }
                }
            }
        }
        return false
    }
}