package io.heapy.smarthome

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.defaultRequest
import io.ktor.client.features.json.JacksonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.websocket.WebSockets
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.userAgent
import org.eclipse.paho.client.mqttv3.MqttAsyncClient
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

class ApplicationFactory {
    val config: Configuration by lazy {
        readConfiguration()
    }

    val mapper: ObjectMapper by lazy {
        jacksonObjectMapper()
    }

    val logApplication: Application by lazy {
        Application(
            config = config,
            mapper = mapper,
            httpClient = httpClient,
            sink = logSink,
            onlineStatusUpdater = onlineStatusUpdater,
        )
    }

    val mqttApplication: Application by lazy {
        Application(
            config = config,
            mapper = mapper,
            httpClient = httpClient,
            sink = CombinedSink(logSink, mqttSink),
            onlineStatusUpdater = onlineStatusUpdater,
        )
    }

    val onlineStatusUpdater: OnlineStatusUpdater by lazy {
        OnlineStatusUpdater(
            mqttAsyncClient = mqttAsyncClient,
        )
    }

    val mqttSink: Sink by lazy {
        MqttSink(
            mqttAsyncClient = mqttAsyncClient,
            mapper = mapper,
            onlineStatusUpdater = onlineStatusUpdater,
        )
    }

    val mqttAsyncClient: MqttAsyncClient by lazy {
        MqttAsyncClient(
            config.mqtt?.url,
            config.mqtt?.clientId,
            MemoryPersistence()
        )
    }

    val logSink: Sink by lazy {
        LogSink()
    }

    val httpClient: HttpClient by lazy {
        HttpClient(CIO) {
            if (config.disableSslValidation) disableSslValidation()
            install(JsonFeature) {
                serializer = JacksonSerializer()
            }
            install(WebSockets)
            defaultRequest {
                contentType(ContentType.Application.Json)
                userAgent("AirQuality/3.0.13 (iPhone; iOS 13.3.1; Scale/3.00s")
            }
        }
    }
}

