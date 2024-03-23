package icu.takeneko.nfh

import com.mojang.logging.LogUtils
import org.slf4j.LoggerFactory
import org.slf4j.event.Level


val logger = LoggerFactory.getLogger("NekoFabricHacks")

fun init(){
    logger.info("Nya!")
    //LogUtils.configureRootLoggingLevel(Level.DEBUG)
}