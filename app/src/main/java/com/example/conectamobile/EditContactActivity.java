package com.example.conectamobile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.HashMap;

public class EditContactActivity extends AppCompatActivity {
    private EditText txtNombreContacto, txtEmail;
    private Button btnCargarImagen, btnCancelar, btnModificar;
    private static final int PICK_IMAGE = 1;
    private Bitmap bitmap;
    private ImageView image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);

        txtNombreContacto = findViewById(R.id.txtNombreContacto);
        txtEmail = findViewById(R.id.txtEmail);
        btnCargarImagen = findViewById(R.id.btnCargarImagen);
        btnCancelar = findViewById(R.id.btnCancelar);
        btnModificar = findViewById(R.id.btnModificar);
        image = findViewById(R.id.imgContacto);

        Intent i = getIntent();
        String id = i.getStringExtra("contactUid");
        obtenerCorreo(id);

        btnCargarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Selecciona una imagen"), PICK_IMAGE);
            }
        });

        btnModificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String correo = txtEmail.getText().toString();
                String nombre = txtNombreContacto.getText().toString();

                if (!nombre.isEmpty() && !correo.isEmpty()) {
                    actualizarDatos(id, correo, nombre);
                    finish();
                } else {
                    Toast.makeText(EditContactActivity.this, "Tu perfil debe tener un nombre", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void obtenerCorreo(String userId) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = db.child("Contactos").child(userId);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String correo = snapshot.child("Correo").getValue(String.class);
                    if (correo != null) {
                        Intent i = getIntent();
                        String nombreContacto = i.getStringExtra("contactName");
                        txtNombreContacto.setText(nombreContacto);
                        txtEmail.setText(correo);

                    } else {
                        Log.w("Firebase", "El usuario no tiene un correo registrado.");
                    }
                } else {
                    Log.e("Firebase", "Usuario no encontrado.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error al obtener el correo: ", error.toException());
            }
        });
    }

    private void actualizarDatos(String userId, String nuevoCorreo, String nuevoNombre){
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Contactos");
        HashMap<String, Object> userData = new HashMap<>();
        //userData.put("Imagen de perfil", image64);
        userData.put("Correo", nuevoCorreo);
        userData.put("Nombre", nuevoNombre);
        db.child(userId).setValue(userData)
                .addOnCompleteListener(EditContactActivity.this, task ->  {
                    if(task.isSuccessful()){
                        Toast.makeText(EditContactActivity.this, "Contacto modificado correctamente", Toast.LENGTH_SHORT).show();
                        Log.d("Que se modifico?", "modificado?" + nuevoCorreo + " / " + nuevoNombre + " / " + userId);
                    } else {
                        Toast.makeText(EditContactActivity.this, "Error al modificar en la base de datos", Toast.LENGTH_SHORT).show();
                        Log.e("Firebase", "Error al guardar los datos: ", task.getException());
                    }
                });    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imagenUri = data.getData();
            try {
                // Convertir la URI a un Bitmap y mostrarlo en el ImageView
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagenUri);
                image.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
            }
        }
    }

}