package com.practica.florideasapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import Models.Cart;

public class CartActivity extends AppCompatActivity {

    private Cart cart;
    private RecyclerView recyclerViewCart;
    private TextView textTotalPrice;
    private Button btnConfirmPurchase;
    String SECRET_KEY="sk_test_51Q7areRu8FS2rmDOuOTGFqpXIXNrJIHOSUTInl9NXxLatkDWY8cZuddAQ2vG5fvQMndqN1o8K9sR0pdqKnDVdbjl00RJJjv8gn";
    String SECRET_PUBLIC="pk_test_51Q7areRu8FS2rmDO6dIqC4f0JfTHnZEOclrhMEGDpTg0LZijJiX2hZDy5dx9YPX1GWdwyuYytTQedhDD9ivoBvcs0047XFY4Yf";
    PaymentSheet paymentSheet;
    private Button btnPurchaseStatus;

    String customerID;
    String Ephericalkey;
    String clientSecret;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cart = Cart.getInstance();

        recyclerViewCart = findViewById(R.id.recyclerViewCart);
        textTotalPrice = findViewById(R.id.textTotalPrice);
        btnConfirmPurchase = findViewById(R.id.btnConfirmPurchase);

        CartAdapter cartAdapter = new CartAdapter(this, cart.getItems());
        recyclerViewCart.setAdapter(cartAdapter);
        recyclerViewCart.setLayoutManager(new LinearLayoutManager(this));

        btnPurchaseStatus = findViewById(R.id.btnPurchaseStatus);

        SharedPreferences sharedPreferences = getSharedPreferences("CartPrefs", MODE_PRIVATE);
        boolean isPurchaseStatusVisible = sharedPreferences.getBoolean("isPurchaseStatusVisible", false);
        btnPurchaseStatus.setVisibility(isPurchaseStatusVisible ? View.VISIBLE : View.GONE);

        btnPurchaseStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(CartActivity.this, OrderDetailsActivity.class);
                Gson gson = new Gson();
                String items = gson.toJson(cart.getItems());
                intent.putExtra("cartItems", items);
                intent.putExtra("totalPrice", cart.getTotalPrice());
                startActivity(intent);
            }
        });

        textTotalPrice.setText("Total: S/ " + cart.getTotalPrice());

        PaymentConfiguration.init(this,SECRET_PUBLIC);
        paymentSheet = new PaymentSheet(this,paymentSheetResult -> {
            onPaymentResult(paymentSheetResult);
        });

        btnConfirmPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PaymentFlow();
            }
        });

        cartAdapter.setOnItemRemovedListener(new CartAdapter.OnItemRemovedListener() {
            @Override
            public void onItemRemoved() {
                actualizarTotal(cart.getTotalPrice());
            }
        });

        StringRequest stringRequest = new StringRequest(Request.Method.POST,"https://api.stripe.com/v1/customers",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object=new JSONObject(response);
                            customerID=object.getString("id");
                            getEphericalkey(customerID);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CartActivity.this, "Error al crear el cliente: " + error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> header=new HashMap<>();
                header.put("Authorization", "Bearer " + SECRET_KEY);

                return header;
            }
        };
        Volley.newRequestQueue(this).add(stringRequest);

    }

    private void onPaymentResult(PaymentSheetResult paymentSheetResult) {
        if(paymentSheetResult instanceof PaymentSheetResult.Completed){
            Toast.makeText(this,"payment succes", Toast.LENGTH_SHORT).show();

            // Obtenenemos la fecha actual
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String currentDate = dateFormat.format(calendar.getTime());
            Log.d("CurrentDate", "Current Date: " + currentDate);

            // Guardar la fecha en SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("OrderPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("orderDate", currentDate);
            editor.apply();

            // Serializar los items del carrito
            Gson gson = new Gson();
            String items = gson.toJson(cart.getItems());

            // Crear un intent para ir a la siguiente actividad
            Intent intent = new Intent(this, OrderDetailsActivity.class);
            intent.putExtra("cartItems", items);
            intent.putExtra("totalPrice", cart.getTotalPrice());
            intent.putExtra("orderDate", currentDate);
            startActivity(intent);

            // Mostrar el botón de estado de compra
            btnPurchaseStatus.setVisibility(View.VISIBLE);

            // Guardar el estado del botón en SharedPreferences
            SharedPreferences cartPrefs = getSharedPreferences("CartPrefs", MODE_PRIVATE);
            SharedPreferences.Editor cartEditor = cartPrefs.edit();
            cartEditor.putBoolean("isPurchaseStatusVisible", true);
            cartEditor.apply();
        }
    }

    private void getEphericalkey(String customerID) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST,"https://api.stripe.com/v1/ephemeral_keys",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object=new JSONObject(response);

                            Ephericalkey=object.getString("id");
                            getClientSecret(customerID,Ephericalkey);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CartActivity.this, "Error al obtener el ephemeral key: " + error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + SECRET_KEY); // Clave secreta de Stripe
                headers.put("Stripe-Version", "2024-09-30.acacia"); // Versión de la API de Stripe
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("customer",customerID);
                return params;
            }
        };
        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void getClientSecret(String customerID, String ephericalkey) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST,"https://api.stripe.com/v1/payment_intents",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object=new JSONObject(response);
                            clientSecret=object.getString("client_secret");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CartActivity.this, "Error al obtener el Client Secret", Toast.LENGTH_SHORT).show();

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                // Colocamos los headers correctos
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + SECRET_KEY); // Agrega tu Stripe Secret Key aquí
                headers.put("Stripe-Version", "2024-09-30.acacia"); // Versión de la API de Stripe
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Aquí definimos los parámetros de la solicitud
                Map<String, String> params = new HashMap<>();
                params.put("customer", customerID);
                // Calcula el total en dólares usando getTotalPrice desde Cart y lo convierte a centavos
                double totalPriceInCents = cart.getTotalPrice() * 100;
                params.put("amount", String.valueOf((int) totalPriceInCents)); // Convierte a String para la solicitud
                params.put("currency", "pen");
                params.put("automatic_payment_methods[enabled]", "true");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(CartActivity.this);
        requestQueue.add(stringRequest);
    }

private void PaymentFlow() {
    if (customerID != null && !customerID.isEmpty() && Ephericalkey != null && !Ephericalkey.isEmpty()) {
        paymentSheet.presentWithPaymentIntent(
                clientSecret,
                new PaymentSheet.Configuration(
                        "abc Company",
                        new PaymentSheet.CustomerConfiguration(
                                customerID,
                                Ephericalkey
                        )
                )
        );
    } else {
        Toast.makeText(CartActivity.this, "Customer ID o Ephemeral Key están vacíos", Toast.LENGTH_LONG).show();
    }
}

    public void actualizarTotal(double totalPrice) {
        textTotalPrice.setText("Total: S/ " + totalPrice);
    }

}
