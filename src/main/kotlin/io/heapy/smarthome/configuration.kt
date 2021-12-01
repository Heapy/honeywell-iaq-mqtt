package io.heapy.smarthome

import com.typesafe.config.ConfigFactory
import io.github.config4k.extract

fun readConfiguration(): Configuration {
    val configuration = ConfigFactory
        .load()
        .extract<Configuration>()

    logger.info("Running with honeywell configuration: {}", configuration.honeywell)
    logger.info("Running with mqtt configuration: {}", configuration.mqtt)

    return configuration
}

data class Configuration(
    val honeywell: HoneywellConfiguration,
    val mqtt: MqttConfiguration?,
)

data class HoneywellConfiguration(
    val phoneNumber: String,
    val password: String,
    val phoneUuid: String,
)

data class MqttConfiguration(
    val url: String?,
    val clientId: String?,
    val username: String?,
    val password: String?,
)
