package io.heapy.smarthome

fun logSink(
    configuration: Configuration,
): Sink {
    return {
        logger.info("Update received: {}", it)
    }
}
