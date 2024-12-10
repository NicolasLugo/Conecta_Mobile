package com.example.conectamobile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ContactListActivity extends AppCompatActivity {
    private SearchView lupa;
    private TextView vacio;
    private ListView listaContactos;
    private FloatingActionButton btnNuevoContacto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        lupa = findViewById(R.id.lupa);
        vacio = findViewById(R.id.vacio);
        listaContactos = findViewById(R.id.listaContactos);
        listaContactos.setEmptyView(vacio);
        btnNuevoContacto = findViewById(R.id.btnNuevoContacto);

        btnNuevoContacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nuevoContacto();
            }
        });

        lupa.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        listarContactos();

        listaContactos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Contacto contacto = (Contacto) listaContactos.getAdapter().getItem(position);

                Intent i = new Intent(ContactListActivity.this, ChatActivity.class);
                i.putExtra("contactName", contacto.getNombre());
                i.putExtra("contactEmail", contacto.getEmail());
                i.putExtra("contactUid", contacto.getId());
                startActivity(i);
            }
        });
    }

    private void nuevoContacto(){
        Intent i = new Intent(this, NewContactActivity.class);
        startActivity(i);
    }

    private void listarContactos(){
        DatabaseReference refContactos = FirebaseDatabase.getInstance().getReference("Contactos");
        List<Contacto> listAdapter = new ArrayList<>();
        List<Contacto> list = new ArrayList<>();
        ArrayAdapter<Contacto> adaptador = new ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, list);
        listaContactos.setAdapter(adaptador);

        refContactos.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listAdapter.clear();
                list.clear();
                for (DataSnapshot contactoSnapshot : snapshot.getChildren()) {
                    String id = contactoSnapshot.getKey();
                    String correo = "";
                    String nombre = contactoSnapshot.child("Nombre").getValue(String.class);

                    Log.d("Firebase", "Datos del contacto: " + contactoSnapshot.getValue());
                    Log.d("Nombre", "nombre = " + nombre + " / Correo = " + correo);

                    if(nombre != null){
                        Contacto contactoCompleto = new Contacto(nombre, correo, id);
                        list.add(contactoCompleto);
                        Contacto contacto1 = new Contacto(nombre);
                        listAdapter.add(contacto1);
                    }
                }
                adaptador.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error al leer datos: " + error.getMessage());
            }
        });
    }
}