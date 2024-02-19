package icu.takeneko.nfh.command

import icu.takeneko.nfh.reload.Reload
import net.minecraft.server.command.CommandManager.*

val reloadCommand = literal("reload").executes {
    try {
        Reload.reload()
    } catch (e: Exception) {
        println()
        e.printStackTrace()
    }
    1
}