package com.example.conectamobile;

import android.util.Log;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;

import java.nio.charset.StandardCharsets;

public class MqttManager {
    private static final String TAG = "MqttManager";
    private static final String SERVER_HOST = "broker.hivemq.com";
    private static final int SERVER_PORT = 1883;
    private Mqtt3AsyncClient mqttClient;

    public MqttManager() {
    }

    public void connect() {
        mqttClient = MqttClient.builder()
                .useMqttVersion3()
                .serverHost(SERVER_HOST)
                .serverPort(SERVER_PORT)
                .buildAsync();

        mqttClient.connectWith()
                .send()
                .whenComplete((ack, throwable) -> {
                    if (throwable != null) {
                        Log.e(TAG, "Error al conectar: " + throwable.getMessage());
                    } else {
                        Log.d(TAG, "Conexión exitosa.");
                        subscribeToTopic("prueba/de/conexion/mqtt/hive/conectamobile");
                    }
                });
    }

    public void subscribeToTopic(String topic) {
        mqttClient.toAsync().subscribeWith()
                .topicFilter(topic) // Usar el tema recibido como parámetro
                .callback(publish -> {
                    String message = new String(publish.getPayloadAsBytes(), StandardCharsets.UTF_8);
                    Log.d("MqttManager", "Mensaje recibido: " + message);
                })
                .send();
    }

    public void publishMessage(String topic, String message) {
        mqttClient.publishWith()
                .topic(topic)
                .payload(message.getBytes(StandardCharsets.UTF_8))
                .send()
                .whenComplete((publishAck, throwable) -> {
                    if (throwable != null) {
                        Log.e(TAG, "Error al publicar: " + throwable.getMessage());
                    } else {
                        Log.d(TAG, "Mensaje publicado con éxito.");
                    }
                });
    }

    public void connectAndPublish(String topic, String message) {
        mqttClient = MqttClient.builder()
                .useMqttVersion3()
                .serverHost(SERVER_HOST)
                .serverPort(SERVER_PORT)
                .buildAsync();

        mqttClient.connectWith()
                .send()
                .whenComplete((ack, throwable) -> {
                    if (throwable != null) {
                        Log.e(TAG, "Error al conectar: " + throwable.getMessage());
                    } else {
                        Log.d(TAG, "Conexión exitosa.");
                        publishMessage(topic, message);
                    }
                });
    }
}