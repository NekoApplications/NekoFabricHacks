package icu.takeneko.nfh.patch

import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.FieldInsnNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.VarInsnNode

class MixinConfigPatch : ClassPatch<Object>() {
    override val clazz: Class<Object>
        get() = Class.forName("org.spongepowered.asm.mixin.transformer.MixinConfig") as Class<Object>

    override fun apply(
        loader: ClassLoader,
        classBeingRedefined: Class<*>,
        targetClass: Class<Object>,
        classFileBuffer: ByteArray,
        classNode: ClassNode
    ): Boolean {
        if (targetClass != clazz) return false
        for (method in classNode.methods) {
            if (method.name == "onSelect" && method.desc == "()V") {
                val iter = method.instructions.iterator()
                while (iter.hasNext()) {
                    val insn = iter.next()
                    //INVOKEVIRTUAL org/spongepowered/asm/mixin/transformer/PluginHandle.onLoad (Ljava/lang/String;)V
                    if (insn is MethodInsnNode
                        && insn.opcode == Opcodes.INVOKEVIRTUAL
                        && insn.owner == "org/spongepowered/asm/mixin/transformer/PluginHandle"
                        && insn.name == "onLoad"
                        && insn.desc == "(Ljava/lang/String;)V"
                    ) {
                        repeat(5) { iter.previous() }
                        // ALOAD 0
                        // GETFIELD org/spongepowered/asm/mixin/transformer/MixinConfig.plugin : Lorg/spongepowered/asm/mixin/transformer/PluginHandle;
                        iter.add(VarInsnNode(Opcodes.ALOAD, 0))
                        iter.add(
                            FieldInsnNode(
                                Opcodes.GETFIELD,
                                "org/spongepowered/asm/mixin/transformer/MixinConfig",
                                "name",
                                "Ljava/lang/String;"
                            )
                        )
                        iter.add(
                            MethodInsnNode(
                                Opcodes.INVOKESTATIC,
                                "icu/takeneko/nfh/progress/Progress",
                                "onSelectingMixinConfig",
                                "(Ljava/lang/String;)V"
                            )
                        )
                        iter.add(VarInsnNode(Opcodes.ALOAD, 0))
                        iter.add(
                            FieldInsnNode(
                                Opcodes.GETFIELD,
                                "org/spongepowered/asm/mixin/transformer/MixinConfig",
                                "plugin",
                                "Lorg/spongepowered/asm/mixin/transformer/PluginHandle;"
                            )
                        )
                        iter.add(
                            MethodInsnNode(
                                Opcodes.INVOKESTATIC,
                                "icu/takeneko/nfh/progress/Progress",
                                "onMixinConfigPluginLoad",
                                "(Ljava/lang/Object;)V"
                            )
                        )
                        return true
                    }
                }
            }
        }
        return false
    }
}