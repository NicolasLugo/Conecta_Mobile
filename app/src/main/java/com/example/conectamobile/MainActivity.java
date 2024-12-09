package com.example.conectamobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button registrar, iniciarSesion;

        registrar = findViewById(R.id.btnRegistrar);
        iniciarSesion = findViewById(R.id.btnLogin);

        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registrarUsuario();
            }
        });

        iniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inicioSesion();
            }
        });
    }

    public void registrarUsuario(){
        Intent i = new Intent(this, RegisterActivity.class);
        startActivity(i);
    }

    public void inicioSesion(){
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }
}