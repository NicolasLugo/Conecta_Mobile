package com.example.conectamobile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttException;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView rvMensajes;
    private EditText txtMensaje;
    private ImageButton btnEnviar;
    private TextView tvContacto;
    private MqttAndroidClient mqttClient;
    private static final String MQTT_BROKER_URL = "tcp://test.mosquitto.org:1883"; // Cambiar por tu broker
    private static final String TOPIC_CHAT = "ConectaMobile/NicolasLugo/chat/mensajes/lugopte720@gmail.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mqttClient = new MqttAndroidClient(getApplicationContext(), MQTT_BROKER_URL, MqttClient.generateClientId());
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);

        try{
            mqttClient.connect(options, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d("MQTT", "Conexión exitosa");
                    suscribirTema();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e("MQTT", "Error al conectar: " + exception.getMessage());
                }
            });
        } catch (MqttException e){
            e.printStackTrace();
        }

        rvMensajes = findViewById(R.id.rvMensajes);
        txtMensaje = findViewById(R.id.txtMensaje);
        btnEnviar = findViewById(R.id.btnEnviar);
        tvContacto = findViewById(R.id.tvContacto);

        Intent i = getIntent();
        String contactName = i.getStringExtra("contactName");
        String contactUid = i.getStringExtra("contactUid");

        tvContacto.setText(contactName);
        tvContacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ChatActivity.this, EditContactActivity.class);
                i.putExtra("contactName", contactName);
                i.putExtra("contactUid", contactUid);
                startActivity(i);}
        });


        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mensaje = txtMensaje.getText().toString().trim();
                if(!mensaje.isEmpty()){
                    publicarMensaje(mensaje);
                    txtMensaje.setText("");
                }
            }
        });
    }

    private void suscribirTema(){
        try {
            mqttClient.subscribe(TOPIC_CHAT, 1);
            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    Log.e("MQTT", "Conexión perdida");
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    String recibido = new String(message.getPayload());
                    Log.d("MQTT", "Mensaje recibido: " + recibido);
                    //agregar al recyclerView cuando ya este funcional
                    /*runOnUiThread(() -> {
                        // Actualiza el RecyclerView
                        mensajesList.add(recibido); // Agrega el mensaje a la lista
                        adaptador.notifyDataSetChanged();
                    });*/
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    Log.d("MQTT", "Mensaje Entregado");
                }
            });
        } catch (MqttException e){
            e.printStackTrace();
        }
    }

    private void publicarMensaje(String message){
        try{
            MqttMessage mqttMessage = new MqttMessage();
            mqttMessage.setPayload(message.getBytes());
            mqttClient.publish(TOPIC_CHAT, mqttMessage);
            Log.d("MQTT", "Mensaje enviado: " + message);
        } catch (MqttException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        try {
            mqttClient.disconnect();
            Log.d("MQTT", "Desconectado del broker MQTT");
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}