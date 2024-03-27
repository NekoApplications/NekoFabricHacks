package icu.takeneko.nfh;

import icu.takeneko.nfh.patch.ClassPatcher;
import icu.takeneko.nfh.patch.FabricLoaderImplPatch;
import icu.takeneko.nfh.patch.MixinApplicatorStandardPatch;
import icu.takeneko.nfh.patch.MixinConfigPatch;
import icu.takeneko.nfh.progress.Progress;
import kotlin.jvm.internal.Intrinsics;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.fabricmc.loader.impl.game.minecraft.MinecraftGameProvider;
import org.spongepowered.asm.service.MixinService;

import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Early {
    public static void launch() throws Throwable {
        var dumps = Path.of("./.dumps");
        if (dumps.toFile().exists()) {
            final Iterator<Path> iterator = Files.walk(dumps).sorted(Comparator.reverseOrder()).iterator();
            while (iterator.hasNext()) {
                Files.delete(iterator.next());
            }
        }
        final ClassLoader classLoader = Early.class.getClassLoader();
        final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        unlockLibraryOnKnot(contextClassLoader);
//        run(ClassLoader.getSystemClassLoader());
        if (contextClassLoader.getClass().isInstance(classLoader)) {
            final Instrumentation inst = ByteBuddyAgent.install();
            inst.redefineModule(
                    ModuleLayer.boot().findModule("java.base").get(),
                    Set.of(),
                    Map.of(),
                    Map.of("java.lang", Set.of(Early.class.getModule())),
                    Set.of(),
                    Map.of()
            );
        }
        run();
    }

    private static void run() throws Throwable {
        defineClass(MixinService.class.getClassLoader(), "icu.takeneko.nfh.ModKt");
        defineClass(MixinService.class.getClassLoader(), "icu.takeneko.nfh.progress.Progress");
        defineClass(MixinService.class.getClassLoader(), "icu.takeneko.nfh.patch.ClassPatcher");
        ClassPatcher.INSTANCE.applyPatch(FabricLoaderImpl.class, new FabricLoaderImplPatch());
        ClassPatcher.INSTANCE.applyPatch((Class<Object>) Class.forName("org.spongepowered.asm.mixin.transformer.MixinConfig"), new MixinConfigPatch());
        ClassPatcher.INSTANCE.applyPatch((Class<Object>) Class.forName("org.spongepowered.asm.mixin.transformer.MixinApplicatorStandard"), new MixinApplicatorStandardPatch());
    }

    public static Class<?> defineClass(String name) throws IllegalAccessException, InvocationTargetException, IOException, NoSuchMethodException {
        return defineClass(FabricLoader.class.getClassLoader(), name);
    }


    public static Class<?> defineClass(ClassLoader classLoader, String name) throws IllegalAccessException, InvocationTargetException, IOException, NoSuchMethodException {
        final Method defineClass = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);
        defineClass.setAccessible(true);
        return (Class<?>) defineClass.invoke(
                classLoader,
                name,
                getClassFile(name),
                0,
                getClassFile(name).length
        );
    }

    private static byte[] getClassFile(String name) throws IOException {
        try (InputStream in = Early.class.getClassLoader().getResourceAsStream(name.replace('.', '/') + ".class")) {
            return in.readAllBytes();
        }
    }

    //com.ishland.earlyloadingscreen.EarlyLaunch
    private static void unlockLibraryOnKnot(ClassLoader knotClassLoader) {
        try {
            final Method getDelegate = knotClassLoader.getClass().getDeclaredMethod("getDelegate");
            getDelegate.setAccessible(true);
            final Object knotClassLoaderDelegate = getDelegate.invoke(knotClassLoader);
            final Class<?> delegateClazz = Class.forName("net.fabricmc.loader.impl.launch.knot.KnotClassDelegate");
            final MinecraftGameProvider gameProvider = (MinecraftGameProvider) FabricLoaderImpl.INSTANCE.getGameProvider();
            final Field getMiscGameLibraries = MinecraftGameProvider.class.getDeclaredField("miscGameLibraries");
            getMiscGameLibraries.setAccessible(true);
            List<Path> miscGameLibraries = (List<Path>) getMiscGameLibraries.get(gameProvider);
            final Method setAllowedPrefixes = delegateClazz.getDeclaredMethod("setAllowedPrefixes", Path.class, String[].class);
            setAllowedPrefixes.setAccessible(true);
            final Method addCodeSource = delegateClazz.getDeclaredMethod("addCodeSource", Path.class);
            addCodeSource.setAccessible(true);
            for (Path library : miscGameLibraries) {
                setAllowedPrefixes.invoke(knotClassLoaderDelegate, library, new String[0]);
                addCodeSource.invoke(knotClassLoaderDelegate, library);
            }
        } catch (Throwable t) {
            throw new RuntimeException("Failed to unlock library on knot", t);
        }
    }
}
