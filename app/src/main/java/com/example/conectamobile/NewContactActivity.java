package com.example.conectamobile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class NewContactActivity extends AppCompatActivity {
    private EditText txtNombreContacto, txtEmail;
    private ImageView image;
    private Button btnCargarImagen, btnCancelar, btnAgregar;
    private static final int PICK_IMAGE = 1;
    //private Uri imageUri;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_contact);

        txtNombreContacto = findViewById(R.id.txtNombreContacto);
        txtEmail = findViewById(R.id.txtEmail);
        image = findViewById(R.id.imgContacto);
        btnCargarImagen = findViewById(R.id.btnCargarImagen);
        btnCancelar = findViewById(R.id.btnCancelar);
        btnAgregar = findViewById(R.id.btnAgregar);

        btnCargarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Selecciona una imagen"), PICK_IMAGE);
            }
        });

        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String correo = txtEmail.getText().toString().trim();
                String nombre = txtNombreContacto.getText().toString().trim();

                if (!nombre.isEmpty() || !correo.isEmpty()) {
                    //String imagen64 = convertirImagen(bitmap);
                    //String imagen64Comprimido = compressBase64(imagen64);
                    cargarAFirebase(correo, nombre /*, imagen64Comprimido*/);
                    finish();
                } else {
                    Toast.makeText(NewContactActivity.this, "Completa todos los datos", Toast.LENGTH_SHORT).show();
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

    private void cargarAFirebase(String email, String name /*String image64*/){
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Usuarios");
        String contactId = db.push().getKey();

        if(contactId != null){
            Contacto c = new Contacto(name, email);

            db.child(contactId).setValue(c)
                    .addOnCompleteListener(NewContactActivity.this, task ->  {
                        if(task.isSuccessful()){
                            Toast.makeText(NewContactActivity.this, "Contacto agregado correctamente", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(NewContactActivity.this, "Error al guardar en la base de datos", Toast.LENGTH_SHORT).show();
                            Log.e("Firebase", "Error al guardar los datos: ", task.getException());
                        }
                    });
        } else {
            Toast.makeText(this, "Error al generar ID único", Toast.LENGTH_SHORT).show();
        }
    }

    /*public static String compressBase64(String base64String) {
        try {
            byte[] input = base64String.getBytes("UTF-8");
            Deflater deflater = new Deflater();
            deflater.setInput(input);
            deflater.finish();

            byte[] output = new byte[input.length];
            int compressedDataLength = deflater.deflate(output);
            deflater.end();

            return Base64.encodeToString(output, 0, compressedDataLength, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }*/

    /*public static String decompressBase64(String compressedBase64) {
        try {
            byte[] compressedData = Base64.decode(compressedBase64, Base64.DEFAULT);
            Inflater inflater = new Inflater();
            inflater.setInput(compressedData);

            byte[] output = new byte[compressedData.length * 4]; // Estimación del tamaño descomprimido
            int decompressedDataLength = inflater.inflate(output);
            inflater.end();

            return new String(output, 0, decompressedDataLength, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }*/

    /*private String convertirImagen(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }*/

}