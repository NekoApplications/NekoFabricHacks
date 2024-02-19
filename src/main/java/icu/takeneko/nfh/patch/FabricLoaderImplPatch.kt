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
            if (methodNode.name == "setup" && methodNode.desc == "()V") {
                var iterator = methodNode.instructions.iterator()
                iterator.add(VarInsnNode(Opcodes.ALOAD, 0))
                iterator.add(
                    FieldInsnNode(
                        Opcodes.GETFIELD,
                        "net/fabricmc/loader/impl/FabricLoaderImpl",
                        "modCandidates",
                        "Ljava/util/List;"
                    )
                )
                iterator.add(
                    MethodInsnNode(
                        Opcodes.INVOKESTATIC,
                        "icu/takeneko/nfh/reload/Reload",
                        "copyModCandidates",
                        "(Ljava/util/List;)V"
                    )
                )
                while (iterator.hasNext()) {
                    val insn = iterator.next()
                    //INVOKESPECIAL net/fabricmc/loader/impl/FabricLoaderImpl.dumpModList (Ljava/util/List;)V
//                    if (insn is MethodInsnNode && insn.desc == "(Ljava/util/List;)V" && insn.owner == "net/fabricmc/loader/impl/FabricLoaderImpl" && insn.name == "dumpModList"){
//                        repeat(3){ iterator.previous() }
//                    }
                    if (insn is VarInsnNode && insn.opcode == Opcodes.ISTORE && insn.`var` == 1) {
                        iterator.add(InsnNode(Opcodes.ICONST_0))
                        iterator.add(VarInsnNode(Opcodes.ISTORE, 1))
                        result = true
                        break
                    }
                }
                //TODO: clone modCandidates
                iterator = methodNode.instructions.iterator()
                while (iterator.hasNext()) {
                    val insn = iterator.next()
                    //    ALOAD 0
                    //    ACONST_NULL
                    //    PUTFIELD net/fabricmc/loader/impl/FabricLoaderImpl.modCandidates : Ljava/util/List;
                    if (insn is InsnNode && insn.opcode == Opcodes.ACONST_NULL) {
                        val insn1 = iterator.next()
                        iterator.previous()
                        iterator.previous()
                        if (insn1 is FieldInsnNode && insn1.owner == "net/fabricmc/loader/impl/FabricLoaderImpl" && insn1.desc == "Ljava/util/List;" && insn1.name == "modCandidates"){
                            iterator.add(InsnNode(Opcodes.RETURN))
                            break
                        }else {
                            continue
                        }
                    }
                }
            }
            if (methodNode.name == "setupLanguageAdapters" && methodNode.desc == "()V"){
                val iter = methodNode.instructions.iterator()
                //    LDC "Duplicate language adapter key: "

                while (iter.hasNext()){
                    val insn = iter.next()
                    if (insn is LdcInsnNode && insn.cst == "Duplicate language adapter key: "){
                        while (iter.hasNext()){
                            val insn1 = iter.next()
                            if (insn1 is InsnNode && insn1.opcode == Opcodes.ATHROW){
                                iter.remove()
                                iter.add(InsnNode(Opcodes.POP))
                                break
                            }
                        }
                        break
                    }
                }
                return true
            }
        }
        return result
    }
}