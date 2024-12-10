package com.example.conectamobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ChatListActivity extends AppCompatActivity {
    private ListView listaChat;
    private TextView vacio;
    private FloatingActionButton btnNuevoChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        listaChat = findViewById(R.id.listaChats);
        vacio = findViewById(R.id.vacio);
        btnNuevoChat = findViewById(R.id.btnNuevoChat);
        listaChat.setEmptyView(vacio);

        btnNuevoChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ChatListActivity.this, ContactListActivity.class);
                startActivity(i);
            }
        });

        listaChat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                abrirContacto();
            }
        });
    }

    private void abrirContacto(){
    }
}