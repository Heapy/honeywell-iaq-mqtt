package io.heapy.smarthome

import com.typesafe.config.ConfigFactory
import io.github.config4k.extract
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.defaultRequest
import io.ktor.client.features.json.JacksonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.websocket.WebSockets
import io.ktor.client.features.websocket.webSocket
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import io.ktor.http.contentType
import io.ktor.http.userAgent
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.eclipse.paho.client.mqttv3.MqttAsyncClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Paths

suspend fun main(args: Array<String>) {
    when (args.getOrNull(0)) {
        "listDevices" -> listDevices()
        "log" -> receiveUpdates(::logSink)
        "mqtt" -> receiveUpdates(::mqttSink)
        else -> logger.error("""
            Use one of known commands: 
                listDevices – list known devices that was added in honeywell iaq
                log – log updates from devices to console
                logMqtt – log updates to mqtt
        """.trimIndent())
    }
}

val logger: Logger = LoggerFactory.getLogger("Main")

suspend fun listDevices() {
    data class DeviceInfoResponse(
        val room: String,
        val home: String,
    )

    data class DeviceResponse(
        val deviceId: String,
        val deviceSerial: String,
        val online: Boolean,
        val deviceInfo: DeviceInfoResponse,
    )

    data class DevicesResponse(
        val devices: List<DeviceResponse>,
    )

    val config = readConfiguration()

    httpClient { client ->
        val cookie = client.login(config)

        val devices = client.get<DevicesResponse> {
            url("https://iaq.honcloud.honeywell.com.cn/v2/00100002/user/device/list")
            header(HttpHeaders.Cookie, cookie)
        }

        logger.info("{} devices fetched", devices.devices.size)
        devices.devices.forEach {
            logger.info("{}", it)
        }
    }
}

suspend fun logSink(
    configuration: Configuration,
): Sink = logger::info

suspend fun mqttSink(
    configuration: Configuration,
): Sink {
    //    MqttAsyncClient()
    TODO()
}

suspend fun httpClient(
    body: suspend (HttpClient) -> Unit,
) {
    HttpClient(CIO) {
        install(JsonFeature) {
            serializer = JacksonSerializer()
        }
        install(WebSockets)
        defaultRequest {
            contentType(ContentType.Application.Json)
            userAgent("AirQuality/3.0.13 (iPhone; iOS 13.3.1; Scale/3.00s")
        }
    }.use {
        body(it)
    }
}

suspend fun HttpClient.login(
    config: Configuration,
): String {
    data class LoginRequest(
        val phoneNumber: String = "",
        val password: String,
        val phoneUuid: String = "",
        val language: String = "en-US",
        val phoneType: String = "ios",
        val type: String = "LoginUser",
    )

    val loginResponse = post<HttpResponse> {
        url("https://iaq.honcloud.honeywell.com.cn/v2/00100002/user")
        body = LoginRequest(
            phoneNumber = config.honeywell.phoneNumber,
            password = config.honeywell.password,
            phoneUuid = config.honeywell.phoneUuid
        )
    }

    return loginResponse.headers[HttpHeaders.SetCookie]!!
}

typealias Sink = (String) -> Unit

suspend fun receiveUpdates(
    sinkProvider: suspend (Configuration) -> Sink,
) {
    data class UpdatesRequest(
        val deviceId: String,
        val start: String, // 2020-03-07T12:22:26.38Z"
        val end: String, // 2020-04-06T12:22:26.38Z
        val granularity: String = "d",
        val type: String = "IAQHistory",
    )

    data class UpdatesResponse(
        val string: String
    )

    val config = readConfiguration()

    httpClient { client ->
        val cookie = client.login(config)

        coroutineScope {
            (config.honeywell.devices ?: listOf())
                .forEach { device ->
                    launch {
                        client.webSocket({
                            method = HttpMethod.Get
                            url(
                                "wss",
                                "acscloud.honeywell.com.cn",
                                443,
                                "/v1/00100002/phone/connect"
                            )
                            header(HttpHeaders.Cookie, cookie)
                        }) {
                            while (true) {
                                val othersMessage = incoming.receive() as? Frame.Text
                                println(othersMessage?.readText())
                            }
                        }
                    }

//                    val updates = client.post<String> {
//                        url("https://iaq.honcloud.honeywell.com.cn/v2/00100002/user/device")
//                        header(HttpHeaders.Cookie, cookie)
//                        body = UpdatesRequest(
//                            deviceId = device.id,
//                            start = "2021-10-26T00:22:26.38Z",
//                            end = "2021-10-26T09:22:26.38Z",
//                            granularity = "h"
//                        )
//                    }
//
//                    logger.info("{}", updates)
                }
        }
    }
}

fun readConfiguration(): Configuration {
    val configFolder = System.getenv("CONFIG_FOLDER") ?: "./"
    val path = Paths.get(configFolder, "honeywell.conf")
    val configuration = Files
        .newBufferedReader(path)
        .use {
            ConfigFactory.parseReader(it)
        }
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
    val devices: List<HoneywellDevice>?,
    val allDevices: Boolean?
)

data class HoneywellDevice(
    val id: String,
    val mqttName: String,
)

data class MqttConfiguration(
    val url: String?,
    val clientId: String?,
    val topic: String?,
    val username: String?,
    val password: String?,
)
