package icu.takeneko.nfh.patch

import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.VarInsnNode


class MixinApplicatorStandardPatch : ClassPatch<Object>() {
    override val clazz: Class<Object>
        get() = Class.forName("org.spongepowered.asm.mixin.transformer.MixinApplicatorStandard") as Class<Object>

    override fun apply(
        loader: ClassLoader,
        classBeingRedefined: Class<*>,
        targetClass: Class<Object>,
        classFileBuffer: ByteArray,
        classNode: ClassNode
    ): Boolean {
        if (targetClass != clazz) return false
        for (method in classNode.methods) {
            if (method.name == "applyMixin" && method.desc == "(Lorg/spongepowered/asm/mixin/transformer/MixinTargetContext;Lorg/spongepowered/asm/mixin/transformer/MixinApplicatorStandard\$ApplicatorPass;)V") {
                val iter = method.instructions.iterator()
                iter.add(VarInsnNode(Opcodes.ALOAD, 1))
                iter.add(VarInsnNode(Opcodes.ALOAD, 2))
                iter.add(MethodInsnNode(
                    Opcodes.INVOKEVIRTUAL,
                    "org/spongepowered/asm/mixin/transformer/MixinApplicatorStandard\$ApplicatorPass",
                    "toString",
                    "()Ljava/lang/String;"
                ))
                iter.add(MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    "icu/takeneko/nfh/progress/Progress",
                    "onMixinApply",
                    "(Lorg/spongepowered/asm/mixin/transformer/MixinTargetContext;Ljava/lang/String;)V"
                ))
                return true
            }
        }
        return false
    }
}