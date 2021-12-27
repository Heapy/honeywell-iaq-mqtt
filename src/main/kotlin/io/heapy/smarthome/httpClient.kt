package io.heapy.smarthome

import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.cio.CIOEngineConfig
import java.security.KeyStore
import java.security.cert.X509Certificate
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

fun HttpClientConfig<CIOEngineConfig>.disableSslValidation() {
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
