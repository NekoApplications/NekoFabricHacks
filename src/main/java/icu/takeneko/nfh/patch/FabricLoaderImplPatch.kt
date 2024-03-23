package icu.takeneko.nfh.patch

import net.fabricmc.loader.impl.FabricLoaderImpl
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*

class FabricLoaderImplPatch : ClassPatch<FabricLoaderImpl>() {
    override val clazz: Class<FabricLoaderImpl>
        get() = FabricLoaderImpl::class.java

    override fun apply(
        loader: ClassLoader,
        classBeingRedefined: Class<*>,
        targetClass: Class<FabricLoaderImpl>,
        classFileBuffer: ByteArray,
        classNode: ClassNode
    ): Boolean {
        var result = false
        for (methodNode in classNode.methods) {
            if (methodNode.name == "invokeEntrypoints" && methodNode.desc == "(Ljava/lang/String;Ljava/lang/Class;Ljava/util/function/Consumer;)V") {
                val iterator = methodNode.instructions.iterator()
                while (iterator.hasNext()) {
                    val insn = iterator.next()
                    if (insn is MethodInsnNode
                        && insn.opcode == Opcodes.INVOKEINTERFACE
                        && insn.name == "getEntrypoint"
                        && insn.desc == "()Ljava/lang/Object;"
                        && insn.owner == "net/fabricmc/loader/api/entrypoint/EntrypointContainer"
                    ) {
                        iterator.previous()
                        iterator.previous()
                        iterator.add(VarInsnNode(Opcodes.ALOAD, 1))
                        iterator.add(VarInsnNode(Opcodes.ALOAD, 7))
                        iterator.add(
                            MethodInsnNode(
                                Opcodes.INVOKESTATIC,
                                "icu/takeneko/nfh/progress/Progress",
                                "onEntrypointInvoke",
                                "(Ljava/lang/String;Lnet/fabricmc/loader/api/entrypoint/EntrypointContainer;)V",
                            )
                        )
                        result = true
                        break
                    }
                }
            }
        }
        return result
    }
}