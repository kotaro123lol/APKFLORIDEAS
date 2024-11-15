package com.practica.florideasapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import Models.Cliente;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase firebase;
    DatabaseReference databaseReference;
    EditText txtCorreo, txtContrasena;
    Button btnIngresar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtCorreo = findViewById(R.id.txtUsuario);
        txtContrasena = findViewById(R.id.txtClave);
        btnIngresar = findViewById(R.id.btnIngresar);

        // Inicializa Firebase
        iniciarFirebase();

        btnIngresar.setOnClickListener(this::onIngresar);
    }
    public void onIngresar(View view) {
        String correo = txtCorreo.getText().toString().trim();
        String contrasena = txtContrasena.getText().toString().trim();
        if (correo.isEmpty() || contrasena.isEmpty()) {
            Toast.makeText(this, "Por favor, ingrese correo y contraseña", Toast.LENGTH_SHORT).show();
            return;
        }
        // Llamar a la función para validar el cliente
        validarCliente(correo, contrasena);
    }

    private void validarCliente(String correo, String contrasena) {
        Log.d("LoginDebug", "Correo ingresado: " + correo);

        databaseReference.orderByChild("correo").equalTo(correo)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            if (snapshot.exists()) {
                                Log.d("LoginDebug", "Correo encontrado en la base de datos.");
                                boolean contrasenaCorrecta = false;

                                for (DataSnapshot data : snapshot.getChildren()) {
                                    Cliente cliente = data.getValue(Cliente.class);
                                    if (cliente != null) {
                                        Log.d("LoginDebug", "Contraseña en Firebase: " + cliente.getContrasena());
                                        if (cliente.getContrasena().equals(contrasena)) {
                                            contrasenaCorrecta = true;
                                            Intent intent = new Intent(MainActivity.this, interfazPrincipal.class);

                                            Log.d("LoginDebug", "Correo enviado: " + correo);
                                            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString("correoCliente", correo);
                                            editor.putString("nombreCliente", cliente.getNombres());
                                            editor.putString("apellidoCliente", cliente.getApellidos());// Store the username
                                            editor.apply();
                                            editor.apply();
                                            startActivity(intent);

                                            break;
                                        }
                                    } else {
                                        Log.e("LoginError", "Cliente es nulo");
                                    }
                                }

                                if (!contrasenaCorrecta) {
                                    Toast.makeText(MainActivity.this, "Contraseña incorrecta", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Log.d("LoginDebug", "Correo no encontrado en la base de datos.");
                                Toast.makeText(MainActivity.this, "Correo no registrado", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Log.e("LoginError", "Error al validar el cliente: ", e);
                            Toast.makeText(MainActivity.this, "Ocurrió un error inesperado: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("DatabaseError", "Error en la base de datos: ", error.toException());
                        Toast.makeText(MainActivity.this, "Error en la base de datos: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void onRegistrar(View view) {
        Intent intent = new Intent(this, interfazRegistrar.class);
        startActivity(intent);
    }

    private void iniciarFirebase() {
        FirebaseApp.initializeApp(this);
        firebase = FirebaseDatabase.getInstance();
        databaseReference = firebase.getReference().child("Clientes");
    }
}
