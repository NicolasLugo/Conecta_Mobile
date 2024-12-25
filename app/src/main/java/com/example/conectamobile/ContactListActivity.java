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
    private TextView vacio, sinContactos;
    private ListView listaContactos;
    private FloatingActionButton btnNuevoContacto;
    private ArrayAdapter<Contacto> adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        lupa = findViewById(R.id.lupa);
        vacio = findViewById(R.id.vacio);
        listaContactos = findViewById(R.id.listaContactos);
        listaContactos.setEmptyView(vacio);
        btnNuevoContacto = findViewById(R.id.btnNuevoContacto);
        sinContactos = findViewById(R.id.emptyMessage);

        btnNuevoContacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nuevoContacto();
            }
        });

        listarContactos();

        lupa.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adaptador.getFilter().filter(query, count -> {
                    if(count == 0){
                        sinContactos.setVisibility(View.VISIBLE);
                        listaContactos.setVisibility(View.GONE);
                        vacio.setVisibility(View.GONE);
                    } else {
                        sinContactos.setVisibility(View.GONE);
                        listaContactos.setVisibility(View.VISIBLE);
                    }
                });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adaptador.getFilter().filter(newText, count -> {
                    if(count == 0){
                        sinContactos.setVisibility(View.VISIBLE);
                        vacio.setVisibility(View.GONE);
                        listaContactos.setVisibility(View.GONE);
                    } else {
                        sinContactos.setVisibility(View.GONE);
                        listaContactos.setVisibility(View.VISIBLE);
                    }
                });
                return false;
            }
        });

        listaContactos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Contacto contacto = (Contacto) listaContactos.getAdapter().getItem(position);

                Intent i = new Intent(ContactListActivity.this, ChatActivity.class);
                i.putExtra("contactName", contacto.getNombre());
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
        DatabaseReference refContactos = FirebaseDatabase.getInstance().getReference("Usuarios");
        List<Contacto> listAdapter = new ArrayList<>();
        List<Contacto> list = new ArrayList<>();
        adaptador = new ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, list);
        listaContactos.setAdapter(adaptador);

        refContactos.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listAdapter.clear();
                list.clear();
                for (DataSnapshot contactoSnapshot : snapshot.getChildren()) {
                    Contacto c = contactoSnapshot.getValue(Contacto.class);
                    if(c != null){
                        String id = contactoSnapshot.getKey();
                        c.setId(id);

                        Log.d("Firebase", "Contacto recibido: " + c.getNombre() + " / Correo: " + c.getEmail() + " / " + id);
                    }

                    String name = c.getNombre();
                    String email = c.getEmail();
                    String id = c.getId();

                    Contacto contacto = new Contacto(name, email = "");
                    Log.d("ContactoAdapter","Objeto para Adapter: " + contacto);
                    Contacto contactoCompleto = new Contacto(name, email, id);
                    list.add(contactoCompleto);
                    Log.d("ContactoCompleto", "Objeto Completo: " + contactoCompleto);
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