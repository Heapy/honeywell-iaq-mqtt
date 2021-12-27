package io.heapy.smarthome

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttAsyncClient

class MqttSink(
    private val mqttAsyncClient: MqttAsyncClient,
    private val mapper: ObjectMapper,
    private val onlineStatusUpdater: OnlineStatusUpdater,
) : Sink {
    override fun process(update: DeviceUpdate) {
        onlineStatusUpdater.pingDevice(update.deviceId)

        CoroutineScope(Dispatchers.IO).launch {
            mqttAsyncClient.publishAutoDiscoveryConfigs(update.deviceId)
            mqttAsyncClient.coPublish(
                "honeywell/${update.deviceId}/update",
                mapper.writeValueAsBytes(update)
            )
        }
    }

    suspend fun MqttAsyncClient.publishAutoDiscoveryConfigs(
        deviceId: String
    ) {
        coPublish(
            "homeassistant/sensor/$deviceId/temperature/config",
            mapper.writeValueAsBytes(
                AutoDiscoveryConfig(
                    availability = listOf(
                        AutoDiscoveryConfig.Availability(
                            topic = "honeywell/$deviceId/state"
                        )
                    ),
                    device = AutoDiscoveryConfig.Device(
                        identifiers = listOf(deviceId),
                        name = "Honeywell Air Quality Monitor",
                        model = "Honeywell Air Quality Monitor",
                        manufacturer = "Honeywell",
                        sw_version = "null",
                    ),
                    device_class = "temperature",
                    enabled_by_default = true,
                    json_attributes_topic = "honeywell/$deviceId/update",
                    name = "HAQ Temperature",
                    state_class = "measurement",
                    state_topic = "honeywell/$deviceId/update",
                    unique_id = "${deviceId}_temperature",
                    unit_of_measurement = "°C",
                    value_template = "{{ value_json.temperature }}",
                )
            )
        )
        coPublish(
            "homeassistant/sensor/$deviceId/humidity/config",
            mapper.writeValueAsBytes(
                AutoDiscoveryConfig(
                    availability = listOf(
                        AutoDiscoveryConfig.Availability(
                            topic = "honeywell/$deviceId/state"
                        )
                    ),
                    device = AutoDiscoveryConfig.Device(
                        identifiers = listOf(deviceId),
                        name = "Honeywell Air Quality Monitor",
                        model = "Honeywell Air Quality Monitor",
                        manufacturer = "Honeywell",
                        sw_version = "null",
                    ),
                    device_class = "humidity",
                    enabled_by_default = true,
                    json_attributes_topic = "honeywell/$deviceId/update",
                    name = "HAQ Humidity",
                    state_class = "measurement",
                    state_topic = "honeywell/$deviceId/update",
                    unique_id = "${deviceId}_humidity",
                    unit_of_measurement = "%",
                    value_template = "{{ value_json.humidity }}",
                )
            )
        )
        coPublish(
            "homeassistant/sensor/$deviceId/pm25/config",
            mapper.writeValueAsBytes(
                AutoDiscoveryConfig(
                    availability = listOf(
                        AutoDiscoveryConfig.Availability(
                            topic = "honeywell/$deviceId/state"
                        )
                    ),
                    device = AutoDiscoveryConfig.Device(
                        identifiers = listOf(deviceId),
                        name = "Honeywell Air Quality Monitor",
                        model = "Honeywell Air Quality Monitor",
                        manufacturer = "Honeywell",
                        sw_version = "null",
                    ),
                    device_class = "pm25",
                    enabled_by_default = true,
                    json_attributes_topic = "honeywell/$deviceId/update",
                    name = "HAQ PM2.5",
                    state_class = "measurement",
                    state_topic = "honeywell/$deviceId/update",
                    unique_id = "${deviceId}_pm25",
                    unit_of_measurement = "µg/m³",
                    value_template = "{{ value_json.pm25 }}",
                )
            )
        )
        coPublish(
            "homeassistant/sensor/$deviceId/tvoc/config",
            mapper.writeValueAsBytes(
                AutoDiscoveryConfig(
                    availability = listOf(
                        AutoDiscoveryConfig.Availability(
                            topic = "honeywell/$deviceId/state"
                        )
                    ),
                    device = AutoDiscoveryConfig.Device(
                        identifiers = listOf(deviceId),
                        name = "Honeywell Air Quality Monitor",
                        model = "Honeywell Air Quality Monitor",
                        manufacturer = "Honeywell",
                        sw_version = "null",
                    ),
                    device_class = "volatile_organic_compounds",
                    enabled_by_default = true,
                    json_attributes_topic = "honeywell/$deviceId/update",
                    name = "HAQ TVOC",
                    state_class = "measurement",
                    state_topic = "honeywell/$deviceId/update",
                    unique_id = "${deviceId}_tvoc",
                    unit_of_measurement = "µg/m³",
                    value_template = "{{ value_json.tvoc }}",
                )
            )
        )
        coPublish(
            "homeassistant/sensor/$deviceId/hcho/config",
            mapper.writeValueAsBytes(
                AutoDiscoveryConfig(
                    availability = listOf(
                        AutoDiscoveryConfig.Availability(
                            topic = "honeywell/$deviceId/state"
                        )
                    ),
                    device = AutoDiscoveryConfig.Device(
                        identifiers = listOf(deviceId),
                        name = "Honeywell Air Quality Monitor",
                        model = "Honeywell Air Quality Monitor",
                        manufacturer = "Honeywell",
                        sw_version = "null",
                    ),
                    device_class = "volatile_organic_compounds",
                    enabled_by_default = true,
                    json_attributes_topic = "honeywell/$deviceId/update",
                    name = "HAQ HCHO",
                    state_class = "measurement",
                    state_topic = "honeywell/$deviceId/update",
                    unique_id = "${deviceId}_hcho",
                    unit_of_measurement = "µg/m³",
                    value_template = "{{ value_json.hcho }}",
                )
            )
        )
        coPublish(
            "homeassistant/sensor/$deviceId/co2/config",
            mapper.writeValueAsBytes(
                AutoDiscoveryConfig(
                    availability = listOf(
                        AutoDiscoveryConfig.Availability(
                            topic = "honeywell/$deviceId/state"
                        )
                    ),
                    device = AutoDiscoveryConfig.Device(
                        identifiers = listOf(deviceId),
                        name = "Honeywell Air Quality Monitor",
                        model = "Honeywell Air Quality Monitor",
                        manufacturer = "Honeywell",
                        sw_version = "null",
                    ),
                    device_class = "carbon_dioxide",
                    enabled_by_default = true,
                    json_attributes_topic = "honeywell/$deviceId/update",
                    name = "HAQ C0₂",
                    state_class = "measurement",
                    state_topic = "honeywell/$deviceId/update",
                    unique_id = "${deviceId}_co2",
                    unit_of_measurement = "ppm",
                    value_template = "{{ value_json.co2 }}",
                )
            )
        )
        coPublish(
            "homeassistant/sensor/$deviceId/aqi/config",
            mapper.writeValueAsBytes(
                AutoDiscoveryConfig(
                    availability = listOf(
                        AutoDiscoveryConfig.Availability(
                            topic = "honeywell/$deviceId/state"
                        )
                    ),
                    device = AutoDiscoveryConfig.Device(
                        identifiers = listOf(deviceId),
                        name = "Honeywell Air Quality Monitor",
                        model = "Honeywell Air Quality Monitor",
                        manufacturer = "Honeywell",
                        sw_version = "null",
                    ),
                    device_class = "aqi",
                    enabled_by_default = true,
                    json_attributes_topic = "honeywell/$deviceId/update",
                    name = "HAQ AIQ",
                    state_class = "measurement",
                    state_topic = "honeywell/$deviceId/update",
                    unique_id = "${deviceId}_aqi",
                    unit_of_measurement = "%",
                    value_template = "{{ value_json.iq }}",
                )
            )
        )
    }
}

data class AutoDiscoveryConfig(
    val availability: List<Availability>,
    val device: Device,
    val device_class: String,
    val enabled_by_default: Boolean = true,
    val json_attributes_topic: String,
    val name: String,
    val state_class: String,
    val state_topic: String,
    val unique_id: String,
    val unit_of_measurement: String,
    val value_template: String,
) {
    data class Availability(
        val topic: String,
    )

    data class Device(
        val identifiers: List<String>,
        val manufacturer: String,
        val model: String,
        val name: String,
        val sw_version: String,
    )
}

/**
 * Simplified implementation that utilized IO pool.
 * Can be replaced with true async publish implementation.
 */
@Suppress("RedundantSuspendModifier")
suspend fun MqttAsyncClient.coPublish(
    topic: String,
    payload: ByteArray,
    qos: Int = 1,
    retained: Boolean = false,
): IMqttDeliveryToken? {
    return publish(topic, payload, qos, retained)
}
