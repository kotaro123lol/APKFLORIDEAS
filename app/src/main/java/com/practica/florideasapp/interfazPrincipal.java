package com.practica.florideasapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Models.Productos;

public class interfazPrincipal extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProductoAdapter productoAdapter;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ly_interfazprincipal);

        String correoCliente = getIntent().getStringExtra("correoCliente");
        if (correoCliente != null) {
            // Muestra el correo en los logs o úsalo según tus necesidades
            Log.d("interfazPrincipal", "Correo recibido: " + correoCliente);
            Toast.makeText(this, "Bienvenido " + correoCliente, Toast.LENGTH_SHORT).show();
        } else {
            Log.d("interfazPrincipal", "No se recibió correoCliente");
        }

        // Inicializa Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("Productos"); // Asegúrate de que este sea el nombre correcto

        recyclerView = findViewById(R.id.recyclerViewProductos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Cargar productos desde Firebase
        cargarProductos();
    }

    private void cargarProductos() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Productos> listaProductos = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Productos producto = data.getValue(Productos.class);
                    if (producto != null) {
                        listaProductos.add(producto);
                    }
                }
                if (!listaProductos.isEmpty()) {
                    productoAdapter = new ProductoAdapter(interfazPrincipal.this, listaProductos);
                    recyclerView.setAdapter(productoAdapter);
                } else {
                    Toast.makeText(interfazPrincipal.this, "No se encontraron productos.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(interfazPrincipal.this, "Error al cargar productos: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) searchItem.getActionView();
        searchView.setQueryHint("Buscar productos...");
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchProductos(query);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    cargarProductos();
                } else {
                    searchProductos(newText);
                }
                return true;
            }
        });
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.cart) {  // Aquí se utiliza el ID constante
            Intent intent = new Intent(this, CartActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void searchProductos(String nombre) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Productos> listaProductos = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Productos producto = data.getValue(Productos.class);
                    if (producto != null && producto.getNombre().toLowerCase().contains(nombre.toLowerCase())) {
                        listaProductos.add(producto);
                    }
                }
                if (!listaProductos.isEmpty()) {
                    productoAdapter = new ProductoAdapter(interfazPrincipal.this, listaProductos);
                    recyclerView.setAdapter(productoAdapter);
                } else {
                    Toast.makeText(interfazPrincipal.this, "No se encontraron productos.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(interfazPrincipal.this, "Error al buscar productos: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}