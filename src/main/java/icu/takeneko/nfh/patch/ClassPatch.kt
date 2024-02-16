package icu.takeneko.nfh.patch

import org.objectweb.asm.tree.ClassNode

abstract class ClassPatch<T> {

    abstract val clazz:Class<T>

    abstract fun apply(
        loader: ClassLoader,
        classBeingRedefined: Class<*>,
        targetClass: Class<T>,
        classFileBuffer: ByteArray,
        classNode: ClassNode
    ):Boolean

    override fun toString(): String {
        return "ClassPatch of Class $clazz"
    }
}