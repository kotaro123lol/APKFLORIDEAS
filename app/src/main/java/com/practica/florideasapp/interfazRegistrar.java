package com.practica.florideasapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import Models.Cliente;
import android.os.Bundle;
import android.widget.Toast;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.UUID;

public class interfazRegistrar extends AppCompatActivity {
    EditText txtNombre, txtCorreo, txtContraseña, txtApellidos, txtDNI, txtDireccion, txtTelefono;
    DatabaseReference databaseReference;
    Button btnRegistrar;
    FirebaseDatabase firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ly_interfazregistro);

        iniciarFirebase();
        txtNombre = findViewById(R.id.txtNombres);
        txtApellidos = findViewById(R.id.txtApellidos);
        txtDNI = findViewById(R.id.txtDNI);
        txtDireccion = findViewById(R.id.txtDireccion);
        txtTelefono = findViewById(R.id.txtTelefono);
        txtCorreo = findViewById(R.id.txtCorreo);
        txtContraseña = findViewById(R.id.txtContraseña);
        btnRegistrar = findViewById(R.id.btnRegistrar);

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean validar = validarCajasTexto();
                if (validar) return;
                registrarCliente();
            }
        });
    }

    private void registrarCliente() {
        String id = UUID.randomUUID().toString();
        String nombres = txtNombre.getText().toString().trim();
        String apellidos = txtApellidos.getText().toString().trim();
        String dni = txtDNI.getText().toString().trim();
        String direccion = txtDireccion.getText().toString().trim();
        String telefono = txtTelefono.getText().toString().trim();
        String correo = txtCorreo.getText().toString().trim();
        String contrasena = txtContraseña.getText().toString().trim();

        if (nombres.isEmpty() || correo.isEmpty() || contrasena.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        Cliente cliente = new Cliente(id, nombres, apellidos, dni, direccion, telefono, correo, contrasena);

        databaseReference.child("Clientes").child(id).setValue(cliente)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(interfazRegistrar.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(interfazRegistrar.this, "Error en el registro", Toast.LENGTH_SHORT).show();
                });
    }

    private void iniciarFirebase() {
        FirebaseApp.initializeApp(this);
        firebase = FirebaseDatabase.getInstance();
        databaseReference = firebase.getReference();
    }

    private boolean validarCajasTexto() {
        if (txtNombre.getText().toString().trim().isEmpty() ||
                txtApellidos.getText().toString().trim().isEmpty() ||
                txtDNI.getText().toString().trim().isEmpty() ||
                txtDireccion.getText().toString().trim().isEmpty() ||
                txtTelefono.getText().toString().trim().isEmpty() ||
                txtCorreo.getText().toString().trim().isEmpty() ||
                txtContraseña.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            return false;
        }
    }
}
