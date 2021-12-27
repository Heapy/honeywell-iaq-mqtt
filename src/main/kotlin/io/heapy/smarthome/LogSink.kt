package io.heapy.smarthome

class LogSink : Sink {
    override fun process(update: DeviceUpdate) {
        logger.info("Update received: {}", update)
    }
}
