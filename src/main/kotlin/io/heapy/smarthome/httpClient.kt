package io.heapy.smarthome

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.CIOEngineConfig
import io.ktor.client.features.defaultRequest
import io.ktor.client.features.json.JacksonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.websocket.WebSockets
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.userAgent
import java.security.KeyStore
import java.security.cert.X509Certificate
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

suspend fun httpClient(
    disableSslValidation: Boolean = true,
    body: suspend (HttpClient) -> Unit,
) {
    HttpClient(CIO) {
        if (disableSslValidation) disableSslValidation()
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

private fun HttpClientConfig<CIOEngineConfig>.disableSslValidation() {
    fun findTrustManager(): X509TrustManager {
        val factory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())!!
        factory.init(null as KeyStore?)
        val manager = factory.trustManagers!!

        return manager.filterIsInstance<X509TrustManager>().first()
    }

    engine {
        https {
            trustManager = object : X509TrustManager by findTrustManager() {
                override fun checkServerTrusted(chain: Array<out X509Certificate>, authType: String) {
                }
            }
        }
    }
}
