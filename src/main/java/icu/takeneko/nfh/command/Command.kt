package icu.takeneko.nfh.command

import net.minecraft.server.command.CommandManager.*

val reloadCommand = literal("reload").executes {
    try {
    } catch (e: Exception) {
        println()
        e.printStackTrace()
    }
    1
}