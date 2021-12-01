package io.heapy.smarthome

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders

suspend fun HttpClient.login(
    config: Configuration,
): String {
    data class LoginRequest(
        val phoneNumber: String,
        val password: String,
        val phoneUuid: String,
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
