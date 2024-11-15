package com.practica.florideasapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import Models.Productos;

public class OrderDetailsActivity extends AppCompatActivity {

    private TextView txtEstado;
    private TextView txtFechaPedido;
    private TextView txtMensajeEstado;
    private TextView txtNombreCliente;
    private TextView txtProductos;
    private TextView txtTotal;
    private LinearLayout linearLayoutProductos;
    private ImageView iconEstado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        String cartItemsJson = getIntent().getStringExtra("cartItems");
        double totalPrice = getIntent().getDoubleExtra("totalPrice", 0.0);

        Gson gson = new Gson();
        Type type = new TypeToken<List<Productos>>(){}.getType();
        List<Productos> cartItems = gson.fromJson(cartItemsJson, type);

        // Recibir el nombre del cliente desde el Intent
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("correoCliente", "Correo no disponible");
        String userName = sharedPreferences.getString("nombreCliente", "Nombre no disponible");
        String userLastName = sharedPreferences.getString("apellidoCliente", "Apellidos no disponibles");
        Log.d("OrderDetailsActivity", "Correo recibido: " + userEmail);
        Log.d("OrderDetailsActivity", "Nombre recibido: " + userName);
        Log.d("OrderDetailsActivity", "Apellidos recibidos: " + userLastName);

        // Configurar el TextView con el nombre del cliente
        TextView textViewNombreCliente = findViewById(R.id.txtNombreCliente);
        textViewNombreCliente.setText("Cliente: " + userName +" "+ userLastName);

        //inicializar la vistas
        txtEstado = findViewById(R.id.txtEstado);
        txtFechaPedido = findViewById(R.id.txtFechaPedido);
        txtMensajeEstado = findViewById(R.id.txtMensajeEstado);
        txtNombreCliente = findViewById(R.id.txtNombreCliente);
        txtProductos = findViewById(R.id.txtProductos);
        txtTotal = findViewById(R.id.txtTotal);
        linearLayoutProductos = findViewById(R.id.linearLayoutProductos);
        iconEstado = findViewById(R.id.iconEstado);

        String[] estados = {"Recibimos tu pedido", "En preparación", "En camino"};
        Random random = new Random();
        String estadoAleatorio = estados[random.nextInt(estados.length)];

        txtEstado.setText("Estado: " + estadoAleatorio);

        Map<String, Integer> estadoIconMap = new HashMap<>();
        estadoIconMap.put("Recibimos tu pedido", R.drawable.ic_recived);
        estadoIconMap.put("En preparación", R.drawable.ic_en_preparacion);
        estadoIconMap.put("En camino", R.drawable.ic_en_camino);

        Integer iconResId = estadoIconMap.get(estadoAleatorio);
        iconEstado.setImageResource(0);
        if (iconResId != null) {
            iconEstado.setImageResource(iconResId);
        } else {
            // Set a default icon if the status is not found in the map
            iconEstado.setImageResource(R.drawable.ic_recived);
        }

        // Obtener la fecha del pedido desde SharedPreferences
        SharedPreferences sharedPreference = getSharedPreferences("OrderPrefs", MODE_PRIVATE);
        String orderDate = sharedPreference.getString("orderDate", "Fecha no disponible");

        txtFechaPedido.setText("Fecha del pedido: " + orderDate);
        txtMensajeEstado.setText("Tu pedido está en: "+ estadoAleatorio);
        txtNombreCliente.setText("Cliente: " + userName +" "+ userLastName);
        txtTotal.setText("Total: S/ " + totalPrice);

        //mostrar los productos
        linearLayoutProductos.removeAllViews();
        for (Productos item : cartItems) {
            TextView productTextView = new TextView(this);
            productTextView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            productTextView.setText(item.getNombre());
            productTextView.setTextSize(16);
            productTextView.setTextColor(getResources().getColor(android.R.color.black));
            linearLayoutProductos.addView(productTextView);
        }
    }
}