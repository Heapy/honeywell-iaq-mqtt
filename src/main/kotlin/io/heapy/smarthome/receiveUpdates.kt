package io.heapy.smarthome

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.client.features.websocket.webSocket
import io.ktor.client.request.header
import io.ktor.client.request.url
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

typealias Sink = (DeviceUpdate) -> Unit

data class DeviceUpdate(
    val deviceId: String,
    val co2: String,
    val hcho: String,
    val tvoc: String,
    val pm25: String,
    val temperature: String,
    val humidity: String,
)

suspend fun receiveUpdates(
    sinkProvider: suspend (Configuration) -> Sink,
) {
    val config = readConfiguration()
    val sink = sinkProvider(config)
    val mapper = jacksonObjectMapper()

    httpClient { client ->
        val cookie = client.login(config)
        client.logDevices(cookie)

        coroutineScope {
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
                        val message = othersMessage?.readText() ?: ""
                        mapper.parseUpdate(message)?.let(sink)
                    }
                }
            }
        }
    }
}

fun ObjectMapper.parseUpdate(message: String): DeviceUpdate? {
    val node = readTree(message)
    val type = node.get("type").asText("Unknown")
    return when (type) {
        "IAQGetData" -> {
            node.get("temperatureUnit").asText()?.let {
                logger.debug("Device temperature unit: {}", it)
            }

            DeviceUpdate(
                deviceId = node.get("deviceId").asText(),
                pm25 = node.get("pm25").asText(""),
                humidity = node.get("humidity").asText(""),
                temperature = node.get("temperature").asText(""),
                co2 = node.get("co2").asText(""),
                tvoc = node.get("tvoc").asText(""),
                hcho = node.get("hcho").asText(""),
            )
        }
        "IAQLanguage", "OnlineStatus" -> {
            logger.debug("{} message: {}", type, message)
            null
        }
        else -> {
            logger.error("Received message with unknown type: {}", message)
            null
        }
    }
}

/*
Known ws messages:
{"data":"en-US","type":"IAQLanguage","deviceId":"0020000xxxxxxxxxxxxx"}
 */
