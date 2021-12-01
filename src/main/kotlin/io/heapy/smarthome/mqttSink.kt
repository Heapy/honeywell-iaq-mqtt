package io.heapy.smarthome

import org.eclipse.paho.client.mqttv3.MqttAsyncClient
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

suspend fun mqttSink(
    configuration: Configuration,
): Sink {
    val mqttAsyncClient = MqttAsyncClient(
        configuration.mqtt?.url,
        configuration.mqtt?.clientId,
        MemoryPersistence()
    )
    mqttAsyncClient.connect()

    return { message ->
//        mqttAsyncClient.publish(
//            "/honeywell-iaq/${configuration.mqtt}"
//        )
    }
}
