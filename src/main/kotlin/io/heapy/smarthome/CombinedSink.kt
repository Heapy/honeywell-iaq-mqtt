package io.heapy.smarthome

class CombinedSink(
    private vararg val sinks: Sink,
) : Sink {
    override fun process(update: DeviceUpdate) {
        sinks.forEach { sink -> sink.process(update) }
    }
}
