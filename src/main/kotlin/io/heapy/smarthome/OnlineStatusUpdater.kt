package io.heapy.smarthome

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.eclipse.paho.client.mqttv3.MqttAsyncClient
import java.time.Duration
import java.time.Instant

class OnlineStatusUpdater(
    private val mqttAsyncClient: MqttAsyncClient,
) {
    private var job: Job? = null
    private val devices = mutableMapOf<String, Instant>()

    fun pingDevice(deviceId: String) {
        devices[deviceId] = Instant.now()
    }

    fun run() {
        job = CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                delay(pingInterval)
                devices.forEach { (deviceId, lastPing) ->
                    val offline = lastPing
                        .plus(Duration.ofMillis(pingInterval))
                        .isBefore(Instant.now())
                    logger.info("OnlineStatusUpdater: Device $deviceId is online: ${!offline}")
                    mqttAsyncClient.publishDeviceStatus(deviceId, offline)
                }
            }
        }
    }

    fun stop() {
        job?.let {
            it.cancel()
            job = null
        }
    }

    private suspend fun MqttAsyncClient.publishDeviceStatus(
        deviceId: String,
        offline: Boolean
    ) {
        coPublish(
            "honeywell/$deviceId/state",
            (if (offline) "offline" else "online").toByteArray()
        )
    }

    private companion object {
        private const val pingInterval = 60_000L
    }
}
