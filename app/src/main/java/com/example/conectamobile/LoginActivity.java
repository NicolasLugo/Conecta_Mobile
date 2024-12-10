package com.example.conectamobile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    DatabaseReference db = FirebaseDatabase.getInstance().getReference("Usuarios");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        Button btnLogin;
        EditText txtCorreo, txtContrasena;

        btnLogin = findViewById(R.id.btnIniciarSesion);
        txtCorreo = findViewById(R.id.txtCorreo);
        txtContrasena = findViewById(R.id.txtContrasena);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String correo = txtCorreo.getText().toString();
                String contrasena = txtContrasena.getText().toString();

                if(!correo.isEmpty() && !contrasena.isEmpty()){
                    iniciarSesion(correo, contrasena);
                } else {
                    Toast.makeText(LoginActivity.this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void iniciarSesion(String email, String passwd){
        mAuth.signInWithEmailAndPassword(email, passwd)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            String userId = mAuth.getCurrentUser().getUid();
                            db.child(userId).child("Usuario").get().addOnCompleteListener(dataTask -> {
                                if(dataTask.isSuccessful() && dataTask.getResult().exists()){
                                    String nombreUsuario = dataTask.getResult().getValue(String.class);
                                    Toast.makeText(LoginActivity.this, "Bienvenido, " + nombreUsuario + "!", Toast.LENGTH_LONG).show();
                                    Intent i = new Intent(LoginActivity.this, ChatListActivity.class);
                                    startActivity(i);
                                    finish();
                                } else {
                                    Log.e("Error Login", "Este es dataTask: " + dataTask.getException());
                                    Toast.makeText(LoginActivity.this, "Error al obtener el nombre del usuario.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Log.e("Login Error", "Error: ", task.getException());
                            Toast.makeText(LoginActivity.this, "Error: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}