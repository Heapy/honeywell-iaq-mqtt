package io.heapy.smarthome

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.url
import io.ktor.http.HttpHeaders

suspend fun HttpClient.logDevices(
    cookie: String,
) {
    val response = get<DevicesResponse> {
        url("https://iaq.honcloud.honeywell.com.cn/v2/00100002/user/device/list")
        header(HttpHeaders.Cookie, cookie)
    }

    logger.info("{} devices fetched", response.devices.size)
    response.devices.forEach {
        logger.info("{}", it)
    }
}

data class DeviceResponse(
    val deviceId: String,
    val deviceSerial: String,
    val online: Boolean,
    val deviceInfo: DeviceInfoResponse,
)

data class DevicesResponse(
    val devices: List<DeviceResponse>,
)

data class DeviceInfoResponse(
    val room: String,
    val home: String,
)
