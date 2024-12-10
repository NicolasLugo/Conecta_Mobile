package com.example.conectamobile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView rvMensajes;
    private EditText txtMensaje;
    private ImageButton btnEnviar;
    private TextView tvContacto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        rvMensajes = findViewById(R.id.rvMensajes);
        txtMensaje = findViewById(R.id.txtMensaje);
        btnEnviar = findViewById(R.id.btnEnviar);
        tvContacto = findViewById(R.id.tvContacto);

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
    }
}