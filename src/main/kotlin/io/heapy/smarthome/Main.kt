package io.heapy.smarthome

import org.slf4j.Logger
import org.slf4j.LoggerFactory

val logger: Logger = LoggerFactory.getLogger("Main")

suspend fun main(args: Array<String>) {
    when (args.getOrElse(0) { "log" }) {
        "log" -> ApplicationFactory().logApplication.run()
        "mqtt" -> ApplicationFactory().run {
            mqttAsyncClient.connect()
            onlineStatusUpdater.run()
            mqttApplication.run()
        }
        else -> logger.error(
            """
            Use one of known commands: 
                log – log updates from devices to console
                mqtt – log updates to mqtt
            """.trimIndent()
        )
    }
}

