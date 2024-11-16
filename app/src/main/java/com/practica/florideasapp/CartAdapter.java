package com.practica.florideasapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import Models.Cart;
import Models.Productos;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;
    private List<Productos> cartItems;
    private Cart cart;

    public CartAdapter(Context context, List<Productos> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
    }
    public interface OnItemRemovedListener {
        void onItemRemoved();
    }

    private OnItemRemovedListener listener;

    public void setOnItemRemovedListener(OnItemRemovedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Productos producto = cartItems.get(position);

        holder.nombreProducto.setText(producto.getNombre());
        holder.precioProducto.setText("S/ " + producto.getPrecio());

        Glide.with(context)
                .load(producto.getRutaImagen())
                .into(holder.imagenProducto);

        holder.btnRemoveFromCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    cartItems.remove(currentPosition);
                    notifyItemRemoved(currentPosition);
                    notifyItemRangeChanged(currentPosition, cartItems.size());
                    if (listener != null) {
                        listener.onItemRemoved();
                    }
                }
                Toast.makeText(context, "Producto eliminado del carrito", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView nombreProducto, precioProducto;
        ImageView imagenProducto;
        Button btnRemoveFromCart;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreProducto = itemView.findViewById(R.id.nombreProductoCart);
            precioProducto = itemView.findViewById(R.id.precioProductoCart);
            imagenProducto = itemView.findViewById(R.id.imagenProductoCart);
            btnRemoveFromCart = itemView.findViewById(R.id.btnRemoveFromCart);
        }
    }
}