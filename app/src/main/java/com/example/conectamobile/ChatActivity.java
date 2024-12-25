package com.example.conectamobile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private EditText txtMensaje;
    private MqttManager mqtt;
    private ArrayList<String> messages;
    private ArrayAdapter<String> adapter;
    private RecyclerView rvMensajes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        rvMensajes = findViewById(R.id.rvMensajes);
        txtMensaje = findViewById(R.id.txtMensaje);
        ImageButton btnEnviar = findViewById(R.id.btnEnviar);
        TextView tvContacto = findViewById(R.id.tvContacto);

        messages = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, R.layout.item_message, R.id.tvMessage, messages);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvMensajes.setLayoutManager(layoutManager);

        mqtt = new MqttManager();
        mqtt.connect();

        Intent i = getIntent();
        String contactName = i.getStringExtra("contactName");
        String contactUid = i.getStringExtra("contactUid");

        Log.d("Datos getIntent", "datos: " + contactName);

        tvContacto.setText(contactName);
        tvContacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ChatActivity.this, EditContactActivity.class);
                i.putExtra("contactName", contactName);
                i.putExtra("contactUid", contactUid);
                startActivity(i);

                Log.d("Datos enviados", "A editContact: " + contactName + " / " + contactUid);
            }
        });

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enviarMensaje();
            }
        });

        rvMensajes.setAdapter(new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = adapter.getView(viewType, null, parent);
                return new RecyclerView.ViewHolder(view) {};
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                adapter.getView(position, holder.itemView, null);
            }

            @Override
            public int getItemCount() {
                return adapter.getCount();
            }
        });
    }

    private void enviarMensaje(){;
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Mensajes");
        String mensaje = txtMensaje.getText().toString();

        if(!mensaje.isEmpty()){
            mqtt.connectAndPublish("prueba/de/conexion/mqtt/hive/conectamobile", mensaje);
            txtMensaje.setText("");
            messages.add(mensaje);
            adapter.notifyDataSetChanged();
        }
    }
}