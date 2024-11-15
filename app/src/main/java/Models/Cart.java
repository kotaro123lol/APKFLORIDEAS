package Models;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private static Cart instance;
    private List<Productos> items;

    private Cart() {
        items = new ArrayList<>();
    }

    public static Cart getInstance() {
        if (instance == null) {
            instance = new Cart();
        }
        return instance;
    }

    public void addItem(Productos producto) {
        items.add(producto);
    }

    public List<Productos> getItems() {
        return items;
    }

    public double getTotalPrice() {
        double total = 0.0;
        for (Productos producto : items) {
            try {
                total += Double.parseDouble(producto.getPrecio()); // Convertir a double
            } catch (NumberFormatException e) {
                Log.e("Cart", "Precio inválido para el producto: " + producto.getNombre());
            }
        }
        return total;
    }

    public void clearCart() {
        items.clear();
    }
}
