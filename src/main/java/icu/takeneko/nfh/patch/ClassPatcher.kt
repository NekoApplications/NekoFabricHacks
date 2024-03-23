package icu.takeneko.nfh.patch

import icu.takeneko.nfh.logger
import net.bytebuddy.agent.ByteBuddyAgent
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.tree.ClassNode
import java.io.File
import java.lang.instrument.ClassFileTransformer
import java.lang.instrument.Instrumentation
import java.nio.file.Files
import java.security.ProtectionDomain
import kotlin.io.path.Path

object ClassPatcher {

    private lateinit var instrumentation: Instrumentation
    private val patcherOutputPath = Path(".dumps")

    init {
        try {
            instrumentation = ByteBuddyAgent.install()
        } catch (e: Exception) {
            logger.warn("Failed to install ByteBuddyAgent, some feature will not work", e)
        }
    }

    fun <T> applyPatch(clazz: Class<T>, patch: ClassPatch<T>) {
        applyPatches(clazz, patch)
    }

    fun <T> applyPatches(clazz: Class<T>, vararg patch: ClassPatch<T>){
        val transformer = Transformer(clazz, *patch)
        try{
            instrumentation.addTransformer(transformer, true)
            instrumentation.retransformClasses(clazz)
            instrumentation.removeTransformer(transformer)
        }catch (e:Throwable){
            logger.error("Patch class $clazz with patches ${patch.joinToString(prefix = "[", postfix = "]")}, attempt to revert changes.", e)
            instrumentation.removeTransformer(transformer)
            try{
                instrumentation.retransformClasses(clazz)
            }catch (e:Throwable){
                logger.error("Failed to revert changes for class $clazz")
            }
        }
    }

    class Transformer<T>(private val targetClass: Class<T>,private vararg val patches: ClassPatch<T>) :
        ClassFileTransformer {
        override fun transform(
            loader: ClassLoader,
            className: String,
            classBeingRedefined: Class<*>,
            protectionDomain: ProtectionDomain,
            classFileBuffer: ByteArray
        ): ByteArray {
            if (className.replace("/",".") != targetClass.name) return classFileBuffer
            val node = ClassNode()
            ClassReader(classFileBuffer).accept(node, 0)
            var transformed = false
            patches.forEach {
                transformed = transformed or it.apply(loader, classBeingRedefined, targetClass, classFileBuffer, node)
            }
            return if (transformed) {
                try{
                    val writer = ClassWriter(ClassWriter.COMPUTE_MAXS or ClassWriter.COMPUTE_FRAMES)
                    node.accept(writer)
                    writer.toByteArray().also {
                        val path = patcherOutputPath.resolve("$className.class")
                        Files.createDirectories(path.parent)
                        Files.write(path, it)
                        //println(path)
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                    classFileBuffer
                }
            } else {
                classFileBuffer
            }
        }
    }

}