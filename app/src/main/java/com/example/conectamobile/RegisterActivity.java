package com.example.conectamobile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Button btnRegistrar;
    private EditText txtUsuario, txtCorreo, txtContrasena;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        txtUsuario = findViewById(R.id.txtUsuario);
        txtContrasena = findViewById(R.id.txtContrasena);
        txtCorreo = findViewById(R.id.txtCorreo);
        btnRegistrar = findViewById(R.id.btnRegistrar);
        mAuth = FirebaseAuth.getInstance();

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = txtUsuario.getText().toString();
                String email = txtCorreo.getText().toString();
                String passwd = txtContrasena.getText().toString();

                if (!user.isEmpty() && !email.isEmpty() && passwd.length() >= 6) {
                    registrarUsuario(user, email, passwd);
                } else {
                    Toast.makeText(RegisterActivity.this, "La contraseña debe contener 6 o más caracteres", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void registrarUsuario(String user, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String userId = mAuth.getCurrentUser().getUid();

                            guardarInfoUsuario(userId, user);
                            Toast.makeText(RegisterActivity.this, "Usuario registrado con éxito.", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(i);
                            finish();
                        } else {
                            Log.e("RegisterError", "Error: ", task.getException());
                            Toast.makeText(RegisterActivity.this, "Error al registrar: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void guardarInfoUsuario(String userId, String user){
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = db.child("Usuarios:").child(userId);

        HashMap<String, Object> userData = new HashMap<>();
        userData.put("Usuario", user);
        userData.put("Correo", mAuth.getCurrentUser().getEmail());

        ref.setValue(userData)
                .addOnCompleteListener(this, task ->  {
                    if(task.isSuccessful()){
                        Log.d("Firebase", "Datos de usuario guardados correctamente.");
                    } else {
                        Log.e("Firebase", "Error al guardar los datos: ", task.getException());
                    }
                });
    }
}